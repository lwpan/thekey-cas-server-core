package org.ccci.gto.cas.authentication;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_KEYUSER;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.authentication.handler.DeactivatedAccountAuthenticationException;
import org.ccci.gto.cas.authentication.handler.DisabledAccountAuthenticationException;
import org.ccci.gto.cas.authentication.handler.LockedAccountAuthenticationException;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationMetaDataPopulator;
import org.jasig.cas.authentication.MutableAuthentication;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.UnknownUsernameAuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserAuthenticationMetaDataPopulator implements
	AuthenticationMetaDataPopulator {
    /** Instance of logging for subclasses. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /** attributes that specify what Credentials this MetaDataPopulator supports */
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

    /**
     * This method guarantees the specified Authentication object has mutable
     * attributes
     * 
     * @param authentication
     * @return an Authentication object with mutable attributes
     */
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

    /**
     * This method checks to see if the specified Credentials are observing the
     * specified Lock. If the Credentials don't support granular locking, all
     * locking is enabled.
     * 
     * @param credentials
     * @param lock
     * @return
     */
    protected final boolean observeLock(final Credentials credentials,
	    final Lock lock) {
	if (credentials instanceof TheKeyCredentials) {
	    return ((TheKeyCredentials) credentials).observeLock(lock);
	}
	return true;
    }

    public Authentication populateAttributes(Authentication authentication,
	    final Credentials credentials) throws AuthenticationException {
	// only process if the provided credentials are supported
	if (this.supports(credentials)) {
	    // make sure the authentication object is mutable
	    authentication = this.makeMutable(authentication);

	    // lookup and store the user in the Authentication response
	    authentication = this.preLookup(authentication, credentials);
	    authentication.getAttributes().put(AUTH_ATTR_KEYUSER,
		    this.findUser(authentication, credentials));
	    authentication = this.postLookup(authentication, credentials);
	    this.validateUser(authentication, credentials);
	}

	// return the authentication object
	return authentication;
    }

    protected Authentication preLookup(final Authentication authentication,
	    final Credentials credentials) throws AuthenticationException {
	return authentication;
    }

    protected Authentication postLookup(final Authentication authentication,
	    final Credentials credentials) throws AuthenticationException {
	return authentication;
    }

    protected boolean validateUser(final Authentication authentication,
	    final Credentials credentials) throws AuthenticationException {
	final GcxUser user = (GcxUser) authentication.getAttributes().get(
		AUTH_ATTR_KEYUSER);

	// the user authenticated, but doesn't exist?
	if (user == null && this.observeLock(credentials, Lock.NULLUSER)) {
	    log.info("User authenticated, but account doesn't exist?!?!?");
	    throw UnknownUsernameAuthenticationException.ERROR;
	}

	// check for any administrative locks on the user
	if (user != null) {
	    // is the account deactivated
	    if (user.isDeactivated()
		    && this.observeLock(credentials, Lock.DEACTIVATED)) {
		log.debug("Account is deactivated: " + user.getGUID());
		throw DeactivatedAccountAuthenticationException.ERROR;
	    }

	    // is the account locked
	    if (user.isLocked() && this.observeLock(credentials, Lock.LOCKED)) {
		log.debug("Account is locked: " + user.getGUID());
		throw LockedAccountAuthenticationException.ERROR;
	    }

	    // is the account disabled
	    if (user.isLoginDisabled()
		    && this.observeLock(credentials, Lock.DISABLED)) {
		log.info("Account is disabled: " + user.getGUID());
		throw DisabledAccountAuthenticationException.ERROR;
	    }
	}

	return true;
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
