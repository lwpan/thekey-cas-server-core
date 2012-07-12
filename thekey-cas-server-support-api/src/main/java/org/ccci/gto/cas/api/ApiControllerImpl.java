package org.ccci.gto.cas.api;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;

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
    @Audit(applicationCode = "THEKEY", action = "API_GET_USER_ATTRIBUTES", actionResolverName = "THEKEY_API_ACTION_RESOLVER", resourceResolverName = "THEKEY_API_GET_USER_ATTRIBUTES_RESOURCE_RESOLVER")
    public Map<String, Object> getUserAttributes(final TheKeyRegisteredService service, final String guid,
            final String email) throws ResourceException {
        this.assertAuthorized(service);

        // lookup the requested user
        final GcxUser user;
        if (StringUtils.isNotBlank(guid) && StringUtils.isNotBlank(email)) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                    "Cannot search for an email and guid at the same time");
        } else if (StringUtils.isNotBlank(guid)) {
            user = this.userService.findUserByGuid(guid);
            if (user == null) {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "guid \""
                        + StringEscapeUtils.escapeHtml(guid) + "\" was not found");
            }
        } else if (StringUtils.isNotBlank(email)) {
            user = this.userService.findUserByEmail(email);
            if (user == null) {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "email \""
                        + StringEscapeUtils.escapeHtml(email) + "\" was not found");
            }
        } else {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "no guid or email was specified");
        }

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

    private void assertAuthorized(final TheKeyRegisteredService service) throws ResourceException {
        if (service == null || !service.isApiEnabled()) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "The specified API Key is not valid");
        }
    }
}
