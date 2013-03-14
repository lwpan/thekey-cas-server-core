package org.ccci.gto.cas.oauth.api.restlet;

import static org.ccci.gto.cas.oauth.Constants.ERROR_UNSUPPORTED_GRANT_TYPE;
import static org.ccci.gto.cas.oauth.Constants.GRANT_TYPE_CODE;
import static org.ccci.gto.cas.oauth.Constants.GRANT_TYPE_REFRESH_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ERROR;
import static org.ccci.gto.cas.oauth.Constants.PARAM_GRANT_TYPE;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.restlet.data.Form;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TokenResource extends Resource {
    private final static Logger LOG = LoggerFactory.getLogger(TokenResource.class);

    public interface Support {
        public Map<Object, Object> processCodeGrant(TokenResource resource);

        public Map<Object, Object> processRefreshTokenGrant(TokenResource resource);
    }

    @Autowired
    @NotNull
    private Support support;

    @Override
    public void acceptRepresentation(final Representation entity) throws ResourceException {
        final Form params = this.getRequest().getEntityAsForm();
        final String grantType = params.getFirstValue(PARAM_GRANT_TYPE, "");
        final Map<Object, Object> resp = new HashMap<Object, Object>();

        // switch based on grant_type
        LOG.debug("switching request based on grant_type: {}", grantType);
        switch (grantType) {
        case GRANT_TYPE_CODE:
            resp.putAll(this.support.processCodeGrant(this));
            break;
        case GRANT_TYPE_REFRESH_TOKEN:
            resp.putAll(this.support.processRefreshTokenGrant(this));
            break;
        default:
            resp.putAll(this.oauthError(ERROR_UNSUPPORTED_GRANT_TYPE));
        }

        // generate a response
        LOG.debug("generating a response");
        final Response response = this.getResponse();
        final Form headers = new Form();
        headers.set("Cache-Control", "no-store", true);
        headers.set("Pragma", "no-cache", true);
        response.getAttributes().put("org.restlet.http.headers", headers);
        response.setEntity(new JsonRepresentation(resp));
    }

    public Map<Object, Object> oauthError(final String error) {
        LOG.debug("returning OAuth error: {}", error);
        this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        final Map<Object, Object> resp = new HashMap<Object, Object>();
        resp.put(PARAM_ERROR, error);
        return resp;
    }

    @Override
    public boolean allowPost() {
        return true;
    }

    @Override
    public boolean allowOptions() {
        return false;
    }

    @Override
    public boolean isModifiable() {
        return false;
    }

    @Override
    public boolean isReadable() {
        return false;
    }
}
