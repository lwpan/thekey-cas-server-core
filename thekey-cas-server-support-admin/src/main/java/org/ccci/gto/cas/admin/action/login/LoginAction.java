package org.ccci.gto.cas.admin.action.login;

import static org.ccci.gto.cas.admin.Constants.SESSION_AUTHENTICATION;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.admin.action.AbstractAuditAction;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import javax.validation.constraints.NotNull;

/**
 * <b>LoginAction</b> handles requests related to the login and logout process.
 * 
 * @author Daniel Frett
 */
public class LoginAction extends
	AbstractAuditAction<TheKeyUsernamePasswordCredentials> {
    private static final long serialVersionUID = 279177251355422724L;

    @NotNull
    private AuthenticationManager authenticationManager;

    @NotNull
    private String adminGroupDn;

    public LoginAction() {
        final TheKeyUsernamePasswordCredentials creds = new TheKeyUsernamePasswordCredentials();
        creds.setObserveLock(TheKeyCredentials.Lock.FEDERATIONALLOWED, false);
        this.setModel(creds);
    }

    /**
     * @param authenticationManager
     *            the AuthenticationManager to use
     */
    public void setAuthenticationManager(
	    final AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }

    /**
     * @param adminGroupDn the adminGroupDn to set
     */
    public void setAdminGroupDn(final String adminGroupDn) {
	this.adminGroupDn = adminGroupDn;
    }

    /**
     * Prepare the login prompt for accepting the user credentials.
     * 
     * @return Result name.
     */
    public String prompt() {
	// Invalidate the session
	this.invalidateSession();
	return SUCCESS;
    }

    /**
     * Handle a login request.
     * 
     * @return Result name.
     */
    public String login() {
	final UsernamePasswordCredentials creds = this.getModel();
	if (log.isDebugEnabled()) {
	    log.debug("User logging in to admin interface: "
		    + creds.getUsername());
	}

	// Invalidate any existing session
	this.invalidateSession();

	try {
	    // authenticate the supplied credentials
	    final Authentication auth = this.authenticationManager
		    .authenticate(creds);
	    final GcxUser user = AuthenticationUtil.getUser(auth);

	    // throw an error if a valid user wasn't found
	    if (user == null) {
		log.error("Credentials authenticated, but a user wasn't found?!?!?");
		this.addActionError(this.getText("login.error.action.notadmin"));
		return ERROR;
	    }
	    if (!user.getGroupMembership().contains(this.adminGroupDn)) {
		log.error("User is not authorized to use the admin application");
		this.addActionError(this.getText("login.error.action.notadmin"));
		return ERROR;
	    }

	    // store the authentication response in the session
	    this.getSession().put(SESSION_AUTHENTICATION, auth);

	    return SUCCESS;
	} catch (AuthenticationException e) {
	    log.error("User authentication failed", e);
	    this.addActionError(this
		    .getText("login.error.action.missinguseridpassword"));
	    this.addFieldError("userid",
		    this.getText("login.error.missing.userid"));
	    this.addFieldError("passwd",
		    this.getText("login.error.missing.password"));
	    return ERROR;
	}
    }

    /**
     * Handle a logout request.
     * 
     * @return Result name.
     */
    public String logout() {
	if (log.isDebugEnabled()) {
	    final GcxUser user = AuthenticationUtil
		    .getUser((Authentication) this.getSession().get(
			    SESSION_AUTHENTICATION));
	    if (user != null) {
		log.debug("Logging off user: " + user.getEmail());
	    }
	}

	// Invalidate the session
	this.invalidateSession();

	return SUCCESS;
    }
}
