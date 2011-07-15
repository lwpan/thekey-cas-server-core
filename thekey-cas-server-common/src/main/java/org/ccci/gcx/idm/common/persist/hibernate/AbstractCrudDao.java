package org.ccci.gcx.idm.common.persist.hibernate ;

import java.util.ArrayList;
import java.util.Collection;

import org.ccci.gcx.idm.common.persist.StaleObjectStateException;
import org.ccci.gto.persist.CrudDao;
import org.hibernate.TransientObjectException;
import org.springframework.util.Assert;

/**
 * <b>AbstractCrudDao</b> is the concrete, base implentor of {@link CrudDao},
 * and provides full CRUD functionality based on Hibernate.
 * 
 * @author Greg Crider Oct 12, 2006 3:00:52 PM
 */
public abstract class AbstractCrudDao<T> extends AbstractQueryDao<T> implements
	CrudDao<T> {
    /**
     * Save the specified object.
     * 
     * @param object
     *            Object to be saved.
     */
    public void save(final T object) {
	this.getSession().save(object);
    }

    /**
     * Save or update the specified object.
     * 
     * @param object
     *            Object to be saved or updated.
     */
    public void saveOrUpdate(final T object) {
	try {
	    this.getSession().saveOrUpdate(object);
	} catch (org.hibernate.StaleObjectStateException sose) {
	    throw new StaleObjectStateException(
		    "Object was change before this commit took place", sose);
	}
    }

    /**
     * Update the specified object.
     * 
     * @param object
     *            Object to be updated.
     */
    public void update(final T object) {
	try {
	    this.getSession().update(object);
	} catch (org.hibernate.StaleObjectStateException sose) {
	    throw new StaleObjectStateException(
		    "Object was change before this commit took place", sose);
	}
    }

    /**
     * Delete the specified object.
     * 
     * @param object
     *            Object to be deleted.
     */
    public void delete(final T object) {
	this.getSession().delete(object);
    }

    /**
     * Save all objects found in the specified collection.
     * 
     * @param objects
     *            {@link Collection} of objects to save.
     */
    public void saveAll(final Collection<? extends T> objects) {
	Assert.notNull(objects);
	for (final T entry : objects) {
	    this.saveOrUpdate(entry);
	}
    }

    /**
     * Save objects, commit DB changes and free up the memory.
     * 
     * @param a_Objects {@link Collection} of object to save.
     * @param a_MaxObjectsInSession Maximum number of objects in session.
     */
    public void saveAndEvict(Collection<? extends T> a_Objects,
	    int a_MaxObjectsInSession)
	{
	Collection<T> savedEntities = new ArrayList<T>(a_MaxObjectsInSession);

		for (final T entity : a_Objects) {
			this.getSession().save( entity ) ;
			savedEntities.add( entity ) ;
			if ( savedEntities.size() % a_MaxObjectsInSession == 0 ) {
				this.getSession().flush() ;
				this.evictAll( savedEntities ) ;
				savedEntities.clear() ;
			}
		}
	}

    /**
     * Evicts all objects in the collection from Hibernate session.
     * 
     * @param a_Objects {@link Collection} of objects to evict.
     */
    public void evictAll(Collection<? extends T> a_Objects)
	{
	for (final T entity : a_Objects) {
			this.evict( entity ) ;
		}
	}

    /**
     * Evicts object from Hibernate session.
     * 
     * @param a_Object Object to evict.
     */
    public void evict( Object a_Object )
    {
        this.getSession().evict( a_Object ) ;
    }
    
    
    /**
     * Flush the Hibernate session.
     */
    public void flush()
    {
        this.getSession().flush() ;
    }
    
    
    /**
     * Clear the current Hibernate session.
     */
    public void clear()
    {
        this.getSession().clear() ;
    }
    
    
    /**
     * Forceably release the current Hibernate session.
     */
    public void release()
    {
        this.releaseSession( this.getSession() ) ;
    }
    
    
    /**
     * Test the object to see if it is in the current session or cache.
     * 
     * @param a_Object Object to test.
     * 
     * @return <tt>True</tt> if the object is transient because it is not associated with the
     * current session.
     */
    public boolean isTransient( Object a_Object )
    {
        boolean result = false ;
        
        try {
            this.getSession().getIdentifier( a_Object ) ;
        } catch ( TransientObjectException toe ) {
            result = true ;
        }
        
        return result ;
    }
}