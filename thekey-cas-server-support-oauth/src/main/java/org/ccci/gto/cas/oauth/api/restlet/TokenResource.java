package org.ccci.gto.cas.oauth.api.restlet;

import static org.ccci.gto.cas.oauth.Constants.ERROR_INVALID_GRANT;
import static org.ccci.gto.cas.oauth.Constants.ERROR_INVALID_SCOPE;
import static org.ccci.gto.cas.oauth.Constants.ERROR_SERVER_ERROR;
import static org.ccci.gto.cas.oauth.Constants.ERROR_UNSUPPORTED_GRANT_TYPE;
import static org.ccci.gto.cas.oauth.Constants.GRANT_TYPE_CODE;
import static org.ccci.gto.cas.oauth.Constants.GRANT_TYPE_REFRESH_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ACCESS_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_CLIENT_ID;
import static org.ccci.gto.cas.oauth.Constants.PARAM_CODE;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ERROR;
import static org.ccci.gto.cas.oauth.Constants.PARAM_EXPIRES_IN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_GRANT_TYPE;
import static org.ccci.gto.cas.oauth.Constants.PARAM_REDIRECT_URI;
import static org.ccci.gto.cas.oauth.Constants.PARAM_REFRESH_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.PARAM_SCOPE;
import static org.ccci.gto.cas.oauth.Constants.PARAM_THEKEY_GUID;
import static org.ccci.gto.cas.oauth.Constants.PARAM_TOKEN_TYPE;
import static org.ccci.gto.cas.oauth.Constants.TOKEN_TYPE_BEARER;

import org.ccci.gto.cas.oauth.OAuthManager;
import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.RefreshToken;
import org.ccci.gto.cas.oauth.util.OAuth2Util;
import org.ccci.gto.persistence.tx.RetryingTransactionService;
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

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class TokenResource extends Resource {
    private final static Logger LOG = LoggerFactory.getLogger(TokenResource.class);

    @Autowired
    @NotNull
    private OAuthManager oauthManager;

    @Autowired
    @NotNull
    private RetryingTransactionService txService;

    @Override
    public void acceptRepresentation(final Representation entity) throws ResourceException {
        final Form params = this.getRequest().getEntityAsForm();
        final String grantType = params.getFirstValue(PARAM_GRANT_TYPE, "");
        final Map<Object, Object> resp = new HashMap<>();

        // switch based on grant_type
        LOG.debug("switching request based on grant_type: {}", grantType);
        switch (grantType) {
        case GRANT_TYPE_CODE:
            resp.putAll(processCodeGrant());
            break;
        case GRANT_TYPE_REFRESH_TOKEN:
            resp.putAll(processRefreshTokenGrant());
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

    Map<Object, Object> oauthError(final String error) {
        LOG.debug("returning OAuth error: {}", error);
        this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        return Collections.<Object, Object>singletonMap(PARAM_ERROR, error);
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

    private Map<Object, Object> processCodeGrant() {
        try {
            return this.txService.inRetryingTransaction(new Callable<Map<Object, Object>>() {
                @Override
                public Map<Object, Object> call() throws Exception {
                    Code code = null;
                    try {
                        final Form params = getRequest().getEntityAsForm();

                        // fetch the code
                        code = oauthManager.getCode(params.getFirstValue(PARAM_CODE));

                        // find & authenticate the client
                        // TODO: utilize client_secret
                        final Client client = oauthManager.getClient(params.getFirstValue(PARAM_CLIENT_ID));

                        // return an error if this is an invalid request
                        if (client == null || code == null || code.isExpired() || !client.getId().equals(code
                                .getClient().getId())) {
                            // return an invalid_grant error
                            return oauthError(ERROR_INVALID_GRANT);
                        }

                        // validate the redirect_uri
                        final String redirectUri = params.getFirstValue(PARAM_REDIRECT_URI);
                        if ((redirectUri == null && code.getRedirectUri() != null) || (redirectUri != null &&
                                !redirectUri.equals(code.getRedirectUri()))) {
                            // return an invalid_grant error
                            return oauthError(ERROR_INVALID_GRANT);
                        }

                        // generate a new refresh_token
                        final RefreshToken refreshToken = new RefreshToken(code);
                        oauthManager.createRefreshToken(refreshToken);

                        // generate a new access_token
                        final AccessToken accessToken = new AccessToken(code);
                        oauthManager.createAccessToken(accessToken);

                        // return the response
                        return generateResponse(accessToken, refreshToken);
                    } finally {
                        // remove any found codes before actually returning
                        if (code != null) {
                            oauthManager.removeCode(code);
                        }
                    }
                }
            });
        } catch (final Exception e) {
            return oauthError(ERROR_SERVER_ERROR);
        }
    }

    private Map<Object, Object> processRefreshTokenGrant() {
        try {
            return this.txService.inRetryingTransaction(new Callable<Map<Object, Object>>() {
                @Override
                public Map<Object, Object> call() throws Exception {
                    final Form params = getRequest().getEntityAsForm();

                    // load the refresh_token
                    final RefreshToken token = oauthManager.getToken(RefreshToken.class,
                            params.getFirstValue(PARAM_REFRESH_TOKEN));

                    // ensure the refresh_token is valid
                    if (token == null || token.isExpired()) {
                        // return an invalid_grant error
                        return oauthError(ERROR_INVALID_GRANT);
                    }

                    // TODO: authenticate client if required

                    // validate the provided scope
                    final Set<String> scope = OAuth2Util.parseScope(params.getFirstValue(PARAM_SCOPE));
                    if (!token.getScope().containsAll(scope)) {
                        return oauthError(ERROR_INVALID_SCOPE);
                    } else if (scope.size() == 0) {
                        // empty scope is the same as the previous scope
                        scope.addAll(token.getScope());
                    }

                    // generate a new access_token
                    final AccessToken accessToken = new AccessToken(token);
                    accessToken.setScope(scope);
                    oauthManager.createAccessToken(accessToken);

                    // return the response
                    return generateResponse(accessToken, null);
                }
            });
        } catch (final Exception e) {
            return oauthError(ERROR_SERVER_ERROR);
        }
    }

    static Map<Object, Object> generateResponse(final AccessToken accessToken, final RefreshToken refreshToken) {
        final Map<Object, Object> resp = new HashMap<>();

        if (accessToken != null) {
            resp.put(PARAM_TOKEN_TYPE, TOKEN_TYPE_BEARER);
            resp.put(PARAM_ACCESS_TOKEN, accessToken.getToken());
            resp.put(PARAM_SCOPE, accessToken.getScopeString());
            resp.put(PARAM_THEKEY_GUID, accessToken.getGuid());

            final Date expirationTime = accessToken.getExpirationTime();
            if (expirationTime != null) {
                final long expiresIn = (expirationTime.getTime() - System.currentTimeMillis()) / 1000;
                if (expiresIn > 0) {
                    resp.put(PARAM_EXPIRES_IN, expiresIn);
                }
            }

            if (refreshToken != null) {
                resp.put(PARAM_REFRESH_TOKEN, refreshToken.getToken());
            }
        }

        return resp;
    }
}
