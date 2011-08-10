package org.ccci.gto.cas.authentication.principal;

import org.ccci.gto.cas.facebook.util.FacebookUtils;

import com.restfb.json.JsonObject;

public class FacebookCredentials extends OAuth2Credentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = 6186525037994959630L;

    private String accessToken;
    private String signedRequest;

    /**
     * flag indicating if the user should be vivified if they don't already
     * exist
     */
    private boolean vivify = false;

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

    /**
     * @param flag
     *            boolean value indicating if the user should be vivified or not
     */
    public void setVivify(final boolean flag) {
	this.vivify = flag;
    }

    /**
     * @return whether or not the user should be vivified if they don't exist
     */
    public boolean isVivify() {
	return this.vivify;
    }
}
