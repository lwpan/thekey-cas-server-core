package me.thekey.cas.api;

import static org.ccci.gto.cas.api.Constants.API_ATTRIBUTES;
import static org.ccci.gto.cas.api.Constants.API_FEDERATEDLOGIN;
import static org.ccci.gto.cas.api.Constants.API_LINKEDIDENTITIES;
import static org.ccci.gto.cas.api.Constants.PARAM_EMAIL;
import static org.ccci.gto.cas.api.Constants.PARAM_FACEBOOKID;
import static org.ccci.gto.cas.api.Constants.PARAM_GUID;
import static org.ccci.gto.cas.api.Constants.PARAM_RELAYGUID;

import com.github.inspektr.audit.annotation.Audit;
import com.google.common.collect.ListMultimap;
import me.thekey.cas.service.UserManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ApiControllerImpl implements ApiController {
    @NotNull
    private AuthenticationManager authenticationManager;

    @NotNull
    private ServicesManager servicesManager;

    @NotNull
    private UserManager userService;

    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * @param servicesManager
     *            the servicesManager to set
     */
    public void setServicesManager(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }

    @Override
    public TheKeyRegisteredService lookupServiceByApiKey(final String apiKey) {
        if (StringUtils.isNotBlank(apiKey)) {
            for (final RegisteredService service : this.servicesManager.getAllServices()) {
                if (service instanceof TheKeyRegisteredService
                        && apiKey.equals(((TheKeyRegisteredService) service).getApiKey())) {
                    return (TheKeyRegisteredService) service;
                }
            }
        }

        return null;
    }

    @Override
    @Audit(applicationCode = "THEKEY", action = "API_FEDERATED_LOGIN",
            actionResolverName = "THEKEY_API_ACTION_RESOLVER",
            resourceResolverName = "THEKEY_API_FEDERATED_LOGIN_RESOURCE_RESOLVER")
    public Map<String, Object> federatedLogin(final TheKeyRegisteredService service,
                                              final Credentials credentials) throws ResourceException {
        this.assertAuthorized(service, API_FEDERATEDLOGIN);

        // authenticate provided credentials
        final Authentication auth;
        try {
            auth = this.authenticationManager.authenticate(credentials);
        } catch (final AuthenticationException e) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
        }

        // return the attributes for the specified user
        return this.getUserAttributes(AuthenticationUtil.getUser(auth), service);
    }

    @Override
    @Audit(applicationCode = "THEKEY", action = "API_GET_LINKED_IDENTITIES", actionResolverName = "THEKEY_API_ACTION_RESOLVER", resourceResolverName = "THEKEY_API_GET_LINKED_IDENTITIES_RESOURCE_RESOLVER")
    public Map<String, Identity> getLinkedIdentities(final TheKeyRegisteredService service,
            final Map<String, String> query) throws ResourceException {
        this.assertAuthorized(service, API_LINKEDIDENTITIES);

        // lookup the requested user
        final GcxUser user = this.findUser(query);

        // generate the identity map
        final Map<String, Identity> identities = new HashMap<String, Identity>();
        identities.put("guid", new Identity(user.getGUID(), 1.0));
        identities.put("email", new Identity(user.getEmail(), (user.isVerified() ? 1.0 : 0.25)));
        final String facebookId = user.getFacebookId();
        if (StringUtils.isNotBlank(facebookId)) {
            identities.put("facebookId", new Identity(facebookId, 0.25));
        }
        final String relayGuid = user.getRelayGuid();
        if (StringUtils.isNotBlank(relayGuid)) {
            identities.put("relayGuid", new Identity(relayGuid, user.getRelayGuidStrengthFor(relayGuid)));
        }

        // return the identity map
        return identities;
    }

    @Override
    @Audit(applicationCode = "THEKEY", action = "API_GET_USER_ATTRIBUTES", actionResolverName = "THEKEY_API_ACTION_RESOLVER", resourceResolverName = "THEKEY_API_GET_USER_ATTRIBUTES_RESOURCE_RESOLVER")
    public Map<String, Object> getUserAttributes(final TheKeyRegisteredService service, final Map<String, String> query)
            throws ResourceException {
        this.assertAuthorized(service, API_ATTRIBUTES);
        return this.getUserAttributes(this.findUser(query), service);
    }

    private Map<String, Object> getUserAttributes(final GcxUser user, final TheKeyRegisteredService service) {
        if (user == null) {
            return Collections.emptyMap();
        }

        // look up the user attributes
        final Map<String, Object> attributes = new HashMap<>();
        if (!service.isIgnoreAttributes()) {
            final ListMultimap<String, String> attrs = userService.getUserAttributes(user);

            for (final String name : service.getAllowedAttributes()) {
                final Object value;
                final List<String> vals = attrs.get(name);
                switch (vals.size()) {
                    case 0:
                        continue;
                    case 1:
                        value = vals.get(0);
                        break;
                    default:
                        value = new ArrayList<>(vals);
                        break;
                }

                // only store attributes that exist
                if (value != null) {
                    attributes.put(name, value);
                }
            }
        }

        return attributes;
    }

    private void assertAuthorized(final TheKeyRegisteredService service, final String type) throws ResourceException {
        if (service == null || !service.isApiSupported(type)) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,
                    "The specified API Key is not valid, or the requested API is not supported");
        }
    }

    private GcxUser findUser(final Map<String, String> query) throws ResourceException {
        final GcxUser user;
        int count = 0;
        for (final String param : new String[] { PARAM_GUID, PARAM_EMAIL, PARAM_RELAYGUID, PARAM_FACEBOOKID }) {
            if (StringUtils.isNotBlank(query.get(param))) {
                count++;
            }
        }
        if (count > 1) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                    "Cannot search for more than 1 type of meta-data at once");
        } else if (StringUtils.isNotBlank(query.get(PARAM_GUID))) {
            final String guid = query.get(PARAM_GUID);
            user = this.userService.findUserByGuid(guid);
            if (user == null) {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "guid \""
                        + StringEscapeUtils.escapeHtml(guid) + "\" was not found");
            }
        } else if (StringUtils.isNotBlank(query.get(PARAM_EMAIL))) {
            final String email = query.get(PARAM_EMAIL);
            user = this.userService.findUserByEmail(email);
            if (user == null) {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "email \""
                        + StringEscapeUtils.escapeHtml(email) + "\" was not found");
            }
        } else if (StringUtils.isNotBlank(query.get(PARAM_RELAYGUID))) {
            final String guid = query.get(PARAM_RELAYGUID);
            user = this.userService.findUserByRelayGuid(guid);
            if (user == null) {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "relayGuid \""
                        + StringEscapeUtils.escapeHtml(guid) + "\" was not found");
            }
        } else if (StringUtils.isNotBlank(query.get(PARAM_FACEBOOKID))) {
            final String facebookId = query.get(PARAM_FACEBOOKID);
            user = this.userService.findUserByFacebookId(facebookId);
            if (user == null) {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "facebookId \""
                        + StringEscapeUtils.escapeHtml(facebookId) + "\" was not found");
            }
        } else {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "no valid search terms were specified");
        }

        return user;
    }
}
