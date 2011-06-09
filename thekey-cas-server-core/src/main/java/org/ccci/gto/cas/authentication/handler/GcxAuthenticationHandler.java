package org.ccci.gto.cas.authentication.handler;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.UnknownUsernameAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class GcxAuthenticationHandler extends
	AbstractUsernamePasswordAuthenticationHandler {
    @NotNull
    private GcxUserService gcxUserService;

    @NotNull
    private AbstractUsernamePasswordAuthenticationHandler handler;

    @Override
    protected boolean authenticateUsernamePasswordInternal(
	    final UsernamePasswordCredentials credentials)
	    throws AuthenticationException {
	// run actual handler
	final boolean response = this.handler.authenticate(credentials);
	final boolean observeLocks = !(credentials instanceof TheKeyUsernamePasswordCredentials)
		|| ((TheKeyUsernamePasswordCredentials) credentials)
			.observeLocks();

	// check for any administrative holds if the user was authenticated and
	// we are observing administrative locks
	if (response && observeLocks) {
	    // Lookup user
	    final String userName = this.getPrincipalNameTransformer()
		    .transform(credentials.getUsername());
	    final GcxUser user = this.gcxUserService.findUserByEmail(userName);

	    // the user authenticated, but doesn't exist?
	    if (user == null) {
		log.info("User authenticated, but account doesn't exist?!?!?");
		throw UnknownUsernameAuthenticationException.ERROR;
	    }

	    // check for any administrative locks on the user
	    if (user != null) {
		// is the account locked
		if (user.isLocked()) {
		    log.info("Account is locked: " + userName);
		    throw LockedAccountAuthenticationException.ERROR;
		}

		// is the account deactivated
		if (user.isDeactivated()) {
		    log.info("Account is deactivated: " + userName);
		    throw DeactivatedAccountAuthenticationException.ERROR;
		}

		// is the account disabled
		if (user.isLoginDisabled()) {
		    log.info("Account is disabled: " + userName);
		    throw DisabledAccountAuthenticationException.ERROR;
		}

		// Does the user need to change their password?
		if (user.isForcePasswordChange()) {
		    log.info("Account has a stale password: " + userName);
		    throw StalePasswordAuthenticationException.ERROR;
		}
	    }
	}

	// return the original authentication response
	return response;
    }

    /**
     * @param gcxUserService
     *            the gcxUserService to set
     */
    public void setGcxUserService(final GcxUserService gcxUserService) {
	this.gcxUserService = gcxUserService;
    }

    /**
     * @param handler
     *            the handler to proxy authentication requests to
     */
    public void setHandler(
	    final AbstractUsernamePasswordAuthenticationHandler handler) {
	this.handler = handler;
    }
}
