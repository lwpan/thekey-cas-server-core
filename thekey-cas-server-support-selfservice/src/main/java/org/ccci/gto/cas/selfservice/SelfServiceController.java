package org.ccci.gto.cas.selfservice;

import static me.thekey.cas.selfservice.Constants.FLOW_MODEL_SELFSERVICE;
import static me.thekey.cas.selfservice.Constants.MESSAGE_UPDATE_SUCCESS;
import static me.thekey.cas.selfservice.Constants.MESSAGE_UPDATE_SUCCESS_CHANGEEMAIL;
import static org.ccci.gto.cas.Constants.ERROR_UPDATEFAILED_NOUSER;
import static org.ccci.gto.cas.Constants.STRENGTH_FULL;
import static org.ccci.gto.cas.Constants.VIEW_ATTR_COMMONURIPARAMS;
import static org.ccci.gto.cas.Constants.VIEW_ATTR_LOCALE;
import static org.ccci.gto.cas.facebook.Constants.PARAMETER_SIGNED_REQUEST;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_SENDFORGOTFAILED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import me.thekey.cas.selfservice.service.NotificationManager;
import me.thekey.cas.selfservice.web.flow.SelfServiceModel;
import me.thekey.cas.service.UserManager;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.federation.FederationProcessor;
import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.ccci.gto.cas.relay.util.RelayUtil;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.util.RandomStringGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * main Controller for the Self Service webflow
 * 
 * @author Daniel Frett
 */
public class SelfServiceController extends MultiAction {
    private static final Logger LOG = LoggerFactory.getLogger(SelfServiceController.class);

    @NotNull
    private AuthenticationManager authenticationManager;

    private final ArrayList<FederationProcessor> federatedProcessors = new ArrayList<FederationProcessor>();

    @NotNull
    private UserManager userManager;

    @NotNull
    private NotificationManager notificationManager;

    @NotNull
    private RandomStringGenerator keyGenerator;

    /**
     * @param authenticationManager
     *            the AuthenticationManager to use
     */
    public void setAuthenticationManager(
	    final AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }

    public void setFederatedProcessors(final Collection<FederationProcessor> federatedProcessors) {
        this.federatedProcessors.clear();
        if (federatedProcessors != null) {
            this.federatedProcessors.addAll(federatedProcessors);
        }
    }

    public void setUserService(final UserManager manager) {
	this.userManager = manager;
    }

