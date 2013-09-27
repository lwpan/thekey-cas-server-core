package org.ccci.gto.cas.selfservice;

import me.thekey.cas.selfservice.web.flow.SelfServiceModel;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;

/**
 * The object that stores data about the user being manipulated in the
 * SelfService webflow
 */
public class SelfServiceUser extends SelfServiceModel {
    private static final long serialVersionUID = 5569782061506971971L;

    /** The authentication state for this SelfService session */
    private Authentication authentication;

    private String firstName;
    private String lastName;

    public void setAuthentication(final Authentication auth) {
	this.authentication = auth;
    }

    public boolean isAuthenticated() {
	return this.authentication != null;
    }

    public Authentication getAuthentication() {
	return this.authentication;
    }

    /**
     * @return the user for the current authentication
     */
    public GcxUser getUser() {
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

    public void setFirstName(final String firstName) {
	this.firstName = firstName;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setLastName(final String lastName) {
	this.lastName = lastName;
    }

    public String getLastName() {
	return lastName;
    }
}
