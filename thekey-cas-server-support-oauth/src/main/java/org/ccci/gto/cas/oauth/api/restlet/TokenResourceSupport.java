package org.ccci.gto.cas.oauth.api.restlet;

import static org.ccci.gto.cas.oauth.Constants.ERROR_INVALID_GRANT;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ACCESS_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_CLIENT_ID;
import static org.ccci.gto.cas.oauth.Constants.PARAM_CODE;
import static org.ccci.gto.cas.oauth.Constants.PARAM_REDIRECT_URI;
import static org.ccci.gto.cas.oauth.Constants.PARAM_REFRESH_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_SCOPE;
import static org.ccci.gto.cas.oauth.Constants.PARAM_TOKEN_TYPE;
import static org.ccci.gto.cas.oauth.Constants.TOKEN_TYPE_BEARER;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.oauth.OAuthManager;
import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.RefreshToken;
import org.ccci.gto.persistence.DeadLockRetry;
import org.restlet.data.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TokenResourceSupport implements TokenResource.Support {
    @Autowired
    @NotNull
    private OAuthManager oauthManager;

    public void setOAuthManager(final OAuthManager oauthManager) {
        this.oauthManager = oauthManager;
    }

    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<Object, Object> processCodeGrant(final TokenResource resource) {
        Code code = null;
        try {
            final Form params = resource.getRequest().getEntityAsForm();

            // fetch the client and code
            final Client client = this.oauthManager.getClient(params.getFirstValue(PARAM_CLIENT_ID));
            code = this.oauthManager.getCode(params.getFirstValue(PARAM_CODE));

            // return an error if this is an invalid request
            if (client == null || code == null || code.isExpired() || !client.getId().equals(code.getClient().getId())) {
                // return an invalid_grant error
                return resource.oauthError(ERROR_INVALID_GRANT);
            }

            // validate the redirect_uri
            final String redirectUri = params.getFirstValue(PARAM_REDIRECT_URI);
            if ((redirectUri == null && code.getRedirectUri() != null)
                    || (redirectUri != null && !redirectUri.equals(code.getRedirectUri()))) {
                // return an invalid_grant error
                return resource.oauthError(ERROR_INVALID_GRANT);
            }

            // generate a new refresh_token
            final RefreshToken refreshToken = new RefreshToken(code);
            this.oauthManager.createRefreshToken(refreshToken);

            // generate a new access_token
            final AccessToken accessToken = new AccessToken(code);
            this.oauthManager.createAccessToken(accessToken);

            // return the response data
            final Map<Object, Object> resp = new HashMap<Object, Object>();
            resp.put(PARAM_ACCESS_TOKEN, accessToken.getToken());
            resp.put(PARAM_TOKEN_TYPE, TOKEN_TYPE_BEARER);
            resp.put(PARAM_REFRESH_TOKEN, refreshToken.getToken());
            resp.put(PARAM_SCOPE, accessToken.getScopeString());
            return resp;
        } finally {
            // remove any found codes before actually returning
            if (code != null) {
                this.oauthManager.removeCode(code);
            }
        }
    }
}
