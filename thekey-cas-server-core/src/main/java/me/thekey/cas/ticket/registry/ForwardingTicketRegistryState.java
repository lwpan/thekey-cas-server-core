package me.thekey.cas.ticket.registry;

import org.jasig.cas.monitor.TicketRegistryState;
import org.jasig.cas.ticket.registry.TicketRegistry;

public abstract class ForwardingTicketRegistryState extends ForwardingTicketRegistry implements TicketRegistryState {
    @Override
    public int sessionCount() {
        final TicketRegistry delegate = delegate();
        if (delegate instanceof TicketRegistryState) {
            return ((TicketRegistryState) delegate).sessionCount();
        }
        return 0;
    }

    @Override
    public int serviceTicketCount() {
        final TicketRegistry delegate = delegate();
        if (delegate instanceof TicketRegistryState) {
            return ((TicketRegistryState) delegate).serviceTicketCount();
        }
        return 0;
    }
}
