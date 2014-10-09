package me.thekey.cas;

import org.ccci.gto.persistence.DeadLockRetry;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.validation.Assertion;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

public class DeadlockRetryingCentralAuthenticationService extends ForwardingCentralAuthenticationService {
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    public void setCentralAuthenticationService(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    @Override
    protected CentralAuthenticationService delegate() {
        return this.centralAuthenticationService;
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public String createTicketGrantingTicket(final Credentials credentials) throws TicketException {
        return super.createTicketGrantingTicket(credentials);
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public String grantServiceTicket(final String ticketGrantingTicketId, final Service service) throws
            TicketException {
        return super.grantServiceTicket(ticketGrantingTicketId, service);
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public String grantServiceTicket(final String ticketGrantingTicketId, final Service service,
                                     final Credentials credentials) throws TicketException {
        return super.grantServiceTicket(ticketGrantingTicketId, service, credentials);
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public Assertion validateServiceTicket(final String serviceTicketId, final Service service) throws TicketException {
        return super.validateServiceTicket(serviceTicketId, service);
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void destroyTicketGrantingTicket(final String ticketGrantingTicketId) {
        super.destroyTicketGrantingTicket(ticketGrantingTicketId);
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public String delegateTicketGrantingTicket(final String serviceTicketId, final Credentials credentials) throws
            TicketException {
        return super.delegateTicketGrantingTicket(serviceTicketId, credentials);
    }
}
