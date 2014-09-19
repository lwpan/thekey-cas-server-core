package me.thekey.cas.ticket.registry;

import org.jasig.cas.ticket.registry.RegistryCleaner;
import org.jasig.cas.ticket.registry.support.LockingStrategy;
import org.jasig.cas.ticket.registry.support.NoOpLockingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

public class SelfCleaningTicketRegistryCleaner implements RegistryCleaner {
    private static final Logger LOG = LoggerFactory.getLogger(SelfCleaningTicketRegistryCleaner.class);

    @Inject
    @NotNull
    private SelfCleaningTicketRegistry ticketRegistry;

    @NotNull
    private LockingStrategy lock = new NoOpLockingStrategy();

    /**
     * @param registry The SelfCleaningTicketRegistry to set.
     */
    public void setTicketRegistry(final SelfCleaningTicketRegistry registry) {
        this.ticketRegistry = registry;
    }

    /**
     * @param strategy Ticket cleanup locking strategy.  An exclusive locking
     *                 strategy is preferable if not required for some ticket backing stores,
     *                 such as JPA, in a clustered CAS environment.
     */
    public void setLock(final LockingStrategy strategy) {
        this.lock = strategy;
    }

    @Override
    public void clean() {
        LOG.info("Beginning ticket cleanup.");
        LOG.debug("Attempting to acquire ticket cleanup lock.");
        if (!this.lock.acquire()) {
            LOG.info("Could not obtain lock.  Aborting cleanup.");
            return;
        }
        LOG.debug("Acquired lock.  Proceeding with cleanup.");
        try {
            this.ticketRegistry.clean();
        } finally {
            LOG.debug("Releasing ticket cleanup lock.");
            this.lock.release();
        }

        LOG.info("Finished ticket cleanup.");
    }
}
