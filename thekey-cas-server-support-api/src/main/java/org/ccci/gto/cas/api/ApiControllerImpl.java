package org.ccci.gto.cas.api;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_RELAYGUID;
import static org.ccci.gto.cas.api.Constants.API_ATTRIBUTES;
import static org.ccci.gto.cas.api.Constants.API_LINKEDIDENTITIES;
import static org.ccci.gto.cas.api.Constants.PARAM_EMAIL;
import static org.ccci.gto.cas.api.Constants.PARAM_FACEBOOKID;
import static org.ccci.gto.cas.api.Constants.PARAM_GUID;
import static org.ccci.gto.cas.api.Constants.PARAM_RELAYGUID;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.github.inspektr.audit.annotation.Audit;

public final class ApiControllerImpl implements ApiController {
    @NotNull
    private ServicesManager servicesManager;

    @NotNull
    private GcxUserService userService;

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
    public void setUserService(final GcxUserService userService) {
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

        // lookup the requested user
        final GcxUser user = this.findUser(query);

        // look up the user attributes
        final Map<String, Object> attributes = new HashMap<String, Object>();
        if (!service.isIgnoreAttributes()) {
            for (final String name : service.getAllowedAttributes()) {
                // get the value of the current attribute
                final Object value;
                if (PRINCIPAL_ATTR_GUID.equals(name)) {
                    value = user.getGUID();
                } else if (PRINCIPAL_ATTR_ADDITIONALGUIDS.equals(name)) {
                    value = user.getGUIDAdditional();
                } else if (PRINCIPAL_ATTR_FACEBOOKID.equals(name)) {
                    value = user.getFacebookId();
                } else if (PRINCIPAL_ATTR_RELAYGUID.equals(name)) {
                    value = user.getRelayGuid();
                } else if (PRINCIPAL_ATTR_EMAIL.equals(name)) {
                    value = user.getEmail();
                } else if (PRINCIPAL_ATTR_FIRSTNAME.equals(name)) {
                    value = user.getFirstName();
                } else if (PRINCIPAL_ATTR_LASTNAME.equals(name)) {
                    value = user.getLastName();
                } else {
                    value = null;
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
