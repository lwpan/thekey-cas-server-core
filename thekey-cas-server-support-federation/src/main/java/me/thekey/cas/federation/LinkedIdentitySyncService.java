package me.thekey.cas.federation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.thekey.cas.federation.api.CommunicationException;
import me.thekey.cas.federation.api.IdentityLinkingServiceApi;
import me.thekey.cas.federation.model.Identity;
import me.thekey.cas.federation.model.LinkedIdentity;
import org.ccci.gto.persistence.tx.RetryingTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LinkedIdentitySyncService {
    private static final Logger LOG = LoggerFactory.getLogger(LinkedIdentitySyncService.class);

    @Inject
    @NotNull
    private IdentityLinkingServiceApi api;

    @PersistenceContext
    private EntityManager em;

    @Inject
    @NotNull
    private RetryingTransactionService txService;

    private final Cache<Identity, Boolean> lastSync = CacheBuilder.newBuilder().concurrencyLevel(20).maximumSize
            (10000).expireAfterWrite(15, TimeUnit.MINUTES).build();

    public void syncIdentities(final Identity identity) {
        this.syncIdentities(identity, false);
    }

    public void syncIdentities(final Identity identity, final boolean force) {
        try {
            if (force) {
                lastSync.invalidate(identity);
            }

            lastSync.get(identity, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    switch (identity.getType()) {
                        case THEKEY:
                            syncTheKeyIdentities(identity);
                            break;
                        default:
                            syncFederatedIdentities(identity);
                            break;
                    }

                    return Boolean.TRUE;
                }
            });
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof CommunicationException) {
                LOG.error("error communicating with Identity Linking Service API", cause);
            } else {
                LOG.error("Unexpected exception", e);
            }
        }
    }

    private void syncTheKeyIdentities(final Identity id1) throws CommunicationException {
        final List<Identity> current = this.api.getLinkedIdentities(id1);

        txService.inRetryingTransaction(new Runnable() {
            @Override
            public void run() {
                // retrieve currently cached links
                final Map<Identity, LinkedIdentity> cached = new HashMap<>();
                final TypedQuery<LinkedIdentity> query1 = em.createNamedQuery("LinkedIdentity.findByGuid",
                        LinkedIdentity.class);
                query1.setParameter("guid", id1.getId());
                for (final LinkedIdentity link : query1.getResultList()) {
                    cached.put(link.getIdentity(), link);
                }

                // create a set of links to add/delete
                final Set<LinkedIdentity> toAdd = new HashSet<>();
                final Set<LinkedIdentity> toDelete = new HashSet<>(cached.values());

                // iterate over current links
                for (final Identity id2 : current) {
                    if (cached.containsKey(id2)) {
                        // we don't need to delete the existing linked identity
                        toDelete.remove(cached.get(id2));
                    } else {
                        // delete any existing links for this identity
                        final TypedQuery<LinkedIdentity> query2 = em.createNamedQuery("LinkedIdentity" +
                                ".findByIdentity", LinkedIdentity.class);
                        query2.setParameter("type", id2.getType());
                        query2.setParameter("id", id2.getId());
                        toDelete.addAll(query2.getResultList());

                        // create a new LinkedIdentity
                        toAdd.add(new LinkedIdentity(id1.getId(), id2));
                    }
                }

                // remove any old linked identities and add new linked identities
                for (final LinkedIdentity identity : toDelete) {
                    em.remove(identity);
                }
                em.flush();
                for (final LinkedIdentity identity : toAdd) {
                    em.persist(identity);
                }
            }
        });
    }

    private void syncFederatedIdentities(final Identity id1) throws CommunicationException {
        final List<Identity> current = this.api.getLinkedIdentities(id1);

        txService.inRetryingTransaction(new Runnable() {
            @Override
            public void run() {
                // retrieve currently cached links
                final Map<String, LinkedIdentity> cached = new HashMap<>();
                final TypedQuery<LinkedIdentity> query1 = em.createNamedQuery("LinkedIdentity.findByIdentity",
                        LinkedIdentity.class);
                query1.setParameter("type", id1.getType());
                query1.setParameter("id", id1.getId());
                for (final LinkedIdentity link : query1.getResultList()) {
                    cached.put(link.getGuid(), link);
                }

                // create a set of links to add/delete
                final Set<LinkedIdentity> toAdd = new HashSet<>();
                final Set<LinkedIdentity> toDelete = new HashSet<>(cached.values());

                // iterate over current links
                for (final Identity id2 : current) {
                    // skip links that we don't care about
                    if (id2.getType() != Identity.Type.THEKEY) {
                        continue;
                    }

                    if (cached.containsKey(id2.getId())) {
                        // we don't need to update the existing linked identity
                        toDelete.remove(cached.get(id2.getId()));
                    } else {
                        // check for a link to a different identity
                        final TypedQuery<LinkedIdentity> query2 = em.createNamedQuery("LinkedIdentity" + "" +
                                ".findByGuidAndType", LinkedIdentity.class);
                        query2.setParameter("guid", id2.getId());
                        query2.setParameter("type", id1.getType());
                        toDelete.addAll(query2.getResultList());

                        // create a new LinkedIdentity
                        toAdd.add(new LinkedIdentity(id2.getId(), id1));
                    }
                }


                // remove any old linked identities and add new linked identities
                for (final LinkedIdentity identity : toDelete) {
                    em.remove(identity);
                }
                em.flush();
                for (final LinkedIdentity identity : toAdd) {
                    em.persist(identity);
                }
            }
        });
    }
}
