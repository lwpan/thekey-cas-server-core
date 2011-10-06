package org.ccci.gto.cas.authentication;

import static org.ccci.gto.cas.facebook.Constants.PRINCIPAL_ATTR_ACCESSTOKEN;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.authentication.handler.FacebookIdAlreadyExistsAuthenticationException;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.ccci.gto.cas.util.RandomGUID;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Principal;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.User;

public class FacebookUserAuthenticationMetaDataPopulator extends
	AbstractUserAuthenticationMetaDataPopulator {
    public FacebookUserAuthenticationMetaDataPopulator() {
	super(FacebookCredentials.class, true);
    }

    public FacebookUserAuthenticationMetaDataPopulator(
	    Class<? extends FacebookCredentials> classToSupport,
	    boolean supportSubClasses) {
	super(classToSupport, supportSubClasses);
    }

    @Override
    protected GcxUser findUser(final Authentication authentication,
	    final Credentials credentials) {
	return this.getUserService().findUserByFacebookId(
		authentication.getPrincipal().getId());
    }

    @Override
    public Authentication postLookup(final Authentication authentication,
	    final Credentials credentials) throws AuthenticationException {
	// vivify the user if they don't exist and the credentials indicate they
	// should be vivified
	if (AuthenticationUtil.getUser(authentication) == null
		&& ((FacebookCredentials) credentials).isVivify()) {
	    // get a few objects necessary for processing
	    final GcxUserService userService = this.getUserService();
	    final Principal p = authentication.getPrincipal();

	    // get the user id, first name, last name, and email address from
	    // facebook
	    final String accessToken = (String) p.getAttributes().get(
		    PRINCIPAL_ATTR_ACCESSTOKEN);
	    final FacebookClient fbClient = new DefaultFacebookClient(
		    accessToken);
	    final User fbUser = fbClient.fetchObject("me", User.class,
		    Parameter.with("fields", "id,first_name,last_name,email"));
	    final String facebookId = fbUser.getId();
	    final String email = fbUser.getEmail();
	    final String firstName = fbUser.getFirstName();
	    final String lastName = fbUser.getLastName();

	    // throw an error if the facebook id isn't the same as the principal
	    // id
	    if (!facebookId.equals(p.getId())) {
		throw new BadCredentialsAuthenticationException();
	    }

	    // see if a Key account already exists for this email
	    final GcxUser current = userService.findUserByEmail(email);
	    if (current != null) {
		if (StringUtils.isNotBlank(current.getFacebookId())) {
		    log.error(current.getEmail()
			    + " already has another facebook account linked to it");
		    throw FacebookIdAlreadyExistsAuthenticationException.ERROR;
		}

		// set the facebook id for the found user
		current.setFacebookId(facebookId);
		userService.updateUser(current, false, "FacebookLogin",
			current.getEmail());
	    }
	    // account doesn't exist, create a new one in LDAP
	    else {
		// create a new user
		final GcxUser user = new GcxUser();
		user.setGUID(RandomGUID.generateGuid(true));
		user.setEmail(email);
		user.setFacebookId(facebookId);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPasswordAllowChange(true);
		user.setForcePasswordChange(true);
		user.setLoginDisabled(false);
		user.setVerified(false);
		userService.createUser(user, null, false);
	    }

	    // store the newly created user in the Authentication object
	    AuthenticationUtil.setUser(authentication,
		    this.findUser(authentication, credentials));
	}

	return authentication;
    }
}
