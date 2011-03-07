package org.ccci.gcx.idm.web;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.util.HtmlUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.EmailValidator;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.validation.PasswordValidator;

/**
 * SimpleLoginUserValidator
 * 
 * validate is called by the login form
 * validateXXX are called by the signup wizard 
 * 
 * @author ken
 *
 */
public class SimpleLoginUserValidator implements Validator {

	protected static final Log log = LogFactory.getLog(SimpleLoginUserValidator.class);

	
	public boolean supports(Class arg0) {
		return SimpleLoginUser.class.equals(arg0);
	}
	
	private PasswordValidator pwvalid;
	private GcxUserService gcxuserservice;
	

	public void validate(Object command, Errors errors) 
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password");
	}
	
	
	// THESE are used for the wizard validation...
	public void validateUsername(SimpleLoginUser user, Errors errors)
	{	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
		
		//errors.rejectValue("username","invalid.username");
		
		if(!errors.hasErrors())
			if(!EmailValidator.getInstance().isValid(user.getUsername()))
			{
				log.warn("We're going to reject this username because commons validator says it isn't valid ");
				errors.rejectValue("username","invalid.username");
			}


		
		if(!errors.hasErrors())
		{
			GcxUser trialuser = gcxuserservice.findUserByEmail(user.getUsername());
			if(trialuser == null)
				trialuser = gcxuserservice.findTransitionalUserByEmail(user.getUsername());
			
			if(trialuser != null)
			{
				errors.rejectValue("username", "duplicate.username");
			}
		}
	}

	public void validatePassword(SimpleLoginUser user, Errors errors)
	{	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password");
		if(!errors.hasErrors())
			if (!pwvalid.isAcceptablePassword(user.getPassword()))
				errors.rejectValue("password", "error.invalidpassword");
	}

	public void validateRetypePassword(SimpleLoginUser user, Errors errors)
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "retypePassword", "required.retypePassword");
		
		if(!errors.hasErrors())
		{
			if(! user.getRetypePassword().equals(user.getPassword()))
			{
				errors.rejectValue("retypePassword", "mismatch.retypePassword");
			}
		}
	}
	
	
	public void validateSecurityQA(SimpleLoginUser user, Errors errors)
	{	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "securityQuestion", "required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "securityAnswer","required");
	}

	public void validateName(SimpleLoginUser user, Errors errors)
	{	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName","required");
	}
	
	public void setPasswordValidator (PasswordValidator a_pv)
	{
		pwvalid = a_pv;
	}
	public PasswordValidator getPasswordValidator()
	{
		return pwvalid;
	}
	public void setGcxUserService (GcxUserService a_svc)
	{
		gcxuserservice = a_svc;
	}
	public GcxUserService getGcxUserService()
	{
		return gcxuserservice;
	}

}
