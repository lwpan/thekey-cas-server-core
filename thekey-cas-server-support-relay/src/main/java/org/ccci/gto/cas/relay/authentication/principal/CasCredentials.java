package org.ccci.gto.cas.relay.authentication.principal;

import static me.thekey.cas.relay.Constants.CREDS_ATTR_CAS_ASSERTION;

import me.thekey.cas.federation.authentication.principal.FederatedTheKeyCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.validation.Assertion;

public final class CasCredentials extends FederatedTheKeyCredentials implements Credentials {
    private static final long serialVersionUID = -756164046254632977L;

    private String service;

    private String ticket;

    public CasCredentials() {
        super();
    }

    public CasCredentials(final boolean observeLocks) {
        super(observeLocks);
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(final String service) {
        this.service = service;
    }

    /**
     * @return the ticket
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * @param ticket
     *            the ticket to set
     */
    public void setTicket(final String ticket) {
        this.ticket = ticket;
    }

    /**
     * @return the assertion
     */
    public Assertion getAssertion() {
        return this.getAttribute(CREDS_ATTR_CAS_ASSERTION, Assertion.class);
    }

    /**
     * @param assertion the assertion to set
     */
    public void setAssertion(final Assertion assertion) {
        this.setAttribute(CREDS_ATTR_CAS_ASSERTION, assertion);
    }
}
