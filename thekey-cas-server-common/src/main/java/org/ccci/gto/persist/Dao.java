package org.ccci.gto.persist;

import java.util.Collection;

public interface Dao<T> {
    /**
     * Persist (create) the object for the first time.
     *
     * @param object Object to be saved/created..
     */
    public void save(final T object);

    /**
     * Update the persistent state associated with the given identifier. An
     * exception is thrown if there is a persistent instance with the same
     * identifier in the current session.
     *
     * @param object Object to be updated.
     */
    public void update(final T object);

    /**
     * Method to save a Collection of Objects
     *
     * @param objects Collection of Object's to be saved/updated.
     */
    public void saveAll(final Collection<? extends T> objects);
}
