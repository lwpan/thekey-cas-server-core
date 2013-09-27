package me.thekey.cas.selfservice.web.flow;

import java.io.Serializable;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;

public class SelfServiceModel implements Serializable {
    private static final long serialVersionUID = 3681503503116682018L;

    /** The authentication state for this SelfService session */
    private Authentication authentication;

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

    /**
     * @return the user for the current authentication
     */
    public final GcxUser getUser() {
        if (this.authentication != null) {
            return AuthenticationUtil.getUser(this.authentication);
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
        final GcxUser user = this.getUser();
        if (user != null) {
            return user.getRelayGuid();
        }
        return null;
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
