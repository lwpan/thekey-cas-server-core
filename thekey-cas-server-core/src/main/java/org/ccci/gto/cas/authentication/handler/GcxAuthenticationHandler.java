package org.ccci.gto.cas.authentication.handler;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.NoOpPrincipalNameTransformer;
import org.jasig.cas.authentication.handler.PrincipalNameTransformer;
import org.jasig.cas.authentication.handler.UnknownUsernameAuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class GcxAuthenticationHandler extends
	AbstractPreAndPostProcessingAuthenticationHandler {
    @NotNull
    private GcxUserService gcxUserService;

    @NotNull
    private AbstractUsernamePasswordAuthenticationHandler handler;

    @NotNull
    private PrincipalNameTransformer principalNameTransformer = new NoOpPrincipalNameTransformer();

    @Override
    protected boolean doAuthentication(Credentials credentials)
	    throws AuthenticationException {
	// run actual handler
	final boolean response = this.handler.authenticate(credentials);

	// The user was authenticated, check for any administrative holds
	if (response) {
	    // Lookup user
	    final String userName = this.getPrincipalNameTransformer()
		    .transform(
			    ((UsernamePasswordCredentials) credentials)
				    .getUsername());
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
	    }
	}

	// return the original authentication response
	return response;
    }

    /**
     * @return the principalNameTransformer
     */
    protected PrincipalNameTransformer getPrincipalNameTransformer() {
	return principalNameTransformer;
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

    /**
     * @param principalNameTransformer the principalNameTransformer to set
     */
    public void setPrincipalNameTransformer(
	    final PrincipalNameTransformer principalNameTransformer) {
	this.principalNameTransformer = principalNameTransformer;
    }

    public boolean supports(final Credentials credentials) {
	return this.handler.supports(credentials);
    }
}
