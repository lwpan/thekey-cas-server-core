package org.ccci.gto.cas.authentication;

import static org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock.STALEPASSWORD;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.handler.StalePasswordAuthenticationException;
import org.ccci.gto.cas.util.UserUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class UsernamePasswordUserAuthenticationMetaDataPopulator extends
	AbstractUserAuthenticationMetaDataPopulator {
    public UsernamePasswordUserAuthenticationMetaDataPopulator() {
	super(UsernamePasswordCredentials.class, true);
    }

    public UsernamePasswordUserAuthenticationMetaDataPopulator(
	    final Class<? extends UsernamePasswordCredentials> classToSupport,
	    final boolean supportSubClasses) {
	super(classToSupport, supportSubClasses);
    }

    @Override
    protected GcxUser findUser(final Authentication authentication,
	    final Credentials credentials) {
	return this.getUserService().findUserByEmail(
		authentication.getPrincipal().getId());
    }

    @Override
    protected boolean validateUser(Authentication authentication,
	    Credentials credentials) throws AuthenticationException {
	final boolean response = super
		.validateUser(authentication, credentials);

	// Does the user need to change their password?
	final GcxUser user = UserUtil.getUser(authentication);
	if (user != null && user.isForcePasswordChange()
		&& this.observeLock(credentials, STALEPASSWORD)) {
	    log.info("Account has a stale password: " + user.getGUID());
	    throw StalePasswordAuthenticationException.ERROR;
	}

	return response;
    }
}
