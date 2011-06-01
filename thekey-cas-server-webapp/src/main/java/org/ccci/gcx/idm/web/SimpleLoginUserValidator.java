package org.ccci.gcx.idm.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.EmailValidator;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.validation.PasswordValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

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

    public boolean supports(final Class<?> clazz) {
	return SimpleLoginUser.class.isAssignableFrom(clazz);
    }
	
	private PasswordValidator pwvalid;
	private GcxUserService gcxuserservice;
	

	public void validate(Object command, Errors errors) 
	{
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password");
	}

    // THESE are used for the wizard validation...
    public void validateUsername(SimpleLoginUser user, Errors errors) {
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username",
		"required.username");

	// Do some additional validation if no errors have been encountered yet
	if (!errors.hasErrors()) {
	    final String userName = user.getUsername();

	    // is this a valid email address?
	    if (!EmailValidator.getInstance().isValid(userName)) {
		log.warn("We're going to reject this username because commons validator says it isn't valid ");
		errors.rejectValue("username", "invalid.username");
	    }
	    // does an account using the specified user name already exist?
	    else if (gcxuserservice.findUserByEmail(userName) != null) {
		errors.rejectValue("username", "duplicate.username");
	    }
	    // is there a legacy transitional user with the specified email
	    // address?
	    // TODO: this is deprecated functionality that needs to go away
	    // eventually
	    else {
		@SuppressWarnings("deprecation")
		final GcxUser tmpUser = gcxuserservice
			.findTransitionalUserByEmail(userName);
		if (tmpUser != null) {
		    errors.rejectValue("username", "duplicate.username");
		}
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
