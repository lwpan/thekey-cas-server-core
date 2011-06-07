package org.ccci.gto.cas.authentication.principal;

import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;

public abstract class OAuth2CredentialsToPrincipalResolver implements
	CredentialsToPrincipalResolver {
    private final Class<? extends OAuth2Credentials> classToSupport;
    private final boolean supportSubClasses;

    public OAuth2CredentialsToPrincipalResolver() {
	this.classToSupport = OAuth2Credentials.class;
	this.supportSubClasses = true;
    }

    public OAuth2CredentialsToPrincipalResolver(
	    final Class<? extends OAuth2Credentials> classToSupport,
	    final boolean supportSubClasses) {
	this.classToSupport = classToSupport;
	this.supportSubClasses = supportSubClasses;
    }

    protected abstract Principal resolveOAuth2Principal(final OAuth2Credentials credentials);
    
    public Principal resolvePrincipal(final Credentials credentials) {
	return resolveOAuth2Principal((OAuth2Credentials) credentials);
    }

    public final boolean supports(final Credentials credentials) {
	return credentials != null
		&& (this.classToSupport.equals(credentials.getClass()) || (this.classToSupport
			.isAssignableFrom(credentials.getClass()) && this.supportSubClasses));
    }
}
