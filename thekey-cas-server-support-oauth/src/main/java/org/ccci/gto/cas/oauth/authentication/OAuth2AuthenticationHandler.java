package org.ccci.gto.cas.oauth.authentication;

import java.util.Set;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

import org.ccci.gto.cas.oauth.OAuthManager;
import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;

public final class OAuth2AuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler implements
        AuthenticationHandler {
    @NotNull
    private OAuthManager oauthManager;

    @NotNull
    private UserManager userService;

    @Override
    protected boolean doAuthentication(final Credentials rawCredentials) throws AuthenticationException {
        final OAuth2Credentials credentials = (OAuth2Credentials) rawCredentials;

        // Lookup the access_token
        final AccessToken token;
        try {
            token = this.oauthManager.getToken(AccessToken.class, credentials.getRawToken());
            credentials.setAccessToken(token);
        } catch (final Exception e) {
            credentials.setAccessToken(null);
            throw new BadCredentialsAuthenticationException("access_token not found", e);
        }
        if (token == null) {
            throw new BadCredentialsAuthenticationException("access_token not found");
        }

        // Make sure the access_token hasn't expired
        if (token.isExpired()) {
            // the access_token has expired
            throw new BadCredentialsAuthenticationException("access_token has expired");
        }

        // make sure the access_token meets the required scope
        final Set<String> requiredScope = credentials.getRequiredScope();
        if (requiredScope.size() > 0) {
            for (final String scope : requiredScope) {
                if (!token.isScopeAuthorized(scope)) {
                    throw new BadCredentialsAuthenticationException("access_token doesn't meet the required scope");
                }
            }
        }

        // Retrieve the user object based on the access_token's guid
        credentials.setGcxUser(this.userService.findUserByGuid(token.getGuid()));

        // check all authentication locks
        AuthenticationUtil.checkLocks(credentials);

        // the access_token is still valid and authenticated successfully
        return true;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials != null && credentials instanceof OAuth2Credentials;
    }

    public void setOAuthManager(final OAuthManager oauthManager) {
        this.oauthManager = oauthManager;
    }

    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }
}
