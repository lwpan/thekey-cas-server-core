package me.thekey.cas.facebook.authentication.handler;

import me.thekey.cas.facebook.authentication.principal.OAuth2ClientCredentials;
import me.thekey.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;

public abstract class OAuth2ClientAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private final Class<? extends OAuth2ClientCredentials> classToSupport;
    private final boolean supportSubClasses;

    public OAuth2ClientAuthenticationHandler() {
        this.classToSupport = OAuth2ClientCredentials.class;
        this.supportSubClasses = true;
    }

    public OAuth2ClientAuthenticationHandler(final Class<? extends OAuth2ClientCredentials> classToSupport,
                                             final boolean supportSubClasses) {
        this.classToSupport = classToSupport;
        this.supportSubClasses = supportSubClasses;
    }

    @Override
    protected boolean doAuthentication(final Credentials rawCredentials) throws AuthenticationException {
        final OAuth2ClientCredentials credentials = (OAuth2ClientCredentials) rawCredentials;

        // authenticate these credentials
        final boolean response = authenticateOAuth2Internal(credentials);

        // only process if authentication was successful
        if (response) {
            // lookup the GcxUser object for the authenticated identity
            lookupUser(credentials);

            // check all authentication locks
            AuthenticationUtil.checkLocks(credentials);
        }

        return response;
    }

    protected abstract boolean authenticateOAuth2Internal(final OAuth2ClientCredentials credentials) throws AuthenticationException;

    protected abstract void lookupUser(final OAuth2ClientCredentials credentials) throws AuthenticationException;

    /**
     * @return true if the credentials are not null and the credentials class is
     *         a subclass of OAuth2Credentials.
     */
    public final boolean supports(final Credentials credentials) {
        return credentials != null
                && (this.classToSupport.equals(credentials.getClass()) || (this.classToSupport
                        .isAssignableFrom(credentials.getClass()) && this.supportSubClasses));
    }
}
