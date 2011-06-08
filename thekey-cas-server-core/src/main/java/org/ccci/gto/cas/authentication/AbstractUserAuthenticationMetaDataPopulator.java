package org.ccci.gto.cas.authentication;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_KEYUSER;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationMetaDataPopulator;
import org.jasig.cas.authentication.MutableAuthentication;
import org.jasig.cas.authentication.principal.Credentials;

public abstract class AbstractUserAuthenticationMetaDataPopulator implements
	AuthenticationMetaDataPopulator {
    private final Class<? extends Credentials> classToSupport;
    private final boolean supportSubClasses;

    @NotNull
    private GcxUserService userService;

    public AbstractUserAuthenticationMetaDataPopulator() {
	this.classToSupport = Credentials.class;
	this.supportSubClasses = true;
    }

    public AbstractUserAuthenticationMetaDataPopulator(
	    final Class<? extends Credentials> classToSupport,
	    final boolean supportSubClasses) {
	this.classToSupport = classToSupport;
	this.supportSubClasses = supportSubClasses;
    }

    protected abstract GcxUser findUser(final Authentication authentication,
	    final Credentials credentials);

    protected Authentication makeMutable(final Authentication authentication) {
	// don't do anything if authentication is already mutable
	if (authentication instanceof MutableAuthentication) {
	    return authentication;
	}

	// create a new MutableAuthentication object
	final Authentication newAuth = new MutableAuthentication(
		authentication.getPrincipal(),
		authentication.getAuthenticatedDate());
	newAuth.getAttributes().putAll(authentication.getAttributes());

	// return the new MutableAuthentication object
	return newAuth;
    }

    public Authentication populateAttributes(
	    final Authentication authentication, final Credentials credentials) {
	// only process if the provided credentials are supported
	if (!this.supports(credentials)) {
	    return authentication;
	}

	// make sure the authentication object is mutable
	final Authentication newAuth = this.makeMutable(authentication);

	// set the user
	newAuth.getAttributes().put(AUTH_ATTR_KEYUSER,
		this.findUser(authentication, credentials));

	// return the updated authentication object
	return newAuth;
    }

    /**
     * @return true if the credentials are not null and the credentials class is
     *         an implementation of Credentials.
     */
    public final boolean supports(final Credentials credentials) {
	return credentials != null
		&& (this.classToSupport.equals(credentials.getClass()) || (this.classToSupport
			.isAssignableFrom(credentials.getClass()) && this.supportSubClasses));
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final GcxUserService userService) {
	this.userService = userService;
    }

    /**
     * @return the userService
     */
    public GcxUserService getUserService() {
	return userService;
    }
}
