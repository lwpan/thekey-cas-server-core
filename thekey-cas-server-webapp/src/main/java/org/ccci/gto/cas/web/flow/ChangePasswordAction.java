package org.ccci.gto.cas.web.flow;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.SimpleLoginUser;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordAction {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NotNull
    private GcxUserService gcxUserService;

    public void populateObject(final SimpleLoginUser user,
	    final UsernamePasswordCredentials credentials) {
	user.setUsername(credentials.getUsername());
    }

    public void save(final SimpleLoginUser user) {
	// only process change password request if an actual user exists
	GcxUser gcxUser = gcxUserService.findUserByEmail(user.getUsername());
	if (gcxUser != null) {
	    if (logger.isDebugEnabled()) {
		logger.debug("Changing password for: " + gcxUser.getGUID());
	    }

	    // set the password and disable the forcePasswordChange flag
	    gcxUser.setPassword(user.getPassword());
	    gcxUser.setForcePasswordChange(false);
	    gcxUser.setVerified(true);
	    gcxUserService.updateUser(gcxUser, true,
		    Constants.SOURCEIDENTIFIER_FORCECHANGEPASSWORD,
		    user.getUsername());

	    logger.debug("Looks like it was a success... now force a relogin (with the new password)");
	}
    }

    /**
     * @param gcxUserService
     *            the gcxUserService to set
     */
    public void setGcxUserService(final GcxUserService gcxUserService) {
	this.gcxUserService = gcxUserService;
    }
}
