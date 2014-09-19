package me.thekey.cas.ticket.registry;

import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.registry.TicketRegistry;

import java.util.Collection;

public abstract class ForwardingTicketRegistry implements TicketRegistry {
    protected abstract TicketRegistry delegate();

    @Override
    public void addTicket(final Ticket ticket) {
        delegate().addTicket(ticket);
    }

    @Override
    public Ticket getTicket(final String ticketId, final Class<? extends Ticket> clazz) {
        return delegate().getTicket(ticketId, clazz);
    }

    @Override
    public Ticket getTicket(final String ticketId) {
        return delegate().getTicket(ticketId);
    }

    @Override
    public boolean deleteTicket(final String ticketId) {
        return delegate().deleteTicket(ticketId);
    }

    @Override
    public Collection<Ticket> getTickets() {
        return delegate().getTickets();
    }
}
