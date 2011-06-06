package org.ccci.gto.cas.authentication.principal;

import org.jasig.cas.authentication.principal.Credentials;

public class OAuth2Credentials implements Credentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = -4603224077053631621L;

    /** The Opaque authorization code */
    private final String code;

    /** an optional state value */
    private final String state;

    public OAuth2Credentials(final String code) {
	this(code, null);
    }

    public OAuth2Credentials(final String code, final String state) {
	this.code = code;
	this.state = state;
    }

    /**
     * @return the code
     */
    public String getCode() {
	return this.code;
    }

    /**
     * @return the state
     */
    public String getState() {
	return this.state;
    }
}
