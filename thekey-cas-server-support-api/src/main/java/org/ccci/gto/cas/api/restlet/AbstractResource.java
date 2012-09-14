package org.ccci.gto.cas.api.restlet;

import static org.ccci.gto.cas.api.Constants.PARAM_EMAIL;
import static org.ccci.gto.cas.api.Constants.PARAM_FACEBOOKID;
import static org.ccci.gto.cas.api.Constants.PARAM_GUID;

import java.util.HashMap;
import java.util.Map;

import org.ccci.gto.cas.api.ApiController;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractResource extends Resource {
    @Autowired
    private ApiController apiController;

    private TheKeyRegisteredService service = null;

    private Map<String, String> params;

    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);

        // lookup the RegisteredService with the specified apiKey
        this.service = apiController.lookupServiceByApiKey((String) getRequest().getAttributes().get("apiKey"));

        // extract the query params from the request
        final Form query = request.getResourceRef().getQueryAsForm();
        this.params = new HashMap<String, String>();
        this.params.put(PARAM_GUID, query.getFirstValue(PARAM_GUID));
        this.params.put(PARAM_EMAIL, query.getFirstValue(PARAM_EMAIL));
        this.params.put(PARAM_FACEBOOKID, query.getFirstValue(PARAM_FACEBOOKID));
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

    protected Map<String, String> getQueryParams() {
        return this.params;
    }
}
