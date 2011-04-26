package org.ccci.gcx.idm.web;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;


/**
 * SimpleLoginUserForcePasswordValidator
 * 
 * validate is called by the force password form
 * we make sure that the password is acceptable and retyped correctly. 
 * 
 * @author ken
 *
 */
public class SimpleLoginUserForcePasswordValidator extends SimpleLoginUserValidator {


	public void validate(Object command, Errors errors) 
	{
		SimpleLoginUser user = (SimpleLoginUser) command;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "retypePassword", "required.retypePassword");

		if(!errors.hasErrors())
		{
			//password is present... is it acceptable?
			this.validatePassword(user, errors);
			this.validateRetypePassword(user, errors);
		}
	}
	


}
