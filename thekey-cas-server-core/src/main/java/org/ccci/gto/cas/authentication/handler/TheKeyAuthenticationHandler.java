package org.ccci.gto.cas.authentication.handler;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.service.UserManager;
import me.thekey.cas.util.AuthenticationUtil;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

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
            creds.setUser(this.userService.findUserByEmail(creds.getUsername()));
        }

        // check all authentication locks
        AuthenticationUtil.checkLocks((TheKeyCredentials) credentials);

        return response;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof TheKeyCredentials && handler.supports(credentials);
    }
}
