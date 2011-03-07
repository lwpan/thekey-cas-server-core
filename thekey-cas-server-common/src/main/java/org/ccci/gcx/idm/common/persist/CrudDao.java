package org.ccci.gcx.idm.common.persist ;


import java.io.Serializable;
import java.util.Collection;

import org.ccci.gcx.idm.common.model.ModelObject;

/**
 * <b>CrudDao</b> defines the full range of CRUD based operations beyond that
 * of strict queries for {@link Dao}'s.
 *
 * @author Greg Crider  Oct 12, 2006  10:37:22 AM
 */
public interface CrudDao extends QueryDao
{

    /**
     * Persist (create) the object for the first time.
     *
     * @param a_Object Object to be saved/created..
     */
    public Serializable save( Object a_Object ) ;


    /**
     * Either save or update the given object, depending upon the value of
     * its identifier property.
     *
     * @param a_Object Object to be saved/updated.
     */
    public void saveOrUpdate( Object a_Object ) ;


    /**
     * Update the persistent state associated with the given identifier. An
     * exception is thrown if there is a persistent instance with the same
     * identifier in the current session.
     *
     * @param a_Object Object to be updated.
     */
    public void update( Object a_Object ) ;


    /**
     * Remove (delete) the persistent object from the datastore
     *
     * @param a_Object Object to be deleted.
     */
    public void delete( Object a_Object ) ;


    /**
     * Iterate over the collection (all members should be instances of {@link ModelObject})
     * and perform a save/update for each.
     *
     * @param a_ModelObjects Collection of {@link ModelObject}'s to be saved/updated.
     */
    public void saveAll( Collection<ModelObject> a_ModelObjects ) ;

}