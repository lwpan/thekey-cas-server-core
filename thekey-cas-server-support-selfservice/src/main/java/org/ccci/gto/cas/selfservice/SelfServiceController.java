package org.ccci.gto.cas.selfservice;

import static org.ccci.gto.cas.Constants.ERROR_UPDATEFAILED_NOUSER;
import static org.ccci.gto.cas.Constants.STRENGTH_FULL;
import static org.ccci.gto.cas.Constants.VIEW_ATTR_COMMONURIPARAMS;
import static org.ccci.gto.cas.facebook.Constants.ERROR_ACCOUNTALREADYLINKED;
import static org.ccci.gto.cas.facebook.Constants.PARAMETER_SIGNED_REQUEST;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORCECHANGEPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_FORGOTPASSWORD;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_SIGNUP;
import static org.ccci.gto.cas.selfservice.Constants.AUDIT_SOURCE_USERUPDATE;
import static org.ccci.gto.cas.selfservice.Constants.ERROR_SENDFORGOTFAILED;
import static org.ccci.gto.cas.selfservice.Constants.FLOW_MODEL_SELFSERVICEUSER;
import static org.ccci.gto.cas.selfservice.Constants.MESSAGE_UPDATESUCCESS;
import static org.ccci.gto.cas.selfservice.Constants.MESSAGE_UPDATESUCCESS_RESETPASSWORD;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.federation.FederationProcessor;
import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.ccci.gto.cas.relay.util.RelayUtil;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.ccci.gto.cas.util.RandomGUID;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
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
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NotNull
    private AuthenticationManager authenticationManager;

    private final ArrayList<FederationProcessor> federatedProcessors = new ArrayList<FederationProcessor>();

    @NotNull
    private GcxUserService userService;

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

    public void setUserService(final GcxUserService userService) {
	this.userService = userService;
    }

    private SelfServiceUser getModel(final RequestContext context) {
	return (SelfServiceUser) context.getFlowScope().get(
		FLOW_MODEL_SELFSERVICEUSER);
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
	    final GcxUser user = userService.findUserByEmail(email);
            final String uriParams = context.getRequestScope().getRequiredString(VIEW_ATTR_COMMONURIPARAMS);
            userService.resetPassword(user, AUDIT_SOURCE_FORGOTPASSWORD, email, uriParams);
	} catch (Exception e) {
	    logger.error("An exception (" + e.getMessage()
		    + ") occurred trying to find user (" + email
		    + ") and send a resetPassword message." + e.getMessage());
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
	final GcxUser freshUser = this.userService.getFreshUser(user);
        freshUser.removeFacebookId(facebookId);
	this.userService.updateUser(freshUser, false, AUDIT_SOURCE_USERUPDATE,
		freshUser.getEmail());
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
        final GcxUser freshUser = this.userService.getFreshUser(user);
        freshUser.removeRelayGuid(relayGuid);
        this.userService.updateUser(freshUser, false, AUDIT_SOURCE_USERUPDATE, freshUser.getEmail());
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
	    user = this.userService.getFreshUser(model.getUser());
	} catch (final GcxUserNotFoundException e) {
	    context.getMessageContext().addMessage(
		    new MessageBuilder().error().source(null)
			    .code(ERROR_UPDATEFAILED_NOUSER).build());
	    return error();
	}

	if (logger.isDebugEnabled()) {
	    logger.debug("updating account details for: " + user.getGUID());
	}

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
	    userService.updateUser(user, changePassword,
		    AUDIT_SOURCE_USERUPDATE, user.getGUID());
	} catch (final GcxUserNotFoundException e) {
	    return error();
	}

	// email changed, so trigger a password reset
	if (changeEmail) {
	    // send a new password.
	    logger.debug("changed username so reset password.");
            userService.resetPassword(user, AUDIT_SOURCE_USERUPDATE, user.getGUID(), context.getRequestScope()
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
	user.setGUID(RandomGUID.generateGuid(true));
	user.setEmail(model.getEmail());
	user.setFirstName(model.getFirstName());
	user.setLastName(model.getLastName());
	user.setPasswordAllowChange(true);
	user.setForcePasswordChange(true);
	user.setLoginDisabled(false);
	user.setVerified(false);

	// create the new user in the GcxUserService
	if (logger.isInfoEnabled()) {
	    logger.info("***** User: " + user);
	    logger.info("***** Preparing to create through service");
	}
	try {
            final Object uriParams = context.getRequestScope().get(VIEW_ATTR_COMMONURIPARAMS);
            this.userService.createUser(user, AUDIT_SOURCE_SIGNUP, true,
                    (uriParams instanceof String ? (String) uriParams : null));
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
	final GcxUser user = this.userService.findUserByEmail(model.getEmail());
	if (user == null) {
	    return error();
	}

	// set the password and disable the forcePasswordChange flag
	if (logger.isDebugEnabled()) {
	    logger.debug("Changing password for: " + model.getEmail());
	}
	user.setPassword(model.getPassword());
	user.setForcePasswordChange(false);
	user.setVerified(true);
	try {
	    this.userService.updateUser(user, true,
		    AUDIT_SOURCE_FORCECHANGEPASSWORD, model.getEmail());
	} catch (final GcxUserNotFoundException e) {
	    return error();
	}

	// return success
	logger.debug("Looks like it was a success... now force a relogin (with the new password)");
	return success();
    }
}
