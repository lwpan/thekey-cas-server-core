package org.ccci.gcx.idm.web;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.validation.PasswordValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;


/**
 * SimpleLoginUserFlowValidator
 * 
 * validates view states for the webflow
 * 
 * @author ken
 *
 */
public class SimpleLoginUserFlowValidator  {
	
	protected static final Log log = LogFactory.getLog(SimpleLoginUserFlowValidator.class);
	
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
				if(log.isDebugEnabled()) log.debug("Authentication success.");
				
		    }
			catch (org.springframework.ldap.CommunicationException ce)
			{
				log.error("Had a problem communicating with the server.",ce);
				throw ce;
			}
			catch (Exception e)
			{
				log.error("ERROR: Couldn't authenticate:  "+e.getMessage());
				errors.rejectValue(null, "error.account.authenticationfailed");
			}		
		}
		if(log.isDebugEnabled()) log.debug("validateSelfServiceSignIn returning errors: "+errors.getErrorCount());
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
		
		
		if(!errors.hasErrors())
		{
			//if they're trying to set their password then there will be a value in getpassword. otherwise ignore.
			if(StringUtils.isNotBlank(user.getPassword()))
			{
				if(log.isDebugEnabled()) log.debug("setting password"+user.getPassword());
				if(!user.getPassword().equals(user.getRetypePassword()))
				{
					if(log.isDebugEnabled()) log.debug("have a typing match error");
					errors.rejectValue("retypePassword", "mismatch.retypePassword");
				}
				else
				{
					if(log.isDebugEnabled()) log.debug("Ok, ready to change their password.");
					if(!pwvalidator.isAcceptablePassword(user.getPassword()))
					{
						if(log.isDebugEnabled()) log.debug("oops, not acceptable.");
						errors.rejectValue("password", "error.invalidpassword");
					}
				}
					
			}

		}
		
	}
	
	
}
