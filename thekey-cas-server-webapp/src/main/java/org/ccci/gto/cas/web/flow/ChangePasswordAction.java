package org.ccci.gto.cas.web.flow;

import org.ccci.gcx.idm.web.SimpleLoginUser;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordAction {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void populateObject(final SimpleLoginUser user,
	    final UsernamePasswordCredentials credentials) {
	user.setUsername(credentials.getUsername());
    }
}
