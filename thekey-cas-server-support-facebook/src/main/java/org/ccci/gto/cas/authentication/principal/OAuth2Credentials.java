package org.ccci.gto.cas.authentication.principal;

import me.thekey.cas.authentication.principal.AbstractTheKeyCredentials;

import javax.validation.constraints.NotNull;

public class OAuth2Credentials extends AbstractTheKeyCredentials {
    private static final long serialVersionUID = 430098956133264866L;

    /** The Opaque authorization code */
    @NotNull
    private String code;

    /** an optional state value */
    private String state;

    public OAuth2Credentials() {
        super();
    }

    public OAuth2Credentials(final boolean observeLocks) {
        super(observeLocks);
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
	this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
	return this.code;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
	this.state = state;
    }

    /**
     * @return the state
     */
    public String getState() {
	return this.state;
    }
}
