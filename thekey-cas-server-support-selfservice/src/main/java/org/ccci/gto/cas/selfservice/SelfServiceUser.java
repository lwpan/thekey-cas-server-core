package org.ccci.gto.cas.selfservice;

import java.io.Serializable;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;

/**
 * The object that stores data about the user being manipulated in the
 * SelfService webflow
 * 
 * @author Daniel Frett
 */
public class SelfServiceUser implements Serializable {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = 8839199455284509406L;

    /** The authentication state for this SelfService session */
    private Authentication authentication;

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String retypePassword;

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

    public void setEmail(final String email) {
	this.email = email;
    }

    public String getEmail() {
	return this.email;
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

    public void setPassword(final String password) {
	this.password = password;
    }

    public String getPassword() {
	return password;
    }

    public void setRetypePassword(final String retypePassword) {
	this.retypePassword = retypePassword;
    }

    public String getRetypePassword() {
	return retypePassword;
    }
}