    public final void setNotificationManager(final NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public final void setKeyGenerator(final RandomStringGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    private SelfServiceModel getSelfServiceModel(final RequestContext context) {
        // check for the SelfServiceModel object
        final Object model = context.getFlowScope().get(FLOW_MODEL_SELFSERVICE);
        if (model instanceof SelfServiceModel) {
            return (SelfServiceModel) model;
        }

        // a valid object wasn't found
        return null;
    }

    /**
     * triggers a resetPassword action for this user since they forgot their
     * password.
     * 
     * @param data
     */
    public Event sendForgotPasswordEmail(final RequestContext context) {
        final SelfServiceModel model = this.getSelfServiceModel(context);
        final String email = model.getEmail();
        final GcxUser user = this.userManager.findUserByEmail(email);

        if (user != null) {
            try {
                // generate a reset password key if we don't already have one (this is cleared when the user resets
                // their password or changes their email address)
                if (StringUtils.isBlank(user.getResetPasswordKey())) {
                    user.setResetPasswordKey(this.keyGenerator.getNewString());
                    this.userManager.updateUser(user);
                }
            } catch (final GcxUserNotFoundException e) {
                LOG.error("An exception occured trying to generate a reset password key for email: {}", email, e);
                context.getMessageContext().addMessage(
                        new MessageBuilder().error().source(null).code(ERROR_SENDFORGOTFAILED).build());
                return error();
            }

            // send the reset password notification
            final Object locale = context.getRequestScope().get(VIEW_ATTR_LOCALE);
            final Object uriParams = context.getRequestScope().get(VIEW_ATTR_COMMONURIPARAMS);
            this.notificationManager.sendResetPasswordMessage(user,
                    (locale instanceof Locale ? (Locale) locale : null),
                    (uriParams instanceof String ? (String) uriParams : null));
        }

	return success();
    }

    public Event linkFacebook(final RequestContext context) throws Exception {
        final SelfServiceModel model = this.getSelfServiceModel(context);
        final GcxUser user = model.getUser();

	// generate a FacebookCredentials object
        final FacebookCredentials credentials = new FacebookCredentials(false);
        credentials.setSignedRequest(context.getRequestParameters().get(PARAMETER_SIGNED_REQUEST));

	// attempt to authenticate the facebook credentials
	try {
            this.authenticationManager.authenticate(credentials);
	} catch (final AuthenticationException e) {
	    return error();
	}

        // run the appropriate federatedProcessor
        for (final FederationProcessor processor : federatedProcessors) {
            if (processor.supports(credentials)) {
                try {
                    if (processor.linkIdentity(user, credentials, STRENGTH_FULL)) {
                        return success();
                    }
                } catch (final Exception e) {
                }
            }
        }

        return error();
    }

    public Event unlinkFacebook(final RequestContext context) throws Exception {
        final SelfServiceModel model = this.getSelfServiceModel(context);
	final GcxUser user = model.getUser();
        final String facebookId = user.getFacebookId();

	// clear the facebook id for this account
	final GcxUser freshUser = this.userManager.getFreshUser(user);
        freshUser.removeFacebookId(facebookId);
        this.userManager.updateUser(freshUser);
        user.removeFacebookId(facebookId);

	// return success
	return success();
    }

    public Event linkRelay(final RequestContext context) {
        final SelfServiceModel model = this.getSelfServiceModel(context);
        final GcxUser user = model.getUser();

        // generate a CasCredentials object, we don't observe locks because they
        // aren't necessary for linking an external identity
        final CasCredentials credentials = new CasCredentials(false);
        credentials.setService(RelayUtil.extractService(context));
        credentials.setTicket(context.getRequestParameters().get("ticket"));

        // attempt to authenticate the credentials
        try {
            this.authenticationManager.authenticate(credentials);
        } catch (final AuthenticationException e) {
            // TODO: set an error message
            return error();
        }

        // run the appropriate federatedProcessor
        for (final FederationProcessor processor : federatedProcessors) {
            if (processor.supports(credentials)) {
                try {
                    if (processor.linkIdentity(user, credentials, STRENGTH_FULL)) {
                        return success();
                    }
                } catch (final Exception e) {
                }
            }
        }

        // TODO: set an error message?
        return error();
    }

    public Event unlinkRelay(final RequestContext context) throws Exception {
        final SelfServiceModel model = this.getSelfServiceModel(context);
        final GcxUser user = model.getUser();
        final String relayGuid = user.getRelayGuid();

        // clear the relay guid for this account
        final GcxUser freshUser = this.userManager.getFreshUser(user);
        freshUser.removeRelayGuid(relayGuid);
        this.userManager.updateUser(freshUser);
        user.removeRelayGuid(relayGuid);

        // return success
        return success();
    }

    /**
     * Update the current user's details
     * 
     * @param context
     * @return
     */
    public Event updateAccountDetails(final RequestContext context) {
        final SelfServiceModel model = this.getSelfServiceModel(context);

	// get a fresh user object before performing updates
	final GcxUser user;
	try {
	    user = this.userManager.getFreshUser(model.getUser());
	} catch (final GcxUserNotFoundException e) {
	    context.getMessageContext().addMessage(
		    new MessageBuilder().error().source(null)
			    .code(ERROR_UPDATEFAILED_NOUSER).build());
	    return error();
	}

        LOG.debug("updating account details for: {}", user.getGUID());

	// update the user object based on the form values
	user.setFirstName(model.getFirstName());
	user.setLastName(model.getLastName());

        // change the password if allowed and requested
        if (StringUtils.isNotBlank(model.getPassword()) && user.isPasswordAllowChange()) {
	    user.setPassword(model.getPassword());
	    user.setForcePasswordChange(false);
	}

	// change the email if required
        final String email = model.getEmail();
        boolean sendChangeEmailMessage = false;
        if (StringUtils.isNotBlank(email) && !email.equals(user.getEmail())) {
            // is this email available
            if (!this.userManager.doesEmailExist(email)) {
                // do we need a new change email key?
                if (StringUtils.isBlank(user.getChangeEmailKey()) || !email.equals(user.getProposedEmail())) {
                    user.setChangeEmailKey(this.keyGenerator.getNewString());
                }
                user.setProposedEmail(model.getEmail());

                // we need to send an email after saving the user
                sendChangeEmailMessage = true;
            }
	}

	// save the updated user
	try {
            userManager.updateUser(user);
	} catch (final GcxUserNotFoundException e) {
            // This is extremely unlikely, so don't bother with a message
	    return error();
	}

        // send the change email notification message if needed
        if (sendChangeEmailMessage) {
            final Object locale = context.getRequestScope().get(VIEW_ATTR_LOCALE);
            final Object uriParams = context.getRequestScope().get(VIEW_ATTR_COMMONURIPARAMS);
            this.notificationManager.sendChangeEmailMessage(user, (locale instanceof Locale ? (Locale) locale : null),
                    (uriParams instanceof String ? (String) uriParams : null));
        }

	// return an appropriate success message
        context.getMessageContext().addMessage(
                new MessageBuilder()
                        .code(sendChangeEmailMessage ? MESSAGE_UPDATE_SUCCESS_CHANGEEMAIL : MESSAGE_UPDATE_SUCCESS)
                        .source(null).error().build());
	return success();
    }

    public Event processSignup(final RequestContext context) {
        final SelfServiceModel model = this.getSelfServiceModel(context);

	// generate a new GcxUser object
	final GcxUser user = new GcxUser();
	user.setEmail(model.getEmail());
        user.setPassword(model.getPassword());
	user.setFirstName(model.getFirstName());
	user.setLastName(model.getLastName());
        user.setSignupKey(this.keyGenerator.getNewString());

	user.setPasswordAllowChange(true);
        user.setForcePasswordChange(false);
	user.setLoginDisabled(false);
	user.setVerified(false);

	// create the new user in the GcxUserService
        LOG.info("***** User: {}", user);
        LOG.info("***** Preparing to create through service");

        try {
            this.userManager.createUser(user);

            final Object locale = context.getRequestScope().get(VIEW_ATTR_LOCALE);
            final Object uriParams = context.getRequestScope().get(VIEW_ATTR_COMMONURIPARAMS);
            this.notificationManager.sendEmailVerificationMessage(user, (locale instanceof Locale ? (Locale) locale
                    : null), (uriParams instanceof String ? (String) uriParams : null));
	} catch (final GcxUserAlreadyExistsException e) {
	    return error();
	}

	// return success
	return success();
    }

    public Event updatePassword(final RequestContext context) {
        final SelfServiceModel model = this.getSelfServiceModel(context);

	// throw an error if the user can't be found???
	final GcxUser user = this.userManager.findUserByEmail(model.getEmail());
	if (user == null) {
	    return error();
	}

	// set the password and disable the forcePasswordChange flag
        LOG.debug("Changing password for: {}", model.getEmail());
	user.setPassword(model.getPassword());
	user.setForcePasswordChange(false);
	try {
            this.userManager.updateUser(user);
	} catch (final GcxUserNotFoundException e) {
	    return error();
	}

	// return success
        LOG.debug("Looks like it was a success... now force a relogin (with the new password)");
	return success();
    }

    public Event propagateLoginTicketAction(final RequestContext context) {
        final String loginTicket = WebUtils.getLoginTicketFromRequest(context);
        WebUtils.putLoginTicket(context, loginTicket);
        return result("propagated");
    }

    /**
     * This webflow action can be used to verify a user account
     * 
     * @param context
     * @return "yes" if the account was verified, "no" if the account wasn't
     *         verified, or "error" if the state is inconsistent in some way
     */
    public Event verifyAccount(final RequestContext context) {
        // Validate login ticket
        final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context);
        final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context);
        if (!authoritativeLoginTicket.equals(providedLoginTicket)) {
            // we don't have a valid login ticket, so don't verify anything
            return no();
        }

        // perform the account verification
        return this.verifyAccount(this.getSelfServiceModel(context));
    }

