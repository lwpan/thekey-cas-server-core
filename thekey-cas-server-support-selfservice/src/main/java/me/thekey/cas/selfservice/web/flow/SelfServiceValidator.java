package me.thekey.cas.selfservice.web.flow;

import static org.ccci.gto.cas.Constants.ERROR_PASSWORDREQUIRED;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

import org.ccci.gto.cas.selfservice.validator.PasswordValidator;
import org.jasig.cas.authentication.AuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public final class SelfServiceValidator {
    private static final Logger LOG = LoggerFactory.getLogger(SelfServiceValidator.class);

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

    public void validateResetPassword(final SelfServiceModel model, final Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", ERROR_PASSWORDREQUIRED);

        if (!errors.hasErrors()) {
            this.validateNewPassword(model, errors);
        }
    }
}
