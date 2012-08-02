package org.ccci.gto.cas.facebook.authentication.handler;

import org.ccci.gto.cas.authentication.principal.OAuth2Credentials;
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
    protected boolean doAuthentication(final Credentials credentials) throws AuthenticationException {
        return authenticateOAuth2Internal((OAuth2Credentials) credentials);
    }

    protected abstract boolean authenticateOAuth2Internal(final OAuth2Credentials credentials)
            throws AuthenticationException;

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
