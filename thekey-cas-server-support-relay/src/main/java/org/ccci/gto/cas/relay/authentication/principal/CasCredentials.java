package org.ccci.gto.cas.relay.authentication.principal;

import java.util.BitSet;

import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.client.validation.Assertion;

public final class CasCredentials implements Credentials, TheKeyCredentials {
    private static final long serialVersionUID = 7917581886659605981L;

    private final BitSet locks = new BitSet();

    private String service;

    private String ticket;

    private Assertion assertion;

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

    public void setObserveLock(final Lock lock, final boolean value) {
        synchronized (locks) {
            locks.set(lock.index, !value);
        }
    }

    @Override
    public boolean observeLock(final Lock lock) {
        synchronized (locks) {
            return !locks.get(lock.index);
        }
    }
}
