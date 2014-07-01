package org.ccci.gto.cas.authentication.principal;

import com.restfb.json.JsonObject;
import com.restfb.types.User;
import org.ccci.gto.cas.facebook.util.FacebookUtils;

public class FacebookCredentials extends OAuth2Credentials {
    private static final long serialVersionUID = 3745114973526803725L;

    private String accessToken;
    private String signedRequest;
    private User fbUser;

    public FacebookCredentials() {
        super();
    }

    public FacebookCredentials(final boolean observeLocks) {
        super(observeLocks);
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
