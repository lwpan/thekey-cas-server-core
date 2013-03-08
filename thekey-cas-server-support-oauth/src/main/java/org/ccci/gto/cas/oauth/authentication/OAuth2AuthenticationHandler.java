package org.ccci.gto.cas.oauth.authentication;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.oauth.OAuthManager;
import org.ccci.gto.cas.oauth.model.Grant;
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
    private GcxUserService userService;

    @Override
    protected boolean doAuthentication(final Credentials rawCredentials) throws AuthenticationException {
        final OAuth2Credentials credentials = (OAuth2Credentials) rawCredentials;

        // Lookup grant from the access token
        try {
            credentials.setGrant(this.oauthManager.getGrantByAccessToken(credentials.getAccessToken()));
        } catch (final Exception e) {
            throw new BadCredentialsAuthenticationException("access_token not found", e);
        }
        final Grant grant = credentials.getGrant();

        // Make sure the grant hasn't expired
        final Date expirationTime = grant.getExpirationTime();
        if (expirationTime == null || expirationTime.before(new Date())) {
            // the access_token has expired
            throw new BadCredentialsAuthenticationException("access_token has expired");
        }

        // make sure the grant meets the required scope
        final Set<String> requiredScope = credentials.getRequiredScope();
        if (requiredScope.size() > 0) {
            for (final String scope : requiredScope) {
                if (!grant.isScopeAuthorized(scope)) {
                    throw new BadCredentialsAuthenticationException("access_token doesn't meet the required scope");
                }
            }
        }

        // Retrieve the user object based on the grant's guid
        credentials.setGcxUser(this.userService.findUserByGuid(grant.getGuid()));

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

    public void setUserService(final GcxUserService userService) {
        this.userService = userService;
    }
}
