package org.ccci.gto.cas.relay.authentication.handler.support;

import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

public class CasAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private TicketValidator validator;

    /**
     * @return the validator
     */
    public TicketValidator getValidator() {
        return validator;
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
            try {
                casCredentials
                        .setAssertion(validator.validate(casCredentials.getTicket(), casCredentials.getService()));
                return true;
            } catch (final TicketValidationException e) {
                casCredentials.setAssertion(null);
                return false;
            }
        }

        // default to unauthorized
        return false;
    }
}
