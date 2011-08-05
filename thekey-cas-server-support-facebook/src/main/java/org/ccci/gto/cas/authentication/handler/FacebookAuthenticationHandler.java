package org.ccci.gto.cas.authentication.handler;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.authentication.principal.OAuth2Credentials;
import org.ccci.gto.cas.facebook.util.FacebookUtils;
import org.jasig.cas.authentication.handler.AuthenticationException;

import com.restfb.json.JsonObject;

public class FacebookAuthenticationHandler extends OAuth2AuthenticationHandler {
    @NotNull
    private String secret;

    /**
     * @param secret
     *            the secret to set
     */
    public void setSecret(final String secret) {
	this.secret = secret;
    }

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

	// extract the json data from the signed request, which is stored as the
	// code for FacebookCredentials
	final String signedRequest = credentials.getSignedRequest();
	final JsonObject data = FacebookUtils.getSignedData(signedRequest);

	// validate the signed request
	if (!FacebookUtils.validateSignedRequest(signedRequest, this.secret,
		data.optString("algorithm"))) {
	    return false;
	}

	if (data.optString("user_id", null) == null) {
	    return false;
	}

	return true;
    }
}
