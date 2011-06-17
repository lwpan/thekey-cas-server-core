package org.ccci.gto.cas.selfservice;

import static org.ccci.gto.cas.Constants.ERROR_UPDATEFAILED_NOUSER;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORCECHANGEPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORGOTPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_SIGNUP;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_USERUPDATE;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_SENDFORGOTFAILED;
import static org.ccci.gto.cas.selfservice.Constants.MESSAGE_UPDATESUCCESS;
import static org.ccci.gto.cas.selfservice.Constants.MESSAGE_UPDATESUCCESS_RESETPASSWORD;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.ccci.gto.cas.util.RandomGUID;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

/**
 * main Controller for the Self Service webflow
 * 
 * @author Daniel Frett
 */
public class SelfServiceController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NotNull
    private GcxUserService userService;

    public void setUserService(final GcxUserService userService) {
	this.userService = userService;
    }

    /**
     * triggers a resetPassword action for this user since they forgot their
     * password.
     * 
     * @param data
     */
    public boolean sendForgotPasswordEmail(final SelfServiceUser data,
	    final MessageContext messages) {
	final String email = data.getEmail();
	try {
	    final GcxUser user = userService.findUserByEmail(email);
	    userService.resetPassword(user, AUDIT_SOURCE_FORGOTPASSWORD, email);
	} catch (Exception e) {
	    logger.error("An exception (" + e.getMessage()
		    + ") occurred trying to find user (" + email
		    + ") and send a resetPassword message." + e.getMessage());
	    messages.addMessage(new MessageBuilder().error().source(null)
		    .code(ERROR_SENDFORGOTFAILED).build());

	    return false;
	}

	return true;
    }

    /**
     * Update the current user's details
     * 
     * @param data
     * @param messages
     * @return
     */
    public boolean updateAccountDetails(final SelfServiceUser data,
	    final MessageContext messages) {
	// get a fresh user object before performing updates
	final GcxUser user = this.userService.getFreshUser(AuthenticationUtil
		.getUser(data.getAuthentication()));
	if (user == null) {
	    messages.addMessage(new MessageBuilder().error().source(null)
		    .code(ERROR_UPDATEFAILED_NOUSER).build());
	    return false;
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("updating account details for: " + user.getGUID());
	}

	// a few processing flags
	final boolean changeEmail = !user.getEmail().equals(data.getEmail());
	final boolean changePassword = !changeEmail
		&& StringUtils.isNotBlank(data.getPassword())
		&& user.isPasswordAllowChange();

	// update the user object based on the form values
	user.setFirstName(data.getFirstName());
	user.setLastName(data.getLastName());

	// change the password if required
	if (changePassword) {
	    user.setPassword(data.getPassword());
	    user.setForcePasswordChange(false);
	    user.setVerified(true);
	}

	// change the email if required
	if (changeEmail) {
	    // update the user object with the new email
	    user.setEmail(data.getEmail());
	    user.setUserid(user.getEmail());
	    user.setVerified(false);
	}

	// save the updated user
	userService.updateUser(user, changePassword, AUDIT_SOURCE_USERUPDATE,
		user.getGUID());

	// email changed, so trigger a password reset
	if (changeEmail) {
	    // send a new password.
	    logger.debug("changed username so reset password.");
	    userService.resetPassword(user, AUDIT_SOURCE_USERUPDATE,
		    user.getGUID());
	}

	// return an appropriate success message
	messages.addMessage(new MessageBuilder()
		.code(changeEmail ? MESSAGE_UPDATESUCCESS_RESETPASSWORD
			: MESSAGE_UPDATESUCCESS).error().source(null).build());
	return true;
    }

    public void populateFromCredentials(final SelfServiceUser data,
	    final UsernamePasswordCredentials credentials) {
	data.setEmail(credentials.getUsername());
    }

    public boolean processSignup(final SelfServiceUser data,
	    final MessageContext messages) {
	// generate a new GcxUser object
	final GcxUser user = new GcxUser();
	user.setGUID(RandomGUID.generateGuid(true));
	user.setEmail(data.getEmail());
	user.setFirstName(data.getFirstName());
	user.setLastName(data.getLastName());
	user.setPasswordAllowChange(true);
	user.setForcePasswordChange(true);
	user.setLoginDisabled(false);
	user.setVerified(false);

	// create the new user in the GcxUserService
	if (logger.isInfoEnabled()) {
	    logger.info("***** User: " + user);
	    logger.info("***** Preparing to create through service");
	}
	this.userService.createUser(user, AUDIT_SOURCE_SIGNUP);

	// return success
	return true;
    }

    public void updatePassword(final SelfServiceUser data) {
	// only process change password request if an actual user exists
	final GcxUser user = this.userService.findUserByEmail(data.getEmail());
	if (user != null) {
	    if (logger.isDebugEnabled()) {
		logger.debug("Changing password for: " + user.getGUID());
	    }

	    // set the password and disable the forcePasswordChange flag
	    user.setPassword(data.getPassword());
	    user.setForcePasswordChange(false);
	    user.setVerified(true);
	    this.userService.updateUser(user, true,
		    AUDIT_SOURCE_FORCECHANGEPASSWORD, data.getEmail());

	    logger.debug("Looks like it was a success... now force a relogin (with the new password)");
	}
    }
}
