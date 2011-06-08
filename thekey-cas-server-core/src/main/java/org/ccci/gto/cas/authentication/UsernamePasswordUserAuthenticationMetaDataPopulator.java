package org.ccci.gto.cas.authentication;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.Authentication;
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
}
