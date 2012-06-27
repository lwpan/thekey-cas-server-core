package org.ccci.gto.cas.authentication;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;

import java.util.HashMap;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.authentication.handler.DeactivatedAccountAuthenticationException;
import org.ccci.gto.cas.authentication.handler.DisabledAccountAuthenticationException;
import org.ccci.gto.cas.authentication.handler.LockedAccountAuthenticationException;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationMetaDataPopulator;
import org.jasig.cas.authentication.MutableAuthentication;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.UnknownUsernameAuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.SimplePrincipal;
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
	    authentication = AuthenticationUtil.makeMutable(authentication);

	    // lookup and store the user in the Authentication response
	    authentication = this.preLookup(authentication, credentials);
	    AuthenticationUtil.setUser(authentication,
		    this.findUser(authentication, credentials));
	    authentication = this.postLookup(authentication, credentials);
	    this.validateUser(authentication, credentials);

	    // populate the principal attributes from the found user
	    authentication = this.populatePrincipalAttributes(authentication);
	}

	// return the authentication object
	return authentication;
    }

    protected Authentication populatePrincipalAttributes(
	    Authentication authentication) {
	// Populate the principal attributes for the found user
	final GcxUser user = AuthenticationUtil.getUser(authentication);
	if (user != null) {
	    // fetch the existing principal attributes
	    final HashMap<String, Object> attrs = new HashMap<String, Object>(
		    authentication.getPrincipal().getAttributes());

	    // set the attributes used for the key
	    attrs.put(PRINCIPAL_ATTR_GUID, user.getGUID());
	    attrs.put(PRINCIPAL_ATTR_ADDITIONALGUIDS, user.getGUIDAdditional());
	    attrs.put(PRINCIPAL_ATTR_EMAIL, user.getEmail());
            attrs.put(PRINCIPAL_ATTR_FACEBOOKID, user.getFacebookId());
	    attrs.put(PRINCIPAL_ATTR_FIRSTNAME, user.getFirstName());
	    attrs.put(PRINCIPAL_ATTR_LASTNAME, user.getLastName());

	    // replace the Authentication object with one utilizing a new
	    // Principal object
	    final Authentication oldAuth = authentication;
	    authentication = new MutableAuthentication(new SimplePrincipal(
		    user.getEmail(), attrs), oldAuth.getAuthenticatedDate());
	    authentication.getAttributes().putAll(oldAuth.getAttributes());
	}

	// return the Authentication object
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
	final GcxUser user = AuthenticationUtil.getUser(authentication);

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
