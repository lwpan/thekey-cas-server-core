package org.ccci.gcx.idm.core.persist.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.persist.hibernate.AbstractCrudDao;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.GcxUserDao;
import org.springframework.util.Assert;

/**
 * <b>GcxUserDaoImpl</b> is the concrete implementation of {@link GcxUserDao}.
 *
 * @author Greg Crider  Oct 21, 2008  1:21:50 PM
 */
public class GcxUserDaoImpl extends AbstractCrudDao implements GcxUserDao
{
    protected static final Log log = LogFactory.getLog( GcxUserDaoImpl.class ) ;

    
    /**
     * Return the maximum number of allowed search results.
     * 
     * @return Unlimited, or <tt>SEARCH_NO_LIMIT</tt>
     * 
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#getMaxSearchResults()
     */
    public int getMaxSearchResults()
    {
        return Constants.SEARCH_NO_LIMIT ;
    }
    
    
    /**
     * @param a_GUID
     * @return
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#findByGUID(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public GcxUser findByGUID( String a_GUID )
    {
        GcxUser result = null ;
        
        List<GcxUser> list = (List<GcxUser>)this.findByNamedQueryAndNamedParam( 
                                Constants.QUERY_GCXUSER_FINDBYGUID,
                                "guid", 
                                a_GUID 
                             ) ;
        
        if ( ( list != null ) && ( list.size() > 0 ) ) {
            result = list.get( 0 ) ;
        }
        
        return result ;
    }

    
    /**
     * @param a_Email
     * @return
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#findByEmail(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public GcxUser findByEmail( String a_Email ) 
    {
        GcxUser result = null ;
        
        List<GcxUser> list = (List<GcxUser>)this.findByNamedQueryAndNamedParam( 
                                Constants.QUERY_GCXUSER_FINDBYEMAIL,
                                "email", 
                                a_Email.toLowerCase() 
                             ) ;
        
        if ( ( list != null ) && ( list.size() > 0 ) ) {
            result = list.get( 0 ) ;
        }
        
        return result ;
    }
    
    
    /**
     * Find all users matching the first name pattern.
     * 
     * @param a_FirstNamePattern Pattern used for matching first name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByFirstName( String a_FirstNamePattern )
    {
        throw new UnsupportedOperationException( "This method is not currently implemented." ) ;
    }
    
    
    /**
     * Find all users matching the last name pattern.
     * 
     * @param a_LastNamePattern Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByLastName( String a_LastNamePattern )
    {
        throw new UnsupportedOperationException( "This method is not currently implemented." ) ;
    }
    
    
    /**
     * Find all users matching the e-mail pattern.
     * 
     * @param a_EmailPattern Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByEmail( String a_EmailPattern )
    {
        throw new UnsupportedOperationException( "This method is not currently implemented." ) ;
    }
    
    
    /**
     * Find all users matching the userid pattern.
     * 
     * @param a_UseridPattern Pattern used for matching userid.
     * @param a_IncludeDeactivated If <tt>true</tt> then deactivated accounts are included.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByUserid( String a_UseridPattern, boolean a_IncludeDeactivated )
    {
        throw new UnsupportedOperationException( "This method is not currently implemented." ) ;
    }

    
    /**
     * Save the {@link GcxUser} object.
     * 
     * @param a_GcxUser New {@link GcxUser} object to be saved.
     */
    public void save( GcxUser a_GcxUser )
    {
        Assert.hasText( a_GcxUser.getEmail(), "E-mail address cannot be blank." ) ;
        
        String email = ( a_GcxUser.getEmail().startsWith( Constants.PREFIX_DEACTIVATED ) ) ? a_GcxUser.getEmail() : a_GcxUser.getEmail().toLowerCase() ;
        
        a_GcxUser.setEmail( email ) ;

        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "********** Saving new user: " + a_GcxUser ) ;
        super.save( a_GcxUser ) ;
    }

    
    /**
     * @param a_GcxUser
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#saveOrUpdate(org.ccci.gcx.idm.core.model.impl.GcxUser)
     */
    public void saveOrUpdate( GcxUser a_GcxUser )
    {
        Assert.hasText( a_GcxUser.getEmail(), "E-mail address cannot be blank." ) ;
        
        String email = ( a_GcxUser.getEmail().startsWith( Constants.PREFIX_DEACTIVATED ) ) ? a_GcxUser.getEmail() : a_GcxUser.getEmail().toLowerCase() ;
        
        a_GcxUser.setEmail( email ) ;

        super.saveOrUpdate( a_GcxUser ) ;
    }
    
    
    /**
     * Delete the {@link GcxUser}.
     * 
     * @param a_GcxUser {@link GcxUser} to be deleted.
     * 
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#delete(org.ccci.gcx.idm.core.model.impl.GcxUser)
     */
    public void delete( GcxUser a_GcxUser )
    {
        super.delete( a_GcxUser ) ;
    }

}
