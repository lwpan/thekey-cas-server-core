package org.ccci.gto.cas.facebook.authentication.handler;

import org.ccci.gto.cas.authentication.principal.OAuth2Credentials;
import me.thekey.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;

public abstract class OAuth2AuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private final Class<? extends OAuth2Credentials> classToSupport;
    private final boolean supportSubClasses;

    public OAuth2AuthenticationHandler() {
        this.classToSupport = OAuth2Credentials.class;
        this.supportSubClasses = true;
    }

    public OAuth2AuthenticationHandler(final Class<? extends OAuth2Credentials> classToSupport,
            final boolean supportSubClasses) {
        this.classToSupport = classToSupport;
        this.supportSubClasses = supportSubClasses;
    }

    @Override
    protected boolean doAuthentication(final Credentials rawCredentials) throws AuthenticationException {
        final OAuth2Credentials credentials = (OAuth2Credentials) rawCredentials;

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

    protected abstract boolean authenticateOAuth2Internal(final OAuth2Credentials credentials)
            throws AuthenticationException;

    protected abstract void lookupUser(final OAuth2Credentials credentials) throws AuthenticationException;

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
