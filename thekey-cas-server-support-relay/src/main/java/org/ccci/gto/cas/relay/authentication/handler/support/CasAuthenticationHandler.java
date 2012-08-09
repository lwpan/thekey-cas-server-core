package org.ccci.gto.cas.relay.authentication.handler.support;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.service.GcxUserService;
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
    private GcxUserService userService;

    @NotNull
    private TicketValidator validator;

    public void setUserService(final GcxUserService userService) {
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
            casCredentials.setAssertion(null);

            // validate the ticket and store the assertion
            final Assertion assertion;
            try {
                assertion = this.validator.validate(casCredentials.getTicket(), casCredentials.getService());
                casCredentials.setAssertion(assertion);
            } catch (final TicketValidationException e) {
                casCredentials.setAssertion(null);
                // TODO: throw an AuthenticationException
                return false;
            }

            // look up the user from the Relay GUID provided in the response
            try {
                casCredentials.setGcxUser(this.userService.findUserByRelayGuid((String) assertion.getPrincipal()
                        .getAttributes().get("guid")));
            } catch (final Exception e) {
                casCredentials.setAssertion(null);
                casCredentials.setGcxUser(null);
                // TODO: throw an AuthenticationException
                return false;
            }

            // check all authentication locks
            AuthenticationUtil.checkLocks(casCredentials);

            // return whether this was a successful authentication or not
            return assertion != null;
        }

        // default to unauthorized
        return false;
    }
}
