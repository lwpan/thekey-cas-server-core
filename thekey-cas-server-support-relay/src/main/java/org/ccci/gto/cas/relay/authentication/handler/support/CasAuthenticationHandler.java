package org.ccci.gto.cas.relay.authentication.handler.support;

import static me.thekey.cas.authentication.principal.TheKeyCredentials.Lock.NULLUSER;

import me.thekey.cas.relay.authentication.util.RelayAuthenticationUtil;
import me.thekey.cas.relay.service.RelayUserManager;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
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

public class CasAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CasAuthenticationHandler.class);

    @Inject
    @NotNull
    private RelayUserManager relayUserManager;

    @NotNull
    private TicketValidator validator;

    /**
     * @param validator
     *            the validator to set
     */
    public void setValidator(final TicketValidator validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof CasCredentials;
    }

    @Override
    protected boolean doAuthentication(final Credentials credentials) throws AuthenticationException {
        if (credentials instanceof CasCredentials) {
            final CasCredentials casCredentials = (CasCredentials) credentials;

            /**
             * Validate the service and ticket if we don't already have an
             * assertion. Checking for an existing assertion is necessary for
             * login after identity linking.
             */
            Assertion assertion = RelayAuthenticationUtil.getAssertion(casCredentials);
            if(assertion == null) {
                // validate the ticket and store the assertion
                try {
                    assertion = this.validator.validate(casCredentials.getTicket(), casCredentials.getService());
                    assert assertion != null;
                    RelayAuthenticationUtil.setAssertion(casCredentials, assertion);
                } catch (final TicketValidationException e) {
                    RelayAuthenticationUtil.setAssertion(casCredentials, null);
                    // TODO: throw an AuthenticationException
                    return false;
                } catch (final Exception e) {
                    RelayAuthenticationUtil.setAssertion(casCredentials, null);
                    LOG.error("Unexpected exception when validating ticket", e);
                    // TODO: throw an AuthenticationException
                    return false;
                }
            }

            // look up the user from the Relay GUID in the assertion
            final GcxUser user = this.relayUserManager.findUserByRelayGuid(RelayAuthenticationUtil.getRelayGuid
                    (assertion));

            // throw an unknown identity exception if the user wasn't found
            if (casCredentials.observeLock(NULLUSER) && user == null) {
                throw UnknownCasIdentityAuthenticationException.ERROR;
            }

            // store the user and check all authentication locks
            casCredentials.setUser(user);
            AuthenticationUtil.checkLocks(casCredentials);

            // return success
            return true;
        }

        // default to failed authentication
        return false;
    }
}
