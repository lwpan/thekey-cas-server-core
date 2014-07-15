package me.thekey.cas.relay.authentication.handler;

import static me.thekey.cas.authentication.principal.TheKeyCredentials.Lock.NULLUSER;
import static me.thekey.cas.relay.Constants.CREDS_ATTR_CAS_ASSERTION;
import static org.ccci.gto.cas.relay.Constants.ATTR_GUID;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.client.RestClient;
import me.thekey.cas.service.UserManager;
import me.thekey.cas.service.UserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.ccci.gto.cas.relay.authentication.handler.support.UnknownCasIdentityAuthenticationException;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class RelayAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    @NotNull
    private RestClient restClient;

    @NotNull
    private UserManager userService;

    @NotNull
    private TicketValidator validator;

    @NotNull
    private String service;

    public void setRestClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }

    public void setValidator(final TicketValidator validator) {
        this.validator = validator;
    }

    public void setService(final String service) {
        this.service = service;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof TheKeyUsernamePasswordCredentials && ((TheKeyCredentials) credentials)
                .observeLock(TheKeyCredentials.Lock.FEDERATIONALLOWED);
    }

    @Override
    protected boolean doAuthentication(final Credentials rawCredentials) throws AuthenticationException {
        if (rawCredentials instanceof TheKeyUsernamePasswordCredentials) {
            final TheKeyUsernamePasswordCredentials credentials = (TheKeyUsernamePasswordCredentials) rawCredentials;

            /**
             * Validate the service and ticket if we don't already have an
             * assertion. Checking for an existing assertion is necessary for
             * login after identity linking.
             */
            Assertion assertion = credentials.getAttribute(CREDS_ATTR_CAS_ASSERTION, Assertion.class);
            if (assertion == null) {
                // get a ticket for the provided credentials
                final String tgt = this.restClient.getTicketGrantingTicket(credentials.getUsername(),
                        credentials.getPassword());
                final String ticket = this.restClient.getTicket(tgt, this.service);

                // validate the ticket and store the assertion
                if (ticket != null) {
                    try {
                        assertion = this.validator.validate(ticket, this.service);
                        credentials.setAttribute(CREDS_ATTR_CAS_ASSERTION, assertion);
                    } catch (final TicketValidationException e) {
                        credentials.setAttribute(CREDS_ATTR_CAS_ASSERTION, null);
                        // TODO: maybe throw an AuthenticationException
                        return false;
                    }
                } else {
                    return false;
                }
            }

            // look up the user from the Relay GUID in the assertion
            final GcxUser user = this.userService.findUserByRelayGuid((String) assertion.getPrincipal().getAttributes
                    ().get(ATTR_GUID));

            // throw an unknown identity exception if the user wasn't found
            if (credentials.observeLock(NULLUSER) && user == null) {
                throw UnknownCasIdentityAuthenticationException.ERROR;
            }

            // update last login time
            if (user != null) {
                final Date now = new Date(System.currentTimeMillis());
                try {
                    final GcxUser freshUser = this.userService.getFreshUser(user);
                    final Date lastLogin = freshUser.getLoginTime();
                    if (lastLogin == null || lastLogin.compareTo(now) < 0) {
                        freshUser.setLoginTime(now);
                        this.userService.updateUser(freshUser);
                        user.setLoginTime(now);
                    }
                } catch (final UserNotFoundException ignored) {
                }
            }

            // store the user and check all authentication locks
            credentials.setUser(user);
            AuthenticationUtil.checkLocks(credentials);

            // return whether this was a successful authentication or not
            return true;
        }

        return false;
    }
}
