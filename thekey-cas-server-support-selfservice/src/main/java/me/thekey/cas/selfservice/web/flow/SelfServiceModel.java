package me.thekey.cas.selfservice.web.flow;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;

import java.io.Serializable;

public final class SelfServiceModel implements Serializable {
    private static final long serialVersionUID = -7944053272154695773L;

    /** The authentication state for this SelfService session */
    private Authentication authentication;
    private TheKeyCredentials credentials;

    private String relayGuid;

    private String email;
    private String firstName;
    private String lastName;

    private String password;
    private String retypePassword;

    private String key;

    public final void setAuthentication(final Authentication auth) {
        this.authentication = auth;
    }

    public final boolean isAuthenticated() {
        return this.authentication != null;
    }

    public final Authentication getAuthentication() {
        return this.authentication;
    }

    public final TheKeyCredentials getCredentials() {
        return this.credentials;
    }

    public final void setCredentials(final TheKeyCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * @return the user for the current authentication
     */
    public final GcxUser getUser() {
        if (this.authentication != null) {
            final GcxUser user = AuthenticationUtil.getUser(this.authentication);
            if (user != null) {
                return user;
            }
        }
        if (this.credentials != null) {
            final GcxUser user = this.credentials.getUser();
            if (user != null) {
                return user;
            }
        }
        return null;
    }

    /**
     * convenience accessor that accesses the facebook id for the authenticated
     * user
     * 
     * @return the facebookId
     */
    public String getFacebookId() {
        final GcxUser user = this.getUser();
        if (user != null) {
            return user.getFacebookId();
        }
        return null;
    }

    public String getRelayGuid() {
        return this.relayGuid;
    }

    public void setRelayGuid(final String relayGuid) {
        this.relayGuid = relayGuid;
    }

    public final String getProposedEmail() {
        final GcxUser user = this.getUser();
        return user != null ? user.getProposedEmail() : null;
    }

    public final String getFirstName() {
        return this.firstName;
    }

    public final String getLastName() {
        return this.lastName;
    }

    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public final void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public final String getEmail() {
        return this.email;
    }

    public final String getPassword() {
        return this.password;
    }

    public final String getRetypePassword() {
        return this.retypePassword;
    }

    public final void setEmail(final String email) {
        this.email = email;
    }

    public final void setPassword(final String password) {
        this.password = password;
    }

    public final void setRetypePassword(final String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public final String getKey() {
        return this.key;
    }

    public final void setKey(final String key) {
        this.key = key;
    }
}
