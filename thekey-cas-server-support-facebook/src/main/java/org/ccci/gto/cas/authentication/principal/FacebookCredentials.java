package org.ccci.gto.cas.authentication.principal;

public class FacebookCredentials extends OAuth2Credentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = 6186525037994959630L;

    private String accessToken;

    public FacebookCredentials(final String code) {
	super(code);
    }

    public FacebookCredentials(final String code, final String state) {
	super(code, state);
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
}
