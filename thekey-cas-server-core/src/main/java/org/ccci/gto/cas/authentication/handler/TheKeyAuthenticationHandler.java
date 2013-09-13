package org.ccci.gto.cas.authentication.handler;

import static org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock.STALEPASSWORD;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheKeyAuthenticationHandler implements AuthenticationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TheKeyAuthenticationHandler.class);

    @NotNull
    private AuthenticationHandler handler;

    @NotNull
    private UserManager userService;

    public void setHandler(final AuthenticationHandler handler) {
        this.handler = handler;
    }

    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }

    @Override
    public boolean authenticate(final Credentials credentials) throws AuthenticationException {
        final boolean response = this.handler.authenticate(credentials);

        // lookup the GcxUser object for the user that just authenticated
        if (credentials instanceof TheKeyUsernamePasswordCredentials) {
            final TheKeyUsernamePasswordCredentials creds = (TheKeyUsernamePasswordCredentials) credentials;
            creds.setGcxUser(this.userService.findUserByEmail(creds.getUsername()));
        }

        // check all authentication locks
        AuthenticationUtil.checkLocks((TheKeyCredentials) credentials);

        // Does the user need to change their password?
        if (credentials instanceof TheKeyUsernamePasswordCredentials) {
            final TheKeyUsernamePasswordCredentials creds = (TheKeyUsernamePasswordCredentials) credentials;
            final GcxUser user = creds.getGcxUser();
            if (user != null && user.isForcePasswordChange() && creds.observeLock(STALEPASSWORD)) {
                LOG.info("Account has a stale password: {}", user.getGUID());
                throw StalePasswordAuthenticationException.ERROR;
            }
        }

        return response;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof TheKeyCredentials && handler.supports(credentials);
    }
}
