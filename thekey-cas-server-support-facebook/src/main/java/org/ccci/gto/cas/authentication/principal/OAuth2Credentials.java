package org.ccci.gto.cas.authentication.principal;

import javax.validation.constraints.NotNull;

public class OAuth2Credentials extends AbstractTheKeyCredentials {
    private static final long serialVersionUID = 3164303387656588339L;

    /** The Opaque authorization code */
    @NotNull
    private String code;

    /** an optional state value */
    private String state;

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
