package org.ccci.gto.persist;

import java.io.Serializable;

/**
 * <b>QueryDao</b> defines those methods for {@link Dao}'s used strictly for
 * query based operation. The intention here is to shield client code from the
 * ability to perform full CRUD operations. If the underlying backing store
 * resource is truly read-only (such as a view), then the developer must be
 * restricted to fetch based operations only. This interface, and its concrete
 * implementors, is a means of enforcing this policy.
 * 
 * @author Daniel Frett
 */
public interface QueryDao<T> extends Dao<T> {
    /**
     * Load a persistent object based on the Class (e.g.,
     * <code>com.fanniemae.model.User</code>) and the key (e.g.,
     * <code>com.fanniemae.model.User</code>'s property <code>userId</code>).
     * Load object matching the given key and return it.
     * 
     * @param key
     *            Unique lookup key for model class.
     */
    public T get(final Serializable key);

    /**
     * Load a persistent object based on the domain model class and the
     * specified key. Load object matching the given key and return it.
     * 
     * @param key
     *            Unique lookup key for model class.
     */
    public T load(final Serializable key);
}
