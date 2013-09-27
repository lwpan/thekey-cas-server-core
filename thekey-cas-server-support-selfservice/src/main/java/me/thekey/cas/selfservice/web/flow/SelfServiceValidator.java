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

    private void validateNewEmail(final SelfServiceModel model, final Errors errors) {
        final String email = model.getEmail();

        // make sure this is a valid email address
        if (!EMAIL_VALIDATOR.isValid(email)) {
            LOG.error("We're going to reject this email because commons validator says it isn't valid ");
            errors.rejectValue("email", ERROR_INVALIDEMAIL);
        }
        // check for any existing accounts if there are no errors
        else if (this.userManager.findUserByEmail(email) != null) {
            LOG.error("An error occurred: email already exists (" + email + ")");
            errors.rejectValue("email", ERROR_UPDATEFAILED_EMAILEXISTS);
        }
    }

    private void validateNewPassword(final SelfServiceModel model, final Errors errors) {
        LOG.debug("validating new password");

        if (!model.getPassword().equals(model.getRetypePassword())) {
            LOG.debug("passwords don't match");
            errors.rejectValue("retypePassword", "mismatch.retypePassword");
        }

        if (!passwordValidator.isAcceptablePassword(model.getPassword())) {
            LOG.debug("oops, not an acceptable password.");
            errors.rejectValue("password", "error.invalidpassword");
        }
    }

    /* login-webflow validation methods */

    public void validateViewChangePasswordForm(final SelfServiceModel model, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", ERROR_PASSWORDREQUIRED);

        if (!errors.hasErrors()) {
            this.validateNewPassword(model, errors);
        }
    }

    /* selfservice-webflow validation methods */

    public void validateSignup(final SelfServiceModel model, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", ERROR_FIRSTNAMEREQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", ERROR_LASTNAMEREQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", ERROR_EMAILREQUIRED);
        if (!errors.hasFieldErrors("email")) {
            this.validateNewEmail(model, errors);
        }
        this.validateNewPassword(model, errors);
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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", ERROR_PASSWORDREQUIRED);

        if (!errors.hasErrors()) {
            this.validateNewPassword(model, errors);
        }
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
                this.validateNewEmail(model, errors);
            }
        }

        // validate any new password
        if (!errors.hasErrors() && StringUtils.isNotBlank(model.getPassword())) {
            this.validateNewPassword(model, errors);
        }
    }
}
