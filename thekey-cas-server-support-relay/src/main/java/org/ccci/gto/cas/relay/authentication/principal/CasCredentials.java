package org.ccci.gto.cas.relay.authentication.principal;

import me.thekey.cas.authentication.principal.AbstractTheKeyCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.validation.Assertion;

public final class CasCredentials extends AbstractTheKeyCredentials implements Credentials {
    private static final long serialVersionUID = 7917581886659605981L;

    private String service;

    private String ticket;

    private Assertion assertion;

    public CasCredentials() {
        this(true);
    }

    public CasCredentials(final boolean observeLocks) {
        super(observeLocks);
        this.setObserveLock(Lock.STALEPASSWORD, false);
        this.setObserveLock(Lock.VERIFIED, false);
    }

    /**
     * @return the assertion
     */
    public Assertion getAssertion() {
        return assertion;
    }

    /**
     * @param assertion
     *            the assertion to set
     */
    public void setAssertion(final Assertion assertion) {
        this.assertion = assertion;
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
}
