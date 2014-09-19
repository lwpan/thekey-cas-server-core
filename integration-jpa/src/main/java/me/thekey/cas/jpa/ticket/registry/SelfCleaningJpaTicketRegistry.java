package me.thekey.cas.jpa.ticket.registry;

import me.thekey.cas.ticket.registry.ForwardingTicketRegistryState;
import me.thekey.cas.ticket.registry.SelfCleaningTicketRegistry;
import org.jasig.cas.ticket.ServiceTicketImpl;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SelfCleaningJpaTicketRegistry extends ForwardingTicketRegistryState implements
        SelfCleaningTicketRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(SelfCleaningJpaTicketRegistry.class);

    private static final int DEFAULT_BATCH_SIZE = 1000;

    @NotNull
    private final TicketRegistry registry;

    @NotNull
    @PersistenceUnit
    private EntityManagerFactory emf;

    private int batchSize = DEFAULT_BATCH_SIZE;

    public SelfCleaningJpaTicketRegistry(final TicketRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected TicketRegistry delegate() {
        return this.registry;
    }

    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void clean() {
        try {
            for (final Class<? extends Ticket> ticketClass : Arrays.asList(ServiceTicketImpl.class,
                    TicketGrantingTicketImpl.class)) {
                int total = 0;
                int stale = 0;
                long oldestTicket = 0;
                do {
                    final List<Ticket> ticketsToRemove = new ArrayList<>();

                    // start transaction
                    final EntityManager em = emf.createEntityManager();
                    final EntityTransaction tx = em.getTransaction();
                    tx.begin();

                    // fetch a batch of tickets to clean
                    final CriteriaBuilder cb = em.getCriteriaBuilder();
                    final CriteriaQuery<Ticket> q = cb.createQuery(Ticket.class);
                    final Root<? extends Ticket> t = q.from(ticketClass);
                    q.select(t);
                    q.where(cb.gt(t.<Number>get("creationTime"), cb.parameter(Long.class, "oldestTicket")));
                    q.orderBy(cb.asc(t.get("creationTime")));
                    final List<Ticket> tickets = em.createQuery(q).setParameter("oldestTicket",
                            oldestTicket).setMaxResults(this.batchSize).getResultList();
                    total = tickets.size();

                    // end transaction
                    tx.commit();

                    // filter out expired tickets
                    for (final Ticket ticket : tickets) {
                        if (ticket.isExpired()) {
                            ticketsToRemove.add(ticket);
                        }
                    }
                    stale = ticketsToRemove.size();

                    // update oldestTicket value (tickets are sorted by creation time, so just look at the last ticket)
                    if (total > 0) {
                        oldestTicket = tickets.get(total - 1).getCreationTime();
                    }

                    // remove all expired tickets
                    LOG.info("{} out of {} {} tickets found to be removed.", stale, total, ticketClass.getSimpleName());
                    for (final Ticket ticket : ticketsToRemove) {
                        // CAS-686: Expire TGT to trigger single sign-out
                        if (ticket instanceof TicketGrantingTicket) {
                            ((TicketGrantingTicket) ticket).expire();
                        }
                        this.deleteTicket(ticket.getId());
                    }
                } while (total == batchSize && stale > 0.25 * total); // there may be more tickets and >25% were stale
            }
        } catch (final PersistenceException e) {
            // log & suppress any PersistenceExceptions thrown, clean() isn't critical functionality
            LOG.error("Error cleaning ticket registry", e);
        }
    }
}
