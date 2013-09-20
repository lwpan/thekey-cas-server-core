package org.ccci.gto.cas.authentication.principal;

import org.ccci.gto.cas.facebook.util.FacebookUtils;

import com.restfb.json.JsonObject;
import com.restfb.types.User;

public class FacebookCredentials extends OAuth2Credentials {
    private static final long serialVersionUID = -5196428927716220237L;

    private String accessToken;
    private String signedRequest;
    private User fbUser;

    public FacebookCredentials() {
        this(true);
    }

    public FacebookCredentials(final boolean observeLocks) {
        super(observeLocks);
        this.setObserveLock(Lock.STALEPASSWORD, false);
        this.setObserveLock(Lock.VERIFIED, false);
    }

    /**
     * @param accessToken the accessToken to set
     */
    public void setAccessToken(final String accessToken) {
	this.accessToken = accessToken;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
	return accessToken;
    }

    /**
     * @param signedRequest
     *            the signedRequest to set
     */
    public void setSignedRequest(final String signedRequest) {
	this.signedRequest = signedRequest;

	// extract the code from the signed request
	final JsonObject data = FacebookUtils.getSignedData(this.signedRequest);
	this.setCode(data.optString("code", null));
    }

    /**
     * @return the signedRequest
     */
    public String getSignedRequest() {
	return this.signedRequest;
    }

    public void setFbUser(final User fbUser) {
        this.fbUser = fbUser;
    }

    public User getFbUser() {
        return fbUser;
    }
}
