package org.ccci.gcx.idm.web;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_KEYUSER;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.EmailValidator;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

/**
 * Provides self-service use cases for the sso web application. Implemented as a
 * Spring webflow; see selfserviceflow.xml for configuration for this
 * controller.
 * 
 * @author Ken Burcham
 * @author Daniel Frett
 */
public class SelfServiceController {
	protected static final Log log = LogFactory.getLog(SelfServiceController.class);
	
	private GcxUserService gcxuserservice;


	/**
	 * triggers a resetPassword action for this user since they forgot their password.
	 * 
	 * @param user
	 * @return true if email is sent, false if it couldn't be sent because the mail wasn't found, etc.
	 * @throws Exception throws exception if no username was provided (which means spring validation broke)
	 */
	public boolean sendForgotPasswordEmail(SimpleLoginUser user,MessageContext context) throws Exception
	{
		if(StringUtils.isEmpty(user.getUsername()))
			throw new Exception("Email not provided! Validator didn't require a valid username.");

		try
		{
			GcxUser gcxuser = gcxuserservice.findUserByEmail(user.getUsername());
			gcxuserservice.resetPassword(gcxuser, Constants.SOURCEIDENTIFIER_FORGOTPASSWORD, user.getUsername());
		}
		catch (Exception e)
		{
			log.error("An exception ("+e.getMessage()+") occurred trying to find user ("
					+user.getUsername()+") and send a resetPassword message."+e.getMessage());

			context.addMessage(new MessageBuilder().error().source(null).code(Constants.ERROR_SENDFORGOTFAILED).build());

			return false;
		}
		
		return true;
	}

    // updates this user's details
    public boolean updateAccountDetails(final SimpleLoginUser form,
	    final MessageContext context) {
	// get a fresh user object before performing updates
	final GcxUser user = this.gcxuserservice.getFreshUser((GcxUser) form
		.getAuthentication().getAttributes().get(AUTH_ATTR_KEYUSER));
	if (user == null) {
	    context.addMessage(new MessageBuilder().error().source(null)
		    .code(Constants.ERROR_UPDATEFAILED).build());
	    return false;
	}
	if (log.isDebugEnabled()) {
	    log.debug("updating account details for: " + user.getGUID());
	}

	// a few processing flags
	final boolean changeEmail = !user.getEmail().equals(form.getUsername());
	final boolean changePassword = !changeEmail
		&& StringUtils.isNotBlank(form.getPassword())
		&& user.isPasswordAllowChange();

	// update the user object based on the form values
	user.setVerified(true);
	user.setFirstName(form.getFirstName());
	user.setLastName(form.getLastName());

	// when changing the email address, make sure the target email address
	// is valid
	if (changeEmail) {
	    // TODO: some of this validation should be handled by the
	    // SimpleLoginUserValidator and not by this method
	    final String email = form.getUsername();

	    // is this a valid email address?
	    boolean invalidEmail = false;
	    if (!EmailValidator.getInstance().isValid(email)) {
		log.error("We're going to reject this username because commons validator says it isn't valid ");
		invalidEmail = true;
	    }
	    // does an account using the specified email address already exist?
	    else if (!email.equalsIgnoreCase(user.getEmail())
		    && gcxuserservice.findUserByEmail(email) != null) {
		log.error("An error occurred: email already exists (" + email
			+ ") for (" + user.getGUID() + ")");
		invalidEmail = true;
	    }
	    // is there a legacy transitional user with the specified email
	    // address?
	    // TODO: this is deprecated functionality that needs to go away
	    // eventually
	    else {
		@SuppressWarnings("deprecation")
		final GcxUser tmpUser = gcxuserservice
			.findTransitionalUserByEmail(email);
		if (tmpUser != null) {
		    log.error("An error occurred: email already exists ("
			    + email + ") for (" + user.getGUID() + ")");
		    invalidEmail = true;
		}
	    }

	    // the specified email is invalid, throw an error and exit
	    if (invalidEmail) {
		context.addMessage(new MessageBuilder().error().source(null)
			.code(Constants.ERROR_UPDATEFAILED_EMAILEXISTS).build());
		return false;
	    }

	    // update the user object with the new email
	    user.setEmail(form.getUsername());
	    user.setUserid(user.getEmail());
	    user.setVerified(false);
	}

	// change the password if required
	if (changePassword) {
	    user.setPassword(form.getPassword());
	    user.setForcePasswordChange(false);
	}

	// save the updated user
	gcxuserservice.updateUser(user, changePassword,
		Constants.SOURCEIDENTIFIERUSERUPDATE, user.getGUID());

	// email changed, so trigger a password reset
	if (changeEmail) {
	    // send a new password.
	    log.debug("changed username so reset password.");
	    gcxuserservice.resetPassword(user,
		    Constants.SOURCEIDENTIFIERUSERUPDATE, user.getGUID());
	}

	// return an appropriate success message
	context.addMessage(new MessageBuilder()
		.code(changeEmail ? Constants.ACCOUNT_UPDATESUCCESS_RESETPASSWORD
			: Constants.ACCOUNT_UPDATESUCCESS).error().source(null)
		.build());
	return true;
    }

	public ArrayList<GcxUser> findUsers(SimpleLoginUser user)
	{
		ArrayList<GcxUser> users = new ArrayList<GcxUser>();
		HashSet<String> useremail = new HashSet<String>();

		try
		{
			if(StringUtils.isNotBlank(user.getFirstName())) {
				for(GcxUser u: gcxuserservice.findAllByFirstName(user.getFirstName())) {
					if(!useremail.contains(u.getEmail())) {
						useremail.add(u.getEmail());
						users.add(u);
					}
				}
			}

			if(StringUtils.isNotBlank(user.getLastName())) {
				for(GcxUser u: gcxuserservice.findAllByLastName(user.getLastName())) {
					if(!useremail.contains(u.getEmail())) {
						useremail.add(u.getEmail());
						users.add(u);
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn("findUsers exception.",e);
		}
		if(log.isDebugEnabled()) log.debug("************* RETURNING "+users.size());
		return users;
	}

	public String getTargetViewState(String destination)
	{
		if(destination == null)
		{
			destination = defaultViewState;
		}
		if(log.isDebugEnabled()) log.debug("RETURNING: "+destination+" AS our view!");
		return destination;
	}
	
	public SimpleLoginUser newUserModel()
	{
		return new SimpleLoginUser();
	}
	
	
    //via DI in Spring

    private String defaultViewState;
    public void setDefaultViewState(String a_str){
    	defaultViewState = a_str;
    }

    public void setGcxUserService(GcxUserService a_svc)
    {
    	this.gcxuserservice = a_svc;
    }
}
