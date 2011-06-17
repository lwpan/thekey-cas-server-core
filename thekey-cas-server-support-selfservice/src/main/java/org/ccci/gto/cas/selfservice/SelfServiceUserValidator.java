package org.ccci.gto.cas.selfservice;

import static org.ccci.gto.cas.Constants.ERROR_INVALIDEMAIL;
import static org.ccci.gto.cas.Constants.ERROR_UPDATEFAILED_EMAILEXISTS;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.validation.PasswordValidator;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator for the Self Service webflow
 * 
 * @author Daniel Frett
 */
public class SelfServiceUserValidator {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NotNull
    private AuthenticationManager authenticationManager;

    @NotNull
    private GcxUserService userService;

    private final EmailValidator emailValidator = EmailValidator.getInstance();
    private PasswordValidator passwordValidator;

    /**
     * @param authenticationManager
     *            the AuthenticationManager to use
     */
    public void setAuthenticationManager(
	    final AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }

    public void setPasswordValidator(final PasswordValidator validator) {
	this.passwordValidator = validator;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final GcxUserService userService) {
	this.userService = userService;
    }

    @SuppressWarnings("deprecation")
    private void validateNewEmail(final SelfServiceUser data,
	    final Errors errors) {
	final String email = data.getEmail();

	// make sure this is a valid email address
	if (!this.emailValidator.isValid(email)) {
	    logger.error("We're going to reject this email because commons validator says it isn't valid ");
	    errors.rejectValue("email", ERROR_INVALIDEMAIL);
	}
	// check for any existing accounts if there are no errors
	else if (this.userService.findUserByEmail(email) != null
		|| this.userService.findTransitionalUserByEmail(email) != null) {
	    logger.error("An error occurred: email already exists (" + email
		    + ")");
	    errors.rejectValue("email", ERROR_UPDATEFAILED_EMAILEXISTS);
	}
    }

    private void validateNewPassword(final SelfServiceUser data,
	    final Errors errors) {
	logger.debug("validating new password");

	if (!data.getPassword().equals(data.getRetypePassword())) {
	    logger.debug("passwords don't match");
	    errors.rejectValue("retypePassword", "mismatch.retypePassword");
	}

	if (!passwordValidator.isAcceptablePassword(data.getPassword())) {
	    logger.debug("oops, not an acceptable password.");
	    errors.rejectValue("password", "error.invalidpassword");
	}
    }

    /**
     * this method validates an authentication request for the self service
     * controller
     * 
     * @param data
     * @param errors
     */
    public void validateAuthenticate(final SelfServiceUser data,
	    final Errors errors) {
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
		"required.username");
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
		"required.password");

	// try to authenticate unless we have errors
	if (!errors.hasErrors()) {
	    // generate a credentials object
	    final TheKeyUsernamePasswordCredentials credentials = new TheKeyUsernamePasswordCredentials();
	    credentials.setUsername(data.getEmail());
	    credentials.setPassword(data.getPassword());
	    credentials.setObserveLock(Lock.STALEPASSWORD, false);

	    // attempt to authenticate
	    try {
		final Authentication auth = this.authenticationManager
			.authenticate(credentials);

		// populate the data object
		final GcxUser user = AuthenticationUtil.getUser(auth);
		data.setAuthentication(auth);
		data.setEmail(user.getEmail());
		data.setFacebookId(user.getFacebookId());
		data.setFirstName(user.getFirstName());
		data.setLastName(user.getLastName());
	    } catch (Exception e) {
		errors.rejectValue(null, "error.account.authenticationfailed");
	    }
	}

	if (logger.isDebugEnabled()) {
	    logger.debug("validateAuthenticate returning errors: "
		    + errors.getErrorCount());
	}
    }

    /**
     * Validate a user's posted update values. firstname and lastname are
     * required. if password is supplied, it must be acceptable and retyped
     * correctly.
     * 
     * @param data
     * @param errors
     */
    public void validateAccountDetails(final SelfServiceUser data,
	    final Errors errors) {
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
		"required.username");
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
		"required.firstName");
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
		"required.lastName");

	// validate a changed email address
	if (!errors.hasErrors()) {
	    final GcxUser user = AuthenticationUtil.getUser(data
		    .getAuthentication());
	    if (!user.getEmail().equalsIgnoreCase(data.getEmail())) {
		this.validateNewEmail(data, errors);
	    }
	}

	// validate any new password
	if (!errors.hasErrors() && StringUtils.isNotBlank(data.getPassword())) {
	    this.validateNewPassword(data, errors);
	}
    }

    /**
     * validate the forgotPassword view
     * 
     * @param data
     * @param errors
     */
    public void validateForgotPassword(final SelfServiceUser data,
	    final Errors errors) {
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
		"required.username");
    }

    public void validateViewChangePasswordForm(final SelfServiceUser data,
	    final Errors errors) {
	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
		"required.password");

	if (!errors.hasErrors()) {
	    this.validateNewPassword(data, errors);
	}
    }
}
