package org.ccci.gto.cas.authentication.handler;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.authentication.principal.OAuth2Credentials;
import org.ccci.gto.cas.facebook.restfb.FacebookClient;
import org.ccci.gto.cas.facebook.util.FacebookUtils;
import org.jasig.cas.authentication.handler.AuthenticationException;

import com.restfb.json.JsonObject;

public class FacebookAuthenticationHandler extends OAuth2AuthenticationHandler {
    @NotNull
    private String appId;

    @NotNull
    private String secret;

    /**
     * @param appId
     *            the appId to set
     */
    public void setAppId(final String appId) {
	this.appId = appId;
    }

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

	// reject any invalid signed requests
	final String signedRequest = credentials.getSignedRequest();
	final JsonObject data = FacebookUtils.getSignedData(signedRequest);
	if (!FacebookUtils.validateSignedRequest(signedRequest, this.secret,
		data.optString("algorithm"))) {
	    return false;
	}

	// exchange the authorization code for an access token
	credentials.setAccessToken(new FacebookClient()
		.exchangeCodeForAccessToken(this.appId, this.secret,
			credentials.getCode(), ""));

	// accept FacebookCredentials if they resolved to an accessToken
	return credentials.getAccessToken() != null;
    }
}
