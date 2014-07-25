package me.thekey.cas.relay.authentication.handler;

import static me.thekey.cas.authentication.principal.TheKeyCredentials.Lock.NULLUSER;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.client.RestClient;
import me.thekey.cas.relay.authentication.util.RelayAuthenticationUtil;
import me.thekey.cas.relay.service.RelayUserManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class RelayAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RelayAuthenticationHandler.class);

    @NotNull
    private RestClient restClient;

    @Inject
    @NotNull
    private UserManager userManager;

    @Inject
    @NotNull
    private RelayUserManager relayUserManager;

    @NotNull
    private TicketValidator validator;

    @NotNull
    private String service;

    public void setRelayUserManager(final RelayUserManager manager) {
        this.relayUserManager = manager;
    }

    public void setRestClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public void setUserManager(final UserManager manager) {
        this.userManager = manager;
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
            Assertion assertion = RelayAuthenticationUtil.getAssertion(credentials);
            if (assertion == null) {
                // get a ticket for the provided credentials
                final String tgt = this.restClient.getTicketGrantingTicket(credentials.getUsername(),
                        credentials.getPassword());
                final String ticket = this.restClient.getTicket(tgt, this.service);

                // validate the ticket and store the assertion
                if (ticket != null) {
                    try {
                        assertion = this.validator.validate(ticket, this.service);
                        assert assertion != null;
                        RelayAuthenticationUtil.setAssertion(credentials, assertion);
                    } catch (final TicketValidationException e) {
                        RelayAuthenticationUtil.setAssertion(credentials, null);
                        // TODO: maybe throw an AuthenticationException
                        return false;
                    } catch (final Exception e) {
                        RelayAuthenticationUtil.setAssertion(credentials, null);
                        LOG.error("Unexpected exception when validating ticket", e);
                        return false;
                    }
                } else {
                    return false;
                }
            }

            // look up the user from the Relay GUID in the assertion
            final GcxUser user = this.relayUserManager.findUserByRelayGuid(RelayAuthenticationUtil.getRelayGuid
                    (assertion));

            // throw an unknown identity exception if the user wasn't found
            if (credentials.observeLock(NULLUSER) && user == null) {
                throw UnknownCasIdentityAuthenticationException.ERROR;
            }

            // update last login time
            if (user != null) {
                final Date now = new Date(System.currentTimeMillis());
                try {
                    final GcxUser freshUser = this.userManager.getFreshUser(user);
                    final Date lastLogin = freshUser.getLoginTime();
                    if (lastLogin == null || lastLogin.compareTo(now) < 0) {
                        freshUser.setLoginTime(now);
                        this.userManager.updateUser(freshUser);
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
