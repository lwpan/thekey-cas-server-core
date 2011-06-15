package org.ccci.gto.cas.authentication.handler;

import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.authentication.principal.OAuth2Credentials;
import org.jasig.cas.authentication.handler.AuthenticationException;

public class FacebookAuthenticationHandler extends OAuth2AuthenticationHandler {
    public FacebookAuthenticationHandler() {
	super(FacebookCredentials.class, false);
    }

    public FacebookAuthenticationHandler(
	    final Class<? extends FacebookCredentials> classToSupport,
	    final boolean supportSubClasses) {
	super(classToSupport, supportSubClasses);
    }

    @Override
    protected boolean authenticateOAuth2Internal(
	    final OAuth2Credentials rawCreds) throws AuthenticationException {
	final FacebookCredentials credentials = (FacebookCredentials) rawCreds;
	// TODO: implement logic to retrieve access_token using the OAuth2
	// authorization code

	return credentials.getAccessToken() != null;
    }
}
