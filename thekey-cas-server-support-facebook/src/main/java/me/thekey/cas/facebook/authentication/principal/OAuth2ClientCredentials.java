package me.thekey.cas.facebook.authentication.principal;

import me.thekey.cas.federation.authentication.principal.FederatedTheKeyCredentials;

import javax.validation.constraints.NotNull;

public class OAuth2ClientCredentials extends FederatedTheKeyCredentials {
    private static final long serialVersionUID = 8518808831746457032L;

    // The Opaque authorization code
    @NotNull
    private String code;

    // an optional state value
    private String state;

    public OAuth2ClientCredentials() {
        super();
    }

    public OAuth2ClientCredentials(final boolean observeLocks) {
        super(observeLocks);
    }

    /**
     * @param code the code to set
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @param state the state to set
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * @return the state
     */
    public String getState() {
        return this.state;
    }
}
