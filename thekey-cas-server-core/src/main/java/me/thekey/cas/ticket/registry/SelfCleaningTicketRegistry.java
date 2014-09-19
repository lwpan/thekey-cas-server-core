package me.thekey.cas.ticket.registry;

import org.jasig.cas.ticket.registry.TicketRegistry;

public interface SelfCleaningTicketRegistry extends TicketRegistry {
    void clean();
}
