package org.ccci.gcx.idm.web;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * SimpleLoginUserFlowValidator
 * 
 * validates view states for the webflow
 * 
 * @author Ken Burcham
 * @author Daniel Frett
 */
public class SimpleLoginUserFlowValidator  {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private GcxUserService gcxuserservice;
	private PasswordValidator pwvalidator;
	
	public void setPasswordValidator(PasswordValidator a_pwd)
	{
		pwvalidator = a_pwd;
	}
	
	public void setGcxUserService(GcxUserService a_svc)
	{
		gcxuserservice = a_svc;
	}
	
	public void validateDisplayForgotPassword(SimpleLoginUser user, Errors errors) 
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
	}
	
	public void validateDisplayForgotUsername(SimpleLoginUser user, Errors errors)
	{
		if(StringUtils.isBlank(user.getFirstName())  &&
		  (StringUtils.isBlank(user.getLastName())))
			errors.rejectValue(null, "required.firstnameorlastname");
	
	

		
	}
	
	/**
	 * validates the account maintenance sign-in page
	 * first, checks the username/password to see if they are present.
	 * second, if present, authenticates against gcxuserservice. 
	 *   if successful, sets names and isAuthenticated on simpleuserlogin model object, used in flow.
	 * @param user
	 * @param errors
	 */
	public void validateSelfServiceSignIn(SimpleLoginUser user, Errors errors) throws org.springframework.ldap.CommunicationException
	{
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password");

		//any other checking here...
		//errors.rejectValue("username","invalid.username");
		
		//try to authenticate
		if(!errors.hasErrors())
		{

			try
			{
		    	GcxUser gcxuser = gcxuserservice.findUserByEmail(user.getUsername());
		    	gcxuser.setPassword(user.getPassword());
		    	
		    	gcxuserservice.authenticate(gcxuser); //throws exception upon failure to authenticate.
		    	
		    	user.setAuthenticated(true);
		    	user.setFirstName(gcxuser.getFirstName());
				user.setLastName(gcxuser.getLastName());
		logger.debug("Authentication success.");
		    }
			catch (org.springframework.ldap.CommunicationException ce)
			{
		logger.error("Had a problem communicating with the server.", ce);
				throw ce;
			}
			catch (Exception e)
			{
		logger.error("ERROR: Couldn't authenticate. ", e);
				errors.rejectValue(null, "error.account.authenticationfailed");
			}		
		}
	if (logger.isDebugEnabled()) {
	    logger.debug("validateSelfServiceSignIn returning errors: "
		    + errors.getErrorCount());
	}
	}
	
	/**
	 * Validate a user's posted update values.  firstname and lastname are required. 
	 * if password is supplied, it must be acceptable and retyped correctly.
	 * @param user
	 * @param errors
	 */
	public void validateDisplayAccountDetails(SimpleLoginUser user, Errors errors)
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "required.firstName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "required.lastName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
		
		
	if (!errors.hasErrors() && StringUtils.isNotBlank(user.getPassword())) {
	    this.validateNewPassword(user, errors);
	}
		
	}
	
    public void validateViewChangeStalePasswordForm(final SimpleLoginUser user,
	    final Errors errors) {
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
		"required.password");

	if (!errors.hasErrors()) {
	    this.validateNewPassword(user, errors);
	}
    }

    private void validateNewPassword(final SimpleLoginUser user,
	    final Errors errors) {
	logger.debug("validating new password");

	if (!user.getPassword().equals(user.getRetypePassword())) {
	    logger.debug("passwords don't match");
	    errors.rejectValue("retypePassword", "mismatch.retypePassword");
	}

	if (!pwvalidator.isAcceptablePassword(user.getPassword())) {
	    logger.debug("oops, not an acceptable password.");
	    errors.rejectValue("password", "error.invalidpassword");
	}
    }
}
