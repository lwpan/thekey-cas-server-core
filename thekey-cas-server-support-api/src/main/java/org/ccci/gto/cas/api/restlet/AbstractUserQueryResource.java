package org.ccci.gto.cas.api.restlet;

import static org.ccci.gto.cas.api.Constants.PARAM_EMAIL;
import static org.ccci.gto.cas.api.Constants.PARAM_FACEBOOKID;
import static org.ccci.gto.cas.api.Constants.PARAM_GUID;
import static org.ccci.gto.cas.api.Constants.PARAM_RELAYGUID;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.restlet.data.Response;

import java.util.HashMap;
import java.util.Map;

public class AbstractUserQueryResource extends AbstractResource {
    protected final Map<String, String> params = new HashMap<>();

    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);

        // extract the query params from the request
        final Form query = request.getResourceRef().getQueryAsForm();
        this.params.put(PARAM_GUID, query.getFirstValue(PARAM_GUID));
        this.params.put(PARAM_EMAIL, query.getFirstValue(PARAM_EMAIL));
        this.params.put(PARAM_RELAYGUID, query.getFirstValue(PARAM_RELAYGUID));
        this.params.put(PARAM_FACEBOOKID, query.getFirstValue(PARAM_FACEBOOKID));
    }
}
