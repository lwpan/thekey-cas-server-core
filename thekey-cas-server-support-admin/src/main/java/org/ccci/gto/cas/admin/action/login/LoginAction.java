package org.ccci.gto.cas.admin.action.login;

import static org.ccci.gcx.idm.web.admin.Constants.SESSION_AUTHENTICATED_USER;

import org.ccci.gcx.idm.core.GcxUserAccountLockedException;
import org.ccci.gcx.idm.core.GcxUserException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.admin.action.AbstractUserAction;

/**
 * <b>LoginAction</b> handles requests related to the login and logout process.
 * 
 * @author Greg Crider Nov 6, 2008 6:22:35 PM
 */
public class LoginAction extends AbstractUserAction {
    private static final long serialVersionUID = 279177251355422724L;

    /**
     * Prepare the login prompt for accepting the user credentials.
     * 
     * @return Result name.
     */
    public String prompt() {
	// Invalidate the session
	this.invalidate();

	return SUCCESS;
    }

    /**
     * Handle a login request.
     * 
     * @return Result name.
     */
    public String login() {
	String result = SUCCESS;

	if (log.isDebugEnabled()) {
	    log.debug("Requested user: (" + this.getModel().getEmail() + ")");
	}

	try {
	    // Authenticate user
	    this.getGcxUserService().authenticate(this.getModel());

	    // Invalidate the session
	    this.invalidate();
	    log.trace("***** New HTTP session started");

	    // If we made it this far, retrieve the user object
	    final GcxUser user = this.getGcxUserService().findUserByEmail(
		    this.getModel().getEmail());
	    // Test to see if the user is authorized as an admin
	    if (!this.getGcxUserService().isUserInAdminGroup(user)) {
		this.addActionError(this.getText("login.error.action.notadmin"));
		result = ERROR;
		log.error("User is not authorized to use the admin application");
	    }
	    // Save the user in the session
	    this.getSession().put(SESSION_AUTHENTICATED_USER, user);
	} catch (GcxUserAccountLockedException e) {
	    log.error("User has been administratively locked ... bye, bye!");
	    throw e;
	} catch (GcxUserException e) {
	    this.addActionError(this
		    .getText("login.error.action.missinguseridpassword"));
	    this.addFieldError("userid",
		    this.getText("login.error.missing.userid"));
	    this.addFieldError("passwd",
		    this.getText("login.error.missing.password"));
	    result = ERROR;
	    log.error("User authentication failed", e);
	}

	return result;
    }

    /**
     * Handle a logout request.
     * 
     * @return Result name.
     */
    public String logout() {
	final GcxUser user = (GcxUser) this.getSession().get(
		SESSION_AUTHENTICATED_USER);

	if (user == null) {
	    log.error("There is no user object in the current session");
	} else if (log.isDebugEnabled()) {
	    log.debug("Logging off user: " + user.getEmail());
	}

	// Invalidate the session
	this.invalidate();

	return SUCCESS;
    }
}
