package org.ccci.gcx.idm.common.persist ;

import java.io.Serializable;

/**
 * <b>QueryDao</b> defines those methods for {@link Dao}'s used strictly for
 * query based operation. The intention here is to shield client code from the
 * ability to perform full CRUD operations. If the underlying backing store resource
 * is truly read-only (such as a view), then the developer must be restricted to
 * fetch based operations only. This interface, and its concrete implementors, is a
 * means of enforcing this policy.
 *
 * @author Greg Crider  Oct 12, 2006  10:37:22 AM
 */
public interface QueryDao extends Dao
{

	/**
	 * Load a persistent object based on the Class (e.g., <code>com.fanniemae.model.User</code>) and the
     * key (e.g., <code>com.fanniemae.model.User</code>'s property <code>userId</code>). Load object
     * matching the given key and return it.
     *
     * @param a_Key Unique lookup key for model class.
	 */
	public Object get( Serializable a_Key ) ;


    /**
     * Load a persistent object based on the domain model class and the specified key. Load object
     * matching the given key and return it.
     *
     * @param a_Key Unique lookup key for model class.
     */
    public Object load( Serializable a_Key ) ;


    /**
     * Eagerly intializes the object by loading it if it was proxied and initializing any lazy fields.
     * If the object was disconnected from the session (i.e., transient), a copy is created to preserve
     * the original object as lazy.
     *
     * <b>Note:</b> Lazy collections are not copied so both the original and the copy will point to
     * the same collection
     *
     * @param a_Object Persisted object that needs to be initialized.
     */
    public Object initialize( Object a_Object ) ;


    /**
     * Test the object to determine if its lazy properties have been initialized.
     *
     * @param a_Object Object to test for initialization.
     *
     * @return <tt>True</tt> if all of the properties have been initialized.
     */
    public boolean isInitialized( Object a_Object ) ;

}