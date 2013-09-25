package org.ccci.gto.cas.selfservice;

import static me.thekey.cas.selfservice.Constants.FLOW_MODEL_SELFSERVICE;
import static me.thekey.cas.selfservice.Constants.PARAMETER_VERIFICATION_KEY;
import static me.thekey.cas.selfservice.Constants.PARAMETER_VERIFICATION_USERNAME;
import static org.ccci.gto.cas.Constants.ERROR_UPDATEFAILED_NOUSER;
import static org.ccci.gto.cas.Constants.STRENGTH_FULL;
import static org.ccci.gto.cas.Constants.VIEW_ATTR_COMMONURIPARAMS;
import static org.ccci.gto.cas.Constants.VIEW_ATTR_LOCALE;
import static org.ccci.gto.cas.facebook.Constants.ERROR_ACCOUNTALREADYLINKED;
import static org.ccci.gto.cas.facebook.Constants.PARAMETER_SIGNED_REQUEST;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORCECHANGEPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORGOTPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_USERUPDATE;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_SENDFORGOTFAILED;
import static org.ccci.gto.cas.selfservice.Constants.FLOW_MODEL_SELFSERVICEUSER;
import static org.ccci.gto.cas.selfservice.Constants.MESSAGE_UPDATESUCCESS;
import static org.ccci.gto.cas.selfservice.Constants.MESSAGE_UPDATESUCCESS_RESETPASSWORD;

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
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.federation.FederationProcessor;
import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.ccci.gto.cas.relay.util.RelayUtil;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.util.RandomStringGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
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

    private SelfServiceUser getModel(final RequestContext context) {
	return (SelfServiceUser) context.getFlowScope().get(
		FLOW_MODEL_SELFSERVICEUSER);
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
     * Action that performs initial login webflow setup
     */
    public Event initialLoginFlowSetupAction(final RequestContext context) {
        final ParameterMap params = context.getRequestParameters();

        // populate selfservice object with required data
        final SelfServiceModel model = this.getSelfServiceModel(context);
        if (model != null) {
            // store any verification key in the request
            if (params.contains(PARAMETER_VERIFICATION_KEY)) {
                model.setKey(params.get(PARAMETER_VERIFICATION_KEY));
            }
        }

        // pre-populate username
        final MutableAttributeMap flowScope = context.getFlowScope();
        if (flowScope.contains("credentials")) {
            final Object rawCredentials = flowScope.get("credentials");
            if (rawCredentials instanceof UsernamePasswordCredentials
                    && params.contains(PARAMETER_VERIFICATION_USERNAME)) {
                ((UsernamePasswordCredentials) rawCredentials).setUsername(params.get(PARAMETER_VERIFICATION_USERNAME));
            }
        }

        return success();
    }

    /**
     * triggers a resetPassword action for this user since they forgot their
     * password.
     * 
     * @param data
     */
    public Event sendForgotPasswordEmail(final RequestContext context) {
	final SelfServiceUser model = getModel(context);

	final String email = model.getEmail();
	try {
            final GcxUser user = this.userManager.findUserByEmail(email);
            final String uriParams = context.getRequestScope().getRequiredString(VIEW_ATTR_COMMONURIPARAMS);
            this.userManager.resetPassword(user, AUDIT_SOURCE_FORGOTPASSWORD, email, uriParams);
        } catch (final Exception e) {
            LOG.error("An exception occured trying to find user: {}", email, e);
	    context.getMessageContext().addMessage(
		    new MessageBuilder().error().source(null)
			    .code(ERROR_SENDFORGOTFAILED).build());

	    return error();
	}

	return success();
    }

    public Event linkFacebook(final RequestContext context) throws Exception {
	final SelfServiceUser model = getModel(context);
        final GcxUser user = model.getUser();

	// generate a FacebookCredentials object
        final FacebookCredentials credentials = new FacebookCredentials(false);
        credentials.setSignedRequest(context.getRequestParameters().get(PARAMETER_SIGNED_REQUEST));
        credentials.setVivify(false);

	// attempt to authenticate the facebook credentials
	Authentication auth;
	try {
	    // attempt to authenticate the credentials
	    auth = this.authenticationManager.authenticate(credentials);
	} catch (final AuthenticationException e) {
	    return error();
	}

        // throw an error if the account is already linked to another account in
        // The Key
        // TODO: remove this check once the facebook js performs a reauth check
        if (AuthenticationUtil.getUser(auth) != null) {
            context.getMessageContext().addMessage(
                    new MessageBuilder().error().source(null).code(ERROR_ACCOUNTALREADYLINKED).build());
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
	final SelfServiceUser model = getModel(context);
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
        final SelfServiceUser model = getModel(context);
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
        final SelfServiceUser model = getModel(context);
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
	final SelfServiceUser model = this.getModel(context);

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

	// a few processing flags
	final boolean changeEmail = !user.getEmail().equals(model.getEmail());
	final boolean changePassword = !changeEmail
		&& StringUtils.isNotBlank(model.getPassword())
		&& user.isPasswordAllowChange();

	// update the user object based on the form values
	user.setFirstName(model.getFirstName());
	user.setLastName(model.getLastName());

	// change the password if required
	if (changePassword) {
	    user.setPassword(model.getPassword());
	    user.setForcePasswordChange(false);
	    user.setVerified(true);
	}

	// change the email if required
	if (changeEmail) {
	    // update the user object with the new email
	    user.setEmail(model.getEmail());
	    user.setUserid(user.getEmail());
	    user.setVerified(false);
	}

	// save the updated user
	try {
            userManager.updateUser(user);
	} catch (final GcxUserNotFoundException e) {
	    return error();
	}

	// email changed, so trigger a password reset
	if (changeEmail) {
	    // send a new password.
            LOG.debug("changed username so reset password.");
            userManager.resetPassword(user, AUDIT_SOURCE_USERUPDATE, user.getGUID(), context.getRequestScope()
                    .getRequiredString(VIEW_ATTR_COMMONURIPARAMS));
	}

	// return an appropriate success message
	context.getMessageContext().addMessage(
		new MessageBuilder()
			.code(changeEmail ? MESSAGE_UPDATESUCCESS_RESETPASSWORD
				: MESSAGE_UPDATESUCCESS).source(null).error()
			.build());
	return success();
    }

    public Event processSignup(final RequestContext context) {
	final SelfServiceUser model = this.getModel(context);

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

    public Event populateFromCredentials(final SelfServiceUser model,
	    final UsernamePasswordCredentials credentials) {
	model.setEmail(credentials.getUsername());
	return success();
    }

    public Event updatePassword(final SelfServiceUser model) {
	// throw an error if the user can't be found???
	final GcxUser user = this.userManager.findUserByEmail(model.getEmail());
	if (user == null) {
	    return error();
	}

	// set the password and disable the forcePasswordChange flag
        LOG.debug("Changing password for: {}", model.getEmail());
	user.setPassword(model.getPassword());
	user.setForcePasswordChange(false);
	user.setVerified(true);
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
     * @param credentials
     * @param model
     * @return "yes" if the account was verified, "no" if the account wasn't
     *         verified or "error" if the state is inconsistent in some way
     */
    public Event verifyAccount(final RequestContext context, final TheKeyCredentials credentials,
            final SelfServiceModel model) {
        // Validate login ticket
        final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context);
        final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context);
        if (!authoritativeLoginTicket.equals(providedLoginTicket)) {
            // we don't have a valid login ticket, so don't verify anything
            return no();
        }

        // make sure there is an actual user to verify
        final GcxUser user = ((TheKeyCredentials) credentials).getUser();
        if (user != null) {
            final String key = model.getKey();
            if (StringUtils.isNotBlank(key)) {
                try {
                    final GcxUser fresh = this.userManager.getFreshUser(user);

                    // do we have a signup key?
                    if (key.equals(fresh.getSignupKey())) {
                        // update the user
                        fresh.setSignupKey(null);
                        fresh.setVerified(true);
                        this.userManager.updateUser(fresh);

                        // clear the key from the model
                        model.setKey(null);

                        // return that we updated the user
                        return yes();
                    } else if (fresh.isVerified()) {
                        // the account is now verified, we shouldn't be in
                        // account verification processing
                        return error();
                    } else {
                        // we were unable to verify the account
                        return no();
                    }
                } catch (final GcxUserNotFoundException e) {
                    // the specified user no longer exists, return an error
                    return error();
                }
            } else {
                // we didn't have a valid key, so we didn't verify the account
                return no();
            }
        } else {
            // we don't have a user object, return an error
            return error();
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

            // short-circuit if the account is already verified
            if (fresh.isVerified()) {
                return error();
            }

            // generate a new signup key if there isn't one currently set for
            // the user
            if (StringUtils.isBlank(fresh.getSignupKey())) {
                fresh.setSignupKey(this.keyGenerator.getNewString());
                try {
                    this.userManager.updateUser(fresh);
                } catch (GcxUserNotFoundException e) {
                    return error();
                }
            }

            // send the account verification email
            final Object locale = context.getRequestScope().get(VIEW_ATTR_LOCALE);
            final Object uriParams = context.getRequestScope().get(VIEW_ATTR_COMMONURIPARAMS);
            this.notificationManager.sendEmailVerificationMessage(fresh, (locale instanceof Locale ? (Locale) locale
                    : null), (uriParams instanceof String ? (String) uriParams : null));
        }

        return success();
    }
}
