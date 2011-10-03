package org.ccci.gto.persist;

import java.util.Collection;

/**
 * <b>CrudDao</b> defines the full range of CRUD based operations beyond that of
 * strict queries for {@link Dao}'s.
 * 
 * @author Daniel Frett
 */
public interface CrudDao<T> extends QueryDao<T> {
    /**
     * Persist (create) the object for the first time.
     * 
     * @param object
     *            Object to be saved/created..
     */
    public void save(final T object);

    /**
     * Either save or update the given object, depending upon the value of its
     * identifier property.
     * 
     * @param object
     *            Object to be saved/updated.
     */
    public void saveOrUpdate(final T object);

    /**
     * Update the persistent state associated with the given identifier. An
     * exception is thrown if there is a persistent instance with the same
     * identifier in the current session.
     * 
     * @param object
     *            Object to be updated.
     */
    public void update(final T object);

    /**
     * Remove (delete) the persistent object from the datastore
     * 
     * @param object
     *            Object to be deleted.
     */
    public void delete(final T object);

    /**
     * Method to save a Collection of ModelObjects
     * 
     * @param objects
     *            Collection of ModelObject's to be saved/updated.
     */
    public void saveAll(final Collection<? extends T> objects);
}
