package org.ccci.gto.cas.authentication.principal;

public class FacebookCredentials extends OAuth2Credentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = 6186525037994959630L;

    private String accessToken;

    /**
     * flag indicating if the user should be vivified if they don't already
     * exist
     */
    private boolean vivify = false;

    public FacebookCredentials() {
	// ignore the oauth2 code for FacebookCredentials because it is
	// currently not used
	super("");
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
