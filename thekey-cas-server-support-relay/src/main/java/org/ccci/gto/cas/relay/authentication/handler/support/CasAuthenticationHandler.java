package org.ccci.gto.cas.relay.authentication.handler.support;

import static org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock.NULLUSER;
import static org.ccci.gto.cas.relay.Constants.ATTR_GUID;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

public class CasAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    @NotNull
    private UserManager userService;

    @NotNull
    private TicketValidator validator;

    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }

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
            Assertion assertion = casCredentials.getAssertion();
            if(assertion == null) {
                // validate the ticket and store the assertion
                try {
                    assertion = this.validator.validate(casCredentials.getTicket(), casCredentials.getService());
                    casCredentials.setAssertion(assertion);
                } catch (final TicketValidationException e) {
                    casCredentials.setAssertion(null);
                    // TODO: throw an AuthenticationException
                    return false;
                }
            }

            // look up the user from the Relay GUID in the assertion
            final GcxUser user = this.userService.findUserByRelayGuid((String) assertion.getPrincipal().getAttributes()
                    .get(ATTR_GUID));

            // throw an unknown identity exception if the user wasn't found
            if (casCredentials.observeLock(NULLUSER) && user == null) {
                throw UnknownCasIdentityAuthenticationException.ERROR;
            }

            // store the user and check all authentication locks
            casCredentials.setGcxUser(user);
            AuthenticationUtil.checkLocks(casCredentials);

            // return whether this was a successful authentication or not
            return assertion != null;
        }

        // default to failed authentication
        return false;
    }
}
