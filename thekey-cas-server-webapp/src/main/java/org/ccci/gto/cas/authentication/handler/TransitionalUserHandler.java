package org.ccci.gto.cas.authentication.handler;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.Constants;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * Handles authenticating a transitional user. If authenticated then we will
 * activate them from a transitional user to a permanent one so all future
 * logins will be as normal GCXUsers.
 * 
 * @author Ken Burcham
 * @author Daniel Frett
 */
public class TransitionalUserHandler extends
	AbstractUsernamePasswordAuthenticationHandler {
    @NotNull
    private GcxUserService gcxUserService;

    @Override
    protected final boolean authenticateUsernamePasswordInternal(
	    final UsernamePasswordCredentials credentials) {

	// see if the credentials are for a transitional user
	final String userName = getPrincipalNameTransformer().transform(
		credentials.getUsername());
	final GcxUser user = gcxUserService
		.findTransitionalUserByEmail(userName);
	if (user != null) {
	    // check the password in the provided credentials
	    final PasswordEncoder pe = this.getPasswordEncoder();
	    final String password = pe.encode(credentials.getPassword());
	    final String storedPassword = pe.encode(user.getPassword());
	    if (password.equals(storedPassword)) {
		// matched, so activate this transitional user.
		log.info("Activating transitional user: " + userName);
		gcxUserService.activateTransitionalUser(user,
			Constants.SOURCEIDENTIFIER_ACTIVATION, userName);

		// return success for authentication
		return true;
	    }
	}

	// Default to not being authorized
	return false;
    }

    public void setGcxUserService(GcxUserService a_svc) {
	gcxUserService = a_svc;
    }
}
