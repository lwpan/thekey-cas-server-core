package org.ccci.gcx.idm.common.persist.hibernate ;

import java.io.Serializable ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Iterator ;

import org.springframework.util.Assert ;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.common.persist.CrudDao;
import org.ccci.gcx.idm.common.persist.StaleObjectStateException;
import org.hibernate.TransientObjectException;


/**
 * <b>AbstractCrudDao</b> is the concrete, base implentor of {@link CrudDao}, and
 * provides full CRUD functionality based on Hibernate.
 *
 * @author Greg Crider  Oct 12, 2006  3:00:52 PM
 */
public abstract class AbstractCrudDao extends AbstractQueryDao implements CrudDao
{

    /**
     * Save the specified object.
     * 
     * @param a_Object Object to be saved.
     */
    public Serializable save( Object a_Object )
    {
        return this.getSession().save( a_Object ) ;
    }


    /**
     * Save or update the specified object.
     * 
     * @param a_Object Object to be saved or updated.
     */
    public void saveOrUpdate( Object a_Object )
    {
        try {
            this.getSession().saveOrUpdate( a_Object ) ;
        } catch ( org.hibernate.StaleObjectStateException sose ) {
            throw new StaleObjectStateException( "Object was change before this commit took place", sose ) ;
        }
    }


    /**
     * Update the specified object.
     * 
     *  @param a_Object Object to be updated.
     */
    public void update( Object a_Object )
    {
        try {
            this.getSession().update( a_Object ) ;
        } catch ( org.hibernate.StaleObjectStateException sose ) {
            throw new StaleObjectStateException( "Object was change before this commit took place", sose ) ;
        }
    }


    /**
     * Delete the specified object.
     * 
     * @param a_Object Object to be deleted.
     */
    public void delete( Object a_Object )
    {
        this.getSession().delete( a_Object ) ;
    }


    /**
     * Save all objects found in the specified collection.
     * 
     * @param a_Objects {@link Collection} of objects to save.
     */
    @SuppressWarnings("unchecked")
    public void saveAll( Collection a_Objects )
    {
        Assert.isTrue( a_Objects != null && !a_Objects.isEmpty() ) ;
        for( Iterator i = a_Objects.iterator(); i.hasNext(); )
            saveOrUpdate( i.next() ) ;
    }

    /**
     * Save objects, commit DB changes and free up the memory.
     * 
     * @param a_Objects {@link Collection} of object to save.
     * @param a_MaxObjectsInSession Maximum number of objects in session.
     */
	public void saveAndEvict( Collection<? extends ModelObject> a_Objects, int a_MaxObjectsInSession )
	{
		Collection<ModelObject> savedEntities = new ArrayList<ModelObject>( a_MaxObjectsInSession ) ;

		for( ModelObject entity: a_Objects ) {
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
    @SuppressWarnings("unchecked")
    public void evictAll( Collection a_Objects )
    {
        for( Iterator iter = a_Objects.iterator(); iter.hasNext(); ) {
            ModelObject entity = (ModelObject)iter.next() ;
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