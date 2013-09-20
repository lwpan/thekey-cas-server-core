package org.ccci.gto.cas.util;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_KEYUSER;
import me.thekey.cas.authentication.handler.UnverifiedAccountAuthenticationException;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.handler.DeactivatedAccountAuthenticationException;
import org.ccci.gto.cas.authentication.handler.DisabledAccountAuthenticationException;
import org.ccci.gto.cas.authentication.handler.LockedAccountAuthenticationException;
import org.ccci.gto.cas.authentication.handler.StalePasswordAuthenticationException;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.MutableAuthentication;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.UnknownUsernameAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthenticationUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationUtil.class);

    /**
     * This method guarantees the specified Authentication object has mutable
     * attributes
     * 
     * @param authentication
     * @return an Authentication object with mutable attributes
     */
    public static Authentication makeMutable(final Authentication authentication) {
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
     * @param user
     *            the user to set in the Authentication object
     */
    public static void setUser(final Authentication authentication,
	    final GcxUser user) {
	authentication.getAttributes().put(AUTH_ATTR_KEYUSER, user);
    }

    /**
     * @return the user currently stored in the Authentication object
     */
    public static final GcxUser getUser(final Authentication authentication) {
	return (GcxUser) authentication.getAttributes().get(AUTH_ATTR_KEYUSER);
    }

    public static final boolean checkLocks(final TheKeyCredentials credentials) throws AuthenticationException {
        final GcxUser user = credentials.getUser();

        // the user authenticated, but doesn't exist?
        if (user == null && credentials.observeLock(Lock.NULLUSER)) {
            LOG.info("User authenticated, but account doesn't exist?!?!?");
            throw UnknownUsernameAuthenticationException.ERROR;
        }

        // check for any administrative locks on the user
        if (user != null) {
            // is the account deactivated
            if (user.isDeactivated() && credentials.observeLock(Lock.DEACTIVATED)) {
                LOG.debug("Account is deactivated: {}", user.getGUID());
                throw DeactivatedAccountAuthenticationException.ERROR;
            }

            // is the account locked
            if (user.isLocked() && credentials.observeLock(Lock.LOCKED)) {
                LOG.debug("Account is locked: {}", user.getGUID());
                throw LockedAccountAuthenticationException.ERROR;
            }

            // is the account disabled
            if (user.isLoginDisabled() && credentials.observeLock(Lock.DISABLED)) {
                LOG.info("Account is disabled: {}", user.getGUID());
                throw DisabledAccountAuthenticationException.ERROR;
            }

            // is the account verified
            if (!user.isVerified() && credentials.observeLock(Lock.VERIFIED)) {
                LOG.info("Account has not been verified: {}", user.getGUID());
                throw UnverifiedAccountAuthenticationException.ERROR;
            }

            // Does the user need to change their password?
            if (user.isForcePasswordChange() && credentials.observeLock(Lock.STALEPASSWORD)) {
                LOG.info("Account has a stale password: {}", user.getGUID());
                throw StalePasswordAuthenticationException.ERROR;
            }
        }

        return true;
    }
}
