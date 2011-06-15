package org.ccci.gto.cas.authentication;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAILADDRESS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.authentication.handler.FacebookIdAlreadyExistsAuthenticationException;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.util.RandomGUID;
import org.ccci.gto.cas.util.UserUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Principal;

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
	if (UserUtil.getUser(authentication) == null
		&& ((FacebookCredentials) credentials).isVivify()) {
	    // get a few objects necessary for processing
	    final GcxUserService userService = this.getUserService();
	    final Principal p = authentication.getPrincipal();

	    // get the attributes stored in the Principal
	    final String facebookId = p.getId();
	    final String email = (String) p.getAttributes().get(
		    PRINCIPAL_ATTR_EMAILADDRESS);
	    final String firstName = (String) p.getAttributes().get(
		    PRINCIPAL_ATTR_FIRSTNAME);
	    final String lastName = (String) p.getAttributes().get(
		    PRINCIPAL_ATTR_LASTNAME);

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
		userService
			.updateUser(current, false, null, current.getEmail());
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
		userService.createUser(user, null);
	    }

	    // store the newly created user in the Authentication object
	    UserUtil.setUser(authentication,
		    this.findUser(authentication, credentials));
	}

	return authentication;
    }
}
