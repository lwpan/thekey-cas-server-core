package me.thekey.cas.selfservice.web.flow;

import static org.ccci.gto.cas.Constants.ERROR_EMAILREQUIRED;
import static org.ccci.gto.cas.Constants.ERROR_FIRSTNAMEREQUIRED;
import static org.ccci.gto.cas.Constants.ERROR_INVALIDEMAIL;
import static org.ccci.gto.cas.Constants.ERROR_LASTNAMEREQUIRED;
import static org.ccci.gto.cas.Constants.ERROR_PASSWORDREQUIRED;
import static org.ccci.gto.cas.Constants.ERROR_UPDATEFAILED_EMAILEXISTS;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.ccci.gto.cas.selfservice.validator.PasswordValidator;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public final class SelfServiceValidator {
    private static final Logger LOG = LoggerFactory.getLogger(SelfServiceValidator.class);

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    @NotNull
    private AuthenticationManager authenticationManager;

    @NotNull
    private UserManager userManager;

    @NotNull
    private PasswordValidator passwordValidator;

    /**
     * @param manager
     *            the AuthenticationManager to use
     */
    public void setAuthenticationManager(final AuthenticationManager manager) {
        this.authenticationManager = manager;
    }

    /**
     * @param manager
     *            the UserManager to use
     */
    public void setUserManager(final UserManager manager) {
        this.userManager = manager;
    }

    public void setPasswordValidator(final PasswordValidator validator) {
        this.passwordValidator = validator;
    }

    private void validateEmail(final Errors errors, final String field) {
        final Object rawEmail = errors.getFieldValue(field);
        final String email = rawEmail != null ? rawEmail.toString() : null;

        // make sure this is a valid email address
        if (!EMAIL_VALIDATOR.isValid(email)) {
            LOG.error("We're going to reject this email because commons validator says it isn't valid ");
            errors.rejectValue(field, ERROR_INVALIDEMAIL);
        }
        // check for any existing accounts if there are no errors
        else if (this.userManager.doesEmailExist(email)) {
            LOG.error("An error occurred: email already exists (" + email + ")");
            errors.rejectValue(field, ERROR_UPDATEFAILED_EMAILEXISTS);
        }
    }

    private void validatePassword(final Errors errors, final String field, final String retypeField) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, ERROR_PASSWORDREQUIRED);

        // get the password values
        final Object rawPassword = errors.getFieldValue(field);
        final String password = rawPassword != null ? rawPassword.toString() : null;
        final Object rawRetypePassword = errors.getFieldValue(field);
        final String retypePassword = rawRetypePassword != null ? rawRetypePassword.toString() : null;

        if (!errors.hasFieldErrors(field)) {
            if (!this.passwordValidator.isAcceptablePassword(password)) {
                errors.rejectValue(field, "error.invalidpassword");
            }
        }

        if (password != null && !password.equals(retypePassword)) {
            LOG.debug("passwords don't match");
            errors.rejectValue(retypeField, "mismatch.retypePassword");
        }

    }

    /* login-webflow validation methods */

    public void validateViewChangePasswordForm(final SelfServiceModel model, final Errors errors) {
        this.validatePassword(errors, "password", "retypePassword");
    }

    /* selfservice-webflow validation methods */

    public void validateSignup(final SelfServiceModel model, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", ERROR_FIRSTNAMEREQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", ERROR_LASTNAMEREQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", ERROR_EMAILREQUIRED);
        if (!errors.hasFieldErrors("email")) {
            this.validateEmail(errors, "email");
        }
        this.validatePassword(errors, "password", "retypePassword");
    }

    /**
     * validate the forgotPassword view
     * 
     * @param model
     * @param errors
     */
    public void validateForgotPassword(final SelfServiceModel model, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", ERROR_EMAILREQUIRED);
    }

    public void validateResetPassword(final SelfServiceModel model, final Errors errors) {
        this.validatePassword(errors, "password", "retypePassword");
    }

    /**
     * this method validates an authentication request for the self service
     * controller
     * 
     * @param model
     * @param errors
     */
    public void validateAuthenticate(final SelfServiceModel model, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", ERROR_EMAILREQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", ERROR_PASSWORDREQUIRED);

        // try to authenticate unless we have errors
        if (!errors.hasErrors()) {
            // generate a credentials object
            final TheKeyUsernamePasswordCredentials credentials = new TheKeyUsernamePasswordCredentials();
            credentials.setUsername(model.getEmail());
            credentials.setPassword(model.getPassword());
            credentials.setObserveLock(Lock.STALEPASSWORD, false);
            credentials.setObserveLock(Lock.VERIFIED, false);

            // attempt to authenticate
            try {
                final Authentication auth = this.authenticationManager.authenticate(credentials);

                // populate the data object
                final GcxUser user = AuthenticationUtil.getUser(auth);
                model.setAuthentication(auth);
                model.setEmail(user.getEmail());
                model.setFirstName(user.getFirstName());
                model.setLastName(user.getLastName());
            } catch (final AuthenticationException e) {
                errors.reject(e.getCode());
            }
        } else {
            LOG.debug("validateAuthenticate returning errors: {}", errors.getErrorCount());
        }
    }

    /**
     * Validate a user's posted update values. firstname and lastname are
     * required. if password is supplied, it must be acceptable and retyped
     * correctly.
     * 
     * @param model
     * @param errors
     */
    public void validateAccountDetails(final SelfServiceModel model, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", ERROR_EMAILREQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", ERROR_FIRSTNAMEREQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", ERROR_LASTNAMEREQUIRED);

        // validate a changed email address
        if (!errors.hasErrors()) {
            final GcxUser user = AuthenticationUtil.getUser(model.getAuthentication());
            if (!user.getEmail().equalsIgnoreCase(model.getEmail())) {
                this.validateEmail(errors, "email");
            }
        }

        // validate any new password only if there are no other errors
        if (!errors.hasErrors() && StringUtils.isNotEmpty(model.getPassword())) {
            this.validatePassword(errors, "password", "retypePassword");
        }
    }
}
