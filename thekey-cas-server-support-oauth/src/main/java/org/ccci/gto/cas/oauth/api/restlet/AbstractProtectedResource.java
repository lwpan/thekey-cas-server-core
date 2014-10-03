package org.ccci.gto.cas.oauth.api.restlet;

import static org.ccci.gto.cas.oauth.Constants.ERROR_BEARER_INVALID_REQUEST;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ACCESS_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ERROR;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ERROR_DESCRIPTION;
import static org.ccci.gto.cas.oauth.api.restlet.Constants.HTTP_OAUTH_BEARER;
import static org.restlet.data.ChallengeScheme.HTTP_OAUTH;

import me.thekey.cas.oauth.server.authentication.OAuth2ServerCredentials;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

public abstract class AbstractProtectedResource extends Resource {

    private String access_token = null;

    protected final String getAccessToken() throws ResourceException {
        // lookup the access token
        if (this.access_token == null) {
            int tokensSeen = 0;
            // check for Bearer or OAuth Authorization header
            final ChallengeResponse auth = this.getRequest().getChallengeResponse();
            if (auth != null) {
                final ChallengeScheme scheme = auth.getScheme();
                // RFC 6750
                if (HTTP_OAUTH_BEARER.equals(scheme)) {
                    this.access_token = auth.getCredentials();
                    tokensSeen++;
                } else if (HTTP_OAUTH.equals(scheme)) {
                    // TODO: find the spec that defines the OAuth
                    // ChallengeScheme
                }
            }

            // check for the access_token as an URI query parameter
            final String getParam = this.getQuery().getFirstValue(PARAM_ACCESS_TOKEN);
            if (getParam != null) {
                this.access_token = getParam;
                tokensSeen++;
            }

            // TODO: check for a form-encoded body parameter

            if (tokensSeen > 1) {
                sendChallengeRequest(ERROR_BEARER_INVALID_REQUEST, "access_token transmitted via more than 1 method");
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        }

        if (this.access_token == null) {
            sendChallengeRequest(null);
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
        }

        return this.access_token;
    }

    protected final OAuth2ServerCredentials getCredentials() throws ResourceException {
        // Generate and return base OAuth2 credentials
        final OAuth2ServerCredentials credentials = new OAuth2ServerCredentials();
        credentials.setRawToken(this.getAccessToken());
        return credentials;
    }

    protected final void sendChallengeRequest(final String error) {
        sendChallengeRequest(error, null);
    }

    protected final void sendChallengeRequest(final String error, final String description) {
        // set a ChallengeRequest on the response
        final ChallengeRequest challenge = new ChallengeRequest(HTTP_OAUTH_BEARER, null);
        if (error != null) {
            challenge.getParameters().add(new Parameter(PARAM_ERROR, error));
        }
        if (description != null) {
            challenge.getParameters().add(new Parameter(PARAM_ERROR_DESCRIPTION, description));
        }
        this.getResponse().setChallengeRequest(challenge);
    }
}
