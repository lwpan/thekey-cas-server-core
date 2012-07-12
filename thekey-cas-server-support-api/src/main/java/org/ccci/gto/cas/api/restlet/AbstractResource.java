package org.ccci.gto.cas.api.restlet;

import org.ccci.gto.cas.api.ApiController;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractResource extends Resource {
    @Autowired
    private ApiController apiController;

    private TheKeyRegisteredService service = null;

    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);

        // lookup the RegisteredService with the specified apiKey
        this.service = apiController.lookupServiceByApiKey((String) getRequest().getAttributes().get("apiKey"));
    }

    /**
     * @return the apiController
     */
    protected ApiController getApiController() {
        return apiController;
    }

    protected TheKeyRegisteredService getRegisteredService() {
        return this.service;
    }
}
