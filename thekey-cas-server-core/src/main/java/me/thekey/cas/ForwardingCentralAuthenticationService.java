package me.thekey.cas;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.validation.Assertion;

public abstract class ForwardingCentralAuthenticationService implements CentralAuthenticationService {
    protected abstract CentralAuthenticationService delegate();

    @Override
    public String createTicketGrantingTicket(final Credentials credentials) throws TicketException {
        return delegate().createTicketGrantingTicket(credentials);
    }

    @Override
    public String grantServiceTicket(final String ticketGrantingTicketId, final Service service) throws
            TicketException {
        return delegate().grantServiceTicket(ticketGrantingTicketId, service);
    }

    @Override
    public String grantServiceTicket(final String ticketGrantingTicketId, final Service service,
                                     final Credentials credentials) throws TicketException {
        return delegate().grantServiceTicket(ticketGrantingTicketId, service, credentials);
    }

    @Override
    public Assertion validateServiceTicket(final String serviceTicketId, final Service service) throws TicketException {
        return delegate().validateServiceTicket(serviceTicketId, service);
    }

    @Override
    public void destroyTicketGrantingTicket(final String ticketGrantingTicketId) {
        delegate().destroyTicketGrantingTicket(ticketGrantingTicketId);
    }

    @Override
    public String delegateTicketGrantingTicket(final String serviceTicketId, final Credentials credentials) throws
            TicketException {
        return delegate().delegateTicketGrantingTicket(serviceTicketId, credentials);
    }
}
