package org.ccci.gto.cas.oauth.api.restlet;

import static org.ccci.gto.cas.oauth.Constants.PARAM_ACCESS_TOKEN;

import org.ccci.gto.cas.oauth.authentication.OAuth2Credentials;
import org.restlet.data.Status;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

public abstract class AbstractProtectedResource extends Resource {
    private String access_token = null;

    protected final String getAccessToken() throws ResourceException {
        // lookup the access token
        if (access_token == null) {
            // check for the access_token as an URI query parameter
            final String getParam = this.getQuery().getFirstValue(PARAM_ACCESS_TOKEN);
            if (getParam != null) {
                if (access_token != null) {
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                            "access_token transmitted via more than 1 method");
                }

                access_token = getParam;
            }

            // TODO: check for a form-encoded body parameter

            // TODO: check for Bearer or OAuth Authorization header
        }

        if (access_token == null) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "Unable to find an access_token");
        }

        return access_token;
    }

    protected final OAuth2Credentials getCredentials() throws ResourceException {
        // Generate and return base OAuth2 credentials
        final OAuth2Credentials credentials = new OAuth2Credentials();
        credentials.setRawToken(this.getAccessToken());
        return credentials;
    }
}