    public Event verifyAccountNoLt(final RequestContext context) {
        // perform the account verification
        return this.verifyAccount(this.getSelfServiceModel(context));
    }

    private Event verifyAccount(final SelfServiceModel model) {
        // throw an error if we don't have a valid model
        if (model == null) {
            return error();
        }

        // short-circuit if we don't have a key to verify
        final String key = model.getKey();
        if (StringUtils.isBlank(key)) {
            return no();
        }

        // short-circuit if we don't have a valid user
        final GcxUser user;
        try {
            if (model.getUser() == null) {
                return error();
            }
            user = this.userManager.getFreshUser(model.getUser());
        } catch (final GcxUserNotFoundException e1) {
            // the specified user no longer exists, that's odd so return an
            // error
            return error();
        }

        // SIGNUP KEY
        if (key.equals(user.getSignupKey())) {
            // update the user
            user.setSignupKey(null);
            user.setVerified(true);
            try {
                this.userManager.updateUser(user);
            } catch (final GcxUserNotFoundException e) {
                return error();
            }

            // clear the key from the model
            model.setKey(null);

            // return that we processed a signup key
            return yes();
        }
        // CHANGE EMAIL KEY
        else if (key.equals(user.getChangeEmailKey())) {
            final Event response;

            // check to see if the proposed email is still available
            final String newEmail = user.getProposedEmail();
            if (this.userManager.doesEmailExist(newEmail)) {
                // there is an account using the proposed email address
                response = error();
            } else {
                // update the user's email address
                user.setEmail(newEmail);
                user.setUserid(newEmail);

                // the user now has a verified email address, so wipe any other
                // pending verification keys
                user.setSignupKey(null);
                user.setVerified(true);

                // update the username in the credentials (if present)
                final TheKeyCredentials credentials = model.getCredentials();
                if (credentials instanceof UsernamePasswordCredentials) {
                    ((UsernamePasswordCredentials) credentials).setUsername(newEmail);
                }

                // return that we accepted a changeEmailKey
                response = yes();
            }

            // always wipe the proposed email and the changeEmailKey
            user.setProposedEmail(null);
            user.setChangeEmailKey(null);

            // clear the key from the model
            model.setKey(null);

            // save the user
            try {
                this.userManager.updateUser(user);
            } catch (final GcxUserNotFoundException e) {
                return error();
            }

            return response;
        }
        // INVALID KEY
        else {
            // we were unable to verify the account
            return no();
        }
    }

