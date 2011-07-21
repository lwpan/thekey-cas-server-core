package org.ccci.gto.cas.admin.action;

import static org.ccci.gcx.idm.web.admin.Constants.SESSION_STATUS_MESSAGE;
import static org.ccci.gto.cas.admin.Constants.SESSION_AUTHENTICATION;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;

/**
 * <b>AbstractUserAction</b> contains the common functionality used by
 * {@link Action}'s that require {@link GcxUserService} related functions.
 * 
 * @author Greg Crider Nov 6, 2008 4:15:06 PM
 */
public abstract class AbstractUserAction extends AbstractAuditAction<GcxUser> {
    private static final long serialVersionUID = -7667374651441831492L;

    @NotNull
    private GcxUserService userService;

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

    /**
     * We can override the prepare step if there are some resources that need to
     * be setup prior to the action running.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    public void prepare() throws Exception {
	super.prepare();

	/*
	 * Clear out any status messages that were previously set. That way an
	 * action implementation doesn't have to worry about clearing them out
	 * upon entry.
	 */
	this.getSession().remove(SESSION_STATUS_MESSAGE);
    }

    /**
     * Convenience method that returns the currently authenticated user
     * 
     * @return the currently authenticated user
     */
    protected GcxUser getAuthenticatedUser() {
	final Authentication auth = (Authentication) this.getSession().get(
		SESSION_AUTHENTICATION);
	if (auth != null) {
	    return AuthenticationUtil.getUser(auth);
	}
	return null;
    }

    /**
     * method that returns the current GcxUser model object for this Action.
     * This is necessary due to how struts2 handles generics
     * 
     * @return
     */
    public GcxUser getGcxUser() {
	return super.getModel();
    }

    /**
     * method that sets the GcxUser model object for this Action. This is
     * necessary due to how struts2 handles generics
     * 
     * @param user
     */
    public void setGcxUser(final GcxUser user) {
	super.setModel(user);
    }
}
