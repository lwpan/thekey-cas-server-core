package org.ccci.gcx.idm.core.persist;

import java.util.List;

import org.ccci.gcx.idm.common.persist.CrudDao;
import org.ccci.gcx.idm.core.model.impl.GcxUser;

/**
 * <b>GcxUserDao</b> defines the {@link CrudDao} functionality for persisting
 * and retrieving {@link GcxUser} entities.
 *
 * @author Greg Crider  Oct 21, 2008  1:16:23 PM
 */
public interface GcxUserDao extends CrudDao
{
    /**
     * Return the maximum number of allowed results.
     * 
     * @return Number of maximum allowed search results or 0 (<tt>SEARCH_NO_LIMIT</tt>) if
     *         there is no imposed limit.
     */
    public int getMaxSearchResults() ;
    

    /**
     * Find the user with the specified e-mail.
     * 
     * @param a_Email Email for lookup.
     * 
     * @return Requested {@link GcxUser} or <tt>null</tt> if not found.
     */
    public GcxUser findByEmail( String a_Email ) ;
    
    
    /**
     * Find the user with the specified GUID.
     * 
     * @param a_GUID GUID for lookup.
     * 
     * @return Request {@link GcxUser} or <tt>null</tt> if not found.
     */
    public GcxUser findByGUID( String a_GUID ) ;
    
    
    /**
     * Find all users matching the first name pattern.
     * 
     * @param a_FirstNamePattern Pattern used for matching first name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByFirstName( String a_FirstNamePattern ) ;
    
    
    /**
     * Find all users matching the last name pattern.
     * 
     * @param a_LastNamePattern Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByLastName( String a_LastNamePattern ) ;
    
    
    /**
     * Find all users matching the e-mail pattern.
     * 
     * @param a_EmailPattern Pattern used for matching e-mail.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByEmail( String a_EmailPattern ) ;
    
    
    /**
     * Find all users matching the userid pattern.
     * 
     * @param a_UseridPattern Pattern used for matching userid.
     * @param a_IncludeDeactivated If <tt>true</tt> then deactivated accounts are included.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     */
    public List<GcxUser> findAllByUserid( String a_UseridPattern, boolean a_IncludeDeactivated ) ;
    
    
    /**
     * Save a new {@link GcxUser} entity, or update it if it already exists.
     * 
     * @param a_GcxUser {@link GcxUser} to be persisted.
     */
    public void saveOrUpdate( GcxUser a_GcxUser ) ;
    
    
    /**
     * Save a new {@link GcxUser} entity.
     * 
     * @param a_Gcxuser {@link GcxUser to be persisted.
     */
    public void save( GcxUser a_Gcxuser ) ;
    
    
    /**
     * Delete the {@link GcxUser}.
     * 
     * @param a_GcxUser {@link GcxUser} to be deleted.
     */
    public void delete( GcxUser a_GcxUser ) ;
}
