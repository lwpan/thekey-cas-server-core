package org.ccci.gto.cas.selfservice;

import static org.ccci.gto.cas.Constants.ERROR_UPDATEFAILED_NOUSER;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORCECHANGEPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORGOTPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_SIGNUP;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_USERUPDATE;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_SENDFORGOTFAILED;
import static org.ccci.gto.cas.selfservice.Constants.FLOW_MODEL_SELFSERVICEUSER;
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
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * main Controller for the Self Service webflow
 * 
 * @author Daniel Frett
 */
public class SelfServiceController extends MultiAction {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NotNull
    private GcxUserService userService;

    public void setUserService(final GcxUserService userService) {
	this.userService = userService;
    }

    private SelfServiceUser getModel(final RequestContext context) {
	return (SelfServiceUser) context.getFlowScope().get(
		FLOW_MODEL_SELFSERVICEUSER);
    }

    /**
     * triggers a resetPassword action for this user since they forgot their
     * password.
     * 
     * @param data
     */
    public Event sendForgotPasswordEmail(final RequestContext context) {
	final SelfServiceUser model = getModel(context);

	final String email = model.getEmail();
	try {
	    final GcxUser user = userService.findUserByEmail(email);
	    userService.resetPassword(user, AUDIT_SOURCE_FORGOTPASSWORD, email);
	} catch (Exception e) {
	    logger.error("An exception (" + e.getMessage()
		    + ") occurred trying to find user (" + email
		    + ") and send a resetPassword message." + e.getMessage());
	    context.getMessageContext().addMessage(
		    new MessageBuilder().error().source(null)
			    .code(ERROR_SENDFORGOTFAILED).build());

	    return error();
	}

	return success();
    }

    /**
     * Update the current user's details
     * 
     * @param context
     * @return
     */
    public Event updateAccountDetails(final RequestContext context) {
	final SelfServiceUser model = this.getModel(context);

	// get a fresh user object before performing updates
	final GcxUser user = this.userService.getFreshUser(model.getUser());
	if (user == null) {
	    context.getMessageContext().addMessage(
		    new MessageBuilder().error().source(null)
			    .code(ERROR_UPDATEFAILED_NOUSER).build());
	    return error();
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("updating account details for: " + user.getGUID());
	}

	// a few processing flags
	final boolean changeEmail = !user.getEmail().equals(model.getEmail());
	final boolean changePassword = !changeEmail
		&& StringUtils.isNotBlank(model.getPassword())
		&& user.isPasswordAllowChange();

	// update the user object based on the form values
	user.setFirstName(model.getFirstName());
	user.setLastName(model.getLastName());

	// change the password if required
	if (changePassword) {
	    user.setPassword(model.getPassword());
	    user.setForcePasswordChange(false);
	    user.setVerified(true);
	}

	// change the email if required
	if (changeEmail) {
	    // update the user object with the new email
	    user.setEmail(model.getEmail());
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
	context.getMessageContext().addMessage(
		new MessageBuilder()
			.code(changeEmail ? MESSAGE_UPDATESUCCESS_RESETPASSWORD
				: MESSAGE_UPDATESUCCESS).source(null).error()
			.build());
	return success();
    }

    public Event processSignup(final RequestContext context) {
	final SelfServiceUser model = this.getModel(context);

	// generate a new GcxUser object
	final GcxUser user = new GcxUser();
	user.setGUID(RandomGUID.generateGuid(true));
	user.setEmail(model.getEmail());
	user.setFirstName(model.getFirstName());
	user.setLastName(model.getLastName());
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
	return success();
    }

    public Event populateFromCredentials(final SelfServiceUser model,
	    final UsernamePasswordCredentials credentials) {
	model.setEmail(credentials.getUsername());
	return success();
    }

    public Event updatePassword(final SelfServiceUser model) {
	// throw an error if the user can't be found???
	final GcxUser user = this.userService.findUserByEmail(model.getEmail());
	if (user == null) {
	    return error();
	}

	// set the password and disable the forcePasswordChange flag
	if (logger.isDebugEnabled()) {
	    logger.debug("Changing password for: " + model.getEmail());
	}
	user.setPassword(model.getPassword());
	user.setForcePasswordChange(false);
	user.setVerified(true);
	this.userService.updateUser(user, true,
		AUDIT_SOURCE_FORCECHANGEPASSWORD, model.getEmail());

	// return success
	logger.debug("Looks like it was a success... now force a relogin (with the new password)");
	return success();
    }
}
