package org.ccci.gcx.idm.core.persist.ldap;

import java.io.Serializable;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.GcxUserDao;
import org.ccci.gcx.idm.core.persist.ldap.spring.mapper.GcxUserMapper;
import org.springframework.ldap.control.SortControlDirContextProcessor;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.util.Assert;

/**
 * <b>GcxUserDaoImpl</b> is the concrete implementation of the {@link GcxUserDao} with
 * LDAP as the backing store.
 *
 * @author Greg Crider  Oct 21, 2008  2:08:57 PM
 */
public class GcxUserDaoImpl extends AbstractLdapCrudDao implements GcxUserDao
{
    protected static final Log log = LogFactory.getLog( GcxUserDaoImpl.class ) ;
    
    
    /**
     * Find all users matching the pattern specified in the filter.
     * 
     * @param a_Filter Filter for the specified pattern.
     * @param a_SortKey Attribute key for sorting the results.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    @SuppressWarnings("unchecked")
    private List<GcxUser> findAllByPattern( Filter a_Filter, String a_SortKey )
    {
        List<GcxUser> result = null ;

        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "FindAll: \n\tSortKey(" + a_SortKey + ")\n\tFilter: " + a_Filter.encode() ) ;
        
        GcxUserMapper mapper = new GcxUserMapper() ;

        // Validate the result size
        this.assertResultSize( "", a_Filter, mapper ) ;

        // Define search controls
        SearchControls searchControls = new SearchControls() ;
        searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE ) ;
        
        // Define processor
        AggregateDirContextProcessor processor = new AggregateDirContextProcessor() ;
        
        // Sort by key
        SortControlDirContextProcessor sorter = new SortControlDirContextProcessor( a_SortKey ) ;
        processor.addDirContextProcessor( sorter ) ;
        
        // TOOD: catch exceptions
        result = this.getLdapTemplate().search( "", a_Filter.encode(), searchControls, mapper, processor ) ;
        
        if ( ( result != null ) && ( result.size() == 0 ) ) {
            result = null ;
        }
        
        return result ;
    }

    /**
     * @param a_GUID
     * @return
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#findByGUID(java.lang.String)
     */
    public GcxUser findByGUID( String a_GUID )
    {
        // Search filter
        AndFilter filter = new AndFilter() ;
        filter.and( new EqualsFilter( "objectclass", Constants.LDAP_OBJECTCLASS_PERSON ) ) 
              .and( new EqualsFilter( Constants.LDAP_KEY_GUID, a_GUID ) )
              ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Search Filter: " + filter.encode() ) ;
        
        // TOOD: catch exceptions
	@SuppressWarnings("unchecked")
	List<GcxUser> list = this.getLdapTemplate().search("", filter.encode(),
		new GcxUserMapper());

	return (list != null && list.size() > 0) ? list.get(0) : null;
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
        
        // Search filter
        AndFilter filter = new AndFilter() ;
        filter.and( new EqualsFilter( "objectclass", Constants.LDAP_OBJECTCLASS_PERSON ) ) 
              .and( new EqualsFilter( Constants.LDAP_KEY_EMAIL, a_Email.toLowerCase() ) )
              ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Search Filter: " + filter.encode() ) ;
        
        // TOOD: catch exceptions
        List list = this.getLdapTemplate().search( "", filter.encode(), new GcxUserMapper() ) ;
        
        if ( ( list != null ) && ( list.size() > 0 ) ) {
            result = (GcxUser)list.get( 0 ) ;
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
        // Search filter
        AndFilter filter = new AndFilter() ;
        filter.and( new LikeFilter( "objectclass", Constants.LDAP_OBJECTCLASS_PERSON ) ) 
              .and( new LikeFilter( Constants.LDAP_KEY_FIRSTNAME, a_FirstNamePattern ) )
              ;

        return this.findAllByPattern( filter, Constants.LDAP_KEY_FIRSTNAME ) ;
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
        // Search filter
        AndFilter filter = new AndFilter() ;
        filter.and( new LikeFilter( "objectclass", Constants.LDAP_OBJECTCLASS_PERSON ) ) 
              .and( new LikeFilter( Constants.LDAP_KEY_LASTNAME, a_LastNamePattern ) )
              ;

        return this.findAllByPattern( filter, Constants.LDAP_KEY_LASTNAME ) ;
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
        // Search filter
        AndFilter filter = new AndFilter() ;
        filter.and( new LikeFilter( "objectclass", Constants.LDAP_OBJECTCLASS_PERSON ) ) 
              .and( new LikeFilter( Constants.LDAP_KEY_EMAIL, a_EmailPattern.toLowerCase() ) )
              ;

        return this.findAllByPattern( filter, Constants.LDAP_KEY_EMAIL ) ;
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
        // Search filter
        AndFilter filter = new AndFilter() ;
        filter.and( new LikeFilter( "objectclass", Constants.LDAP_OBJECTCLASS_PERSON ) ) 
              .and( new LikeFilter( Constants.LDAP_KEY_USERID, a_UseridPattern.toLowerCase() ) )
              ;
        // Drop of deactivated accounts if not needed
        if ( !a_IncludeDeactivated ) {
            filter.and( new NotFilter( new LikeFilter( Constants.LDAP_KEY_EMAIL, Constants.PREFIX_DEACTIVATED + "*" ) ) ) ;
        }

        return this.findAllByPattern( filter, Constants.LDAP_KEY_USERID ) ;
    }


    /**
     * @param a_Key 
     * @return
     * @see org.ccci.gcx.idm.common.persist.QueryDao#get(java.io.Serializable)
     */
    public Object get( Serializable a_Key )
    {
        Assert.isAssignable( String.class, a_Key.getClass(), "Key must be a String" ) ;
        
        return this.findByEmail( (String)a_Key ) ;
    }


    /**
     * Delete the existing {@link GcxUser} entity.
     * 
     * @param a_GcxUser {@link GcxUser} to be deleted.
     * 
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#delete(org.ccci.gcx.idm.core.model.impl.GcxUser)
     */
    public void delete( GcxUser a_GcxUser )
    {
        super.delete( a_GcxUser ) ;
    }
    
    
    /**
     * Save a new {@link GcxUser} entity.
     * 
     * @param a_GcxUser {@link GcxUser} to be persisted.
     */
    public void save( GcxUser a_GcxUser )
    {
        Assert.hasText( a_GcxUser.getEmail(), "E-mail address cannot be blank." ) ;
        Assert.hasText( a_GcxUser.getUserid(), "Userid cannot be blank." ) ;
        
        String email = ( a_GcxUser.getEmail().startsWith( Constants.PREFIX_DEACTIVATED ) ) ? a_GcxUser.getEmail() : a_GcxUser.getEmail().toLowerCase() ;
        String userid = a_GcxUser.getUserid().toLowerCase() ;
        
        a_GcxUser.setEmail( email ) ;
        a_GcxUser.setUserid( userid ) ;
        
        super.save( a_GcxUser ) ;
    }

    
    /**
     * Update the existing {@link GcxUser} entity.
     * 
     * @param a_GcxUser {@link GcxUser} to be updated.
     */
    public void update( GcxUser a_GcxUser )
    {
        Assert.hasText( a_GcxUser.getEmail(), "E-mail address cannot be blank." ) ;
        Assert.hasText( a_GcxUser.getUserid(), "Userid cannot be blank." ) ;
        
        String email = ( a_GcxUser.getEmail().startsWith( Constants.PREFIX_DEACTIVATED ) ) ? a_GcxUser.getEmail() : a_GcxUser.getEmail().toLowerCase() ;
        String userid = a_GcxUser.getUserid().toLowerCase() ;
        
        a_GcxUser.setEmail( email ) ;
        a_GcxUser.setUserid( userid ) ;
        
        super.update( a_GcxUser ) ;
    }
    
    
    /**
     * Save or update the {@link GcxUser} entity.
     * 
     * @param a_GcxUser {@link GcxUser} to be persisted.
     * 
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#saveOrUpdate(org.ccci.gcx.idm.core.model.impl.GcxUser)
     */
    public void saveOrUpdate( GcxUser a_GcxUser ) 
    {
        super.saveOrUpdate( a_GcxUser ) ;
    }

}
