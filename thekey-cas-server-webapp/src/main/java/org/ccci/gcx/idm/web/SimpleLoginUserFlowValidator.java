package org.ccci.gcx.idm.web;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_KEYUSER;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.validation.PasswordValidator;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
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
	
    @NotNull
    private AuthenticationManager authenticationManager;

	private PasswordValidator pwvalidator;

    /**
     * @param authenticationManager
     *            the AuthenticationManager to use
     */
    public void setAuthenticationManager(
	    final AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }

    /**
     * @return the authenticationManager
     */
    public AuthenticationManager getAuthenticationManager() {
	return authenticationManager;
    }

	public void setPasswordValidator(PasswordValidator a_pwd)
	{
		pwvalidator = a_pwd;
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
     * validates the account maintenance sign-in page first, checks the
     * username/password to see if they are present. second, if present,
     * authenticates against gcxuserservice. if successful, sets names and
     * isAuthenticated on simpleuserlogin model object, used in flow.
     * 
     * @param form
     * @param errors
     */
    public void validateSelfServiceSignIn(final SimpleLoginUser form,
	    final Errors errors) {
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username",
		"required.username");
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
		"required.password");

	// try to authenticate
	if (!errors.hasErrors()) {
	    // generate a credentials object
	    final TheKeyUsernamePasswordCredentials credentials = new TheKeyUsernamePasswordCredentials();
	    credentials.setUsername(form.getUsername());
	    credentials.setPassword(form.getPassword());
	    credentials.setObserveLocks(false);

	    // attempt to authenticate
	    try {
		final Authentication auth = this.authenticationManager
			.authenticate(credentials);
		form.setAuthentication(auth);

		final GcxUser user = (GcxUser) auth.getAttributes().get(
			AUTH_ATTR_KEYUSER);
		form.setUsername(user.getEmail());
		form.setFirstName(user.getFirstName());
		form.setLastName(user.getLastName());
	    } catch (Exception e) {
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