    public Event sendAccountVerification(final RequestContext context, final TheKeyCredentials credentials) {
        // get a fresh user object
        final GcxUser user = credentials.getUser();
        if (user != null) {
            // get a fresh copy of the user object
            final GcxUser fresh;
            try {
                fresh = this.userManager.getFreshUser(user);
            } catch (final GcxUserNotFoundException e) {
                return error();
            }

            // send notification
            return this.sendAccountVerification(context, fresh);
        }

        return success();
    }

    public Event sendAccountVerification(final RequestContext context) {
        final SelfServiceModel model = this.getSelfServiceModel(context);
        final String email = model != null ? model.getEmail() : null;
        if (email != null) {
            final GcxUser user = this.userManager.findUserByEmail(email);
            return this.sendAccountVerification(context, user);
        }

        return success();
    }

    private Event sendAccountVerification(final RequestContext context, final GcxUser user) {
        if (user != null) {
            // short-circuit if the account is already verified
            if (user.isVerified()) {
                return error();
            }

            // generate a new signup key if there isn't one currently set for
            // the user
            if (StringUtils.isBlank(user.getSignupKey())) {
                user.setSignupKey(this.keyGenerator.getNewString());
                try {
                    this.userManager.updateUser(user);
                } catch (GcxUserNotFoundException e) {
                    return error();
                }
            }

            // send the account verification email
            final Object locale = context.getRequestScope().get(VIEW_ATTR_LOCALE);
            final Object uriParams = context.getRequestScope().get(VIEW_ATTR_COMMONURIPARAMS);
            this.notificationManager.sendEmailVerificationMessage(user, (locale instanceof Locale ? (Locale) locale
                    : null), (uriParams instanceof String ? (String) uriParams : null));
        }

        return success();
    }

    public Event resetPassword(final RequestContext context) {
        final SelfServiceModel model = this.getSelfServiceModel(context);

        // short-circuit if we don't have a valid user object
        final GcxUser user = this.userManager.findUserByEmail(model.getEmail());
        if (user == null) {
            return error();
        }

        // short-circuit if we don't have a valid reset password key
        final String key = model.getKey();
        if (StringUtils.isBlank(key) || !key.equals(user.getResetPasswordKey())) {
            return error();
        }

        // update the user
        user.setPassword(model.getPassword());
        user.setResetPasswordKey(null);
        user.setForcePasswordChange(false);
        user.setVerified(true); // we can verify the account because the
                                // resetPasswordKey was received via email
        user.setSignupKey(null);

        try {
            this.userManager.updateUser(user);
        } catch (final GcxUserNotFoundException e) {
            LOG.debug("Unexpected error", e);
            return error();
        }

        return success();
    }
}
