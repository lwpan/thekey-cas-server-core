package me.thekey.cas.relay.service;

import me.thekey.cas.federation.LinkedIdentitySyncService;
import me.thekey.cas.federation.model.Identity;
import me.thekey.cas.federation.model.LinkedIdentity;
import me.thekey.cas.service.UserManager;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.persistence.tx.RetryingTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.concurrent.Callable;

public class RelayUserManagerImpl implements RelayUserManager {
    private static final Logger LOG = LoggerFactory.getLogger(RelayUserManagerImpl.class);

    @Inject
    @NotNull
    private UserManager userManager;

    @PersistenceContext
    private EntityManager em;

    @Inject
    @NotNull
    private RetryingTransactionService txService;

    @Inject
    @NotNull
    private LinkedIdentitySyncService sync;

    public void setUserManager(final UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public GcxUser findUserByRelayGuid(final String relayGuid) {
        // sync the Relay identity first
        sync.syncIdentities(Identity.relay(relayGuid));

        // lookup the user by the current relay guid
        final String guid;
        try {
            guid = txService.inRetryingTransaction(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final TypedQuery<LinkedIdentity> query = em.createNamedQuery("LinkedIdentity.findByIdentity",
                            LinkedIdentity.class);
                    query.setParameter("type", Identity.Type.RELAY);
                    query.setParameter("id", relayGuid);
                    try {
                        return query.getSingleResult().getGuid();
                    } catch (final NoResultException e) {
                        return null;
                    }
                }
            });
        } catch (final Exception e) {
            LOG.debug("Unexpected error looking up relay account", e);
            return null;
        }

        // return the user for the found guid
        return guid != null ? this.userManager.findUserByGuid(guid) : null;
    }

    @Override
    public String getRelayGuid(final GcxUser user) {
        // sync the user's identity first
        sync.syncIdentities(Identity.thekey(user.getGUID()));

        // look up the relay guid for the specified user
        try {
            return txService.inRetryingTransaction(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final TypedQuery<LinkedIdentity> query = em.createNamedQuery("LinkedIdentity.findByGuidAndType",
                            LinkedIdentity.class);
                    query.setParameter("guid", user.getGUID());
                    query.setParameter("type", Identity.Type.RELAY);
                    try {
                        return query.getSingleResult().getIdentity().getId();
                    } catch (final NoResultException e) {
                        return null;
                    }
                }
            });
        } catch (final Exception e) {
            LOG.error("Unexpected exception", e);
            return null;
        }
    }
}
