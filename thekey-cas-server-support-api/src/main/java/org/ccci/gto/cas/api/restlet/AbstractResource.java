package org.ccci.gto.cas.api.restlet;

import org.apache.commons.lang.StringUtils;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractResource extends Resource {
    @Autowired
    private ServicesManager servicesManager;

    private TheKeyRegisteredService service = null;

    private ResourceException error = null;

    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);

        // lookup the RegisteredService with the specified apiKey
        final String apiKey = (String) getRequest().getAttributes().get("apiKey");
        if (StringUtils.isNotBlank(apiKey)) {
            for (final RegisteredService service : servicesManager.getAllServices()) {
                if (service instanceof TheKeyRegisteredService && ((TheKeyRegisteredService) service).isApiEnabled()
                        && apiKey.equals(((TheKeyRegisteredService) service).getApiKey())) {
                    this.service = (TheKeyRegisteredService) service;
                    break;
                }
            }
        }
    }

    protected TheKeyRegisteredService getRegisteredService() {
        return this.service;
    }

    protected void assertAuthorized() throws ResourceException {
        if (this.service == null) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
        }
    }

    protected void setError(final ResourceException error) {
        this.error = error;
    }

    protected void assertValidRequest() throws ResourceException {
        if (this.error != null) {
            throw this.error;
        }
    }
}
