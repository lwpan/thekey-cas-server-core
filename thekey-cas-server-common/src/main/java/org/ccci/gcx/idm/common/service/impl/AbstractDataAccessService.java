package org.ccci.gcx.idm.common.service.impl;

import java.util.Map;

import org.ccci.gcx.idm.common.persist.Dao;
import org.ccci.gcx.idm.common.service.DataAccessService;


/**
 * <b>AbstractDataAccessService</b> contains the common functionality required by
 * all concrete implementations of {@link DataAccessService}.
 * 
 * The principle philosophy is that the calling
 * client will not directly access the dao. Services methods are meant to
 * capture coarse-grained business functions.
 * 
 * <b>Note:</b> there is no way to guarantee that all of the {@link Dao} beans
 * have been properly set for the concrete implementation. The only possible
 * safegaurds (such as requiring a validation method to be implemented, and
 * then using the Spring init-method attribute) still require a proper wiring
 * of the bean. Therefore, the only check in place is the <code>getDao</code>
 * method which will ensure that the {@link Dao} is present.
 *
 * @author Greg Crider  Jan 2, 2008  3:10:18 PM
 */
public abstract class AbstractDataAccessService extends AbstractBusinessService implements DataAccessService
{
    /** Group of Dao's with their bean id/name as the key */
    private Map<String,Dao> m_DaoGroup = null ;

    
    /**
     * @param a_DaoGroup The {@link Dao} group to set. This is the collection
     * of {@link Dao}'s to be used internally by the service. The map entry key
     * is the bean or dao name, and the element is a reference to the dao bean
     * itself. If there is currently no defined group, this parameter is set. If a
     * group already exists, the specified group is added.
     */
    public void setDaoGroup( Map<String,Dao> a_DaoGroup )
    {
        if ( this.m_DaoGroup == null ) {
            this.m_DaoGroup = a_DaoGroup ;
        } else {
            this.m_DaoGroup.putAll( a_DaoGroup ) ;
        }
    }
    
    
    /**
     * Get the specified {@link Dao} object.
     * 
     * @param a_Name {@link Dao} name.
     * 
     * @return {@link Dao} object.
     * 
     * @exception IllegalArgumentException if the specified {@link Dao} was not properly
     *         configured (i.e., it is not present).
     */
    protected Dao getDao( String a_Name )
    {
        Dao result = null ;
        
        // Make sure there are actually some Dao's defined
        if ( this.m_DaoGroup == null ) {
            /*= ERROR =*/ log.error( "No Dao's were configured for the service" ) ;
            throw new IllegalArgumentException( "No Dao's were configured for the service" ) ;
        // Make sure the specified Dao is actually present
        } else if ( !this.m_DaoGroup.containsKey( a_Name ) ) {
            String errorMsg = "The Dao with Name(" + a_Name + ") is not defined" ;
            /*= ERROR =*/ log.error( errorMsg ) ;
            throw new IllegalArgumentException( errorMsg ) ;
        }
        
        // Get the Dao
        result = (Dao)this.m_DaoGroup.get( a_Name ) ;
        
        return result ;
    }

}
