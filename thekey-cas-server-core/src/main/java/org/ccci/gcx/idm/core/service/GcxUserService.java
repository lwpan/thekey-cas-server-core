package org.ccci.gcx.idm.core.service;

import java.util.List;

import org.ccci.gcx.idm.common.service.DataAccessService;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;

/**
 * <b>GcxUserService</b> defines the available functionality for accessing and
 * updating GCX user data.
 * 
 * @author Greg Crider Oct 21, 2008 1:06:31 PM
 * @author Daniel Frett
 */
public interface GcxUserService extends DataAccessService
{
    /**
     * Return the maximum number of allowed results.
     * 
     * @return Number of maximum allowed search results or 0 (<tt>SEARCH_NO_LIMIT</tt>) if
     *         there is no imposed limit.
     */
    public int getMaxSearchResults() ;
    
    
    /**
     * Determine if the specified user already exists in the transitional backing store.
     * 
     * @param a_GcxUser {@link GcxUser} to be verified.
     */
    @Deprecated
    public boolean doesTransitionalUserExist( GcxUser a_GcxUser ) ;
    
    
    /**
     * Determine if the specified user already exists in the permanent backing store.
     * 
     * @param a_GcxUser {@link GcxUser} to be verified.
     */
    public boolean doesUserExist( GcxUser a_GcxUser ) ;

    /**
     * Save the new {@link GcxUser} object in the GcxUser data store. Use this
     * method if the the user is self-created (by the user himself).
     * 
     * @param user
     *            {@link GcxUser} object to be saved.
     * @param source
     *            Identifier of application or entity used to create user.
     */
    public void createUser(final GcxUser user, final String source);

    /**
     * Activate the transitional user by creating a new, permananent user account, and
     * removing the existing transitional one.
     * 
     * @param a_GcxUser {@link GcxUser} transitional user (just the primary identifier of
     *        e-mail address or GUID).
     * @param a_Source Source identifier of applicaton or entity used to create user.
     * @param a_CreatedBy Userid or identifier of who is creating user (if not created by the
     *        user himself).
     */
    @Deprecated
    public void activateTransitionalUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy ) ;

    /**
     * Update the specified {@link GcxUser}.
     * 
     * @param a_GcxUser {@link GcxUser} to be updated.
     * @param a_HasPasswordChange If <tt>true</tt> then the password has been changed.
     * @param a_Source Source identifier of applicaton or entity used to update user.
     * @param a_CreatedBy Userid or identifier of who is updating user (if not updated by the
     *        user himself).
     */
    public void updateUser( GcxUser a_GcxUser, boolean a_HasPasswordChange, String a_Source, String a_CreatedBy ) ;
    
    
    /**
     * Deactivate the user by disabling the account and changing the e-mail address.
     * 
     * @param a_GcxUser {@link GcxUser} to deactivate
     * @param a_Source Source identifier of applicaton or entity used to deactivate user.
     * @param a_CreatedBy Userid or identifier of who is deactivating user (if not deactivated by the
     *        user himself).
     */
    public void deactivateUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy ) ;
    
    
    /**
     * Reactivate a previously deactivated user.
     * 
     * @param a_GcxUser {@link GcxUser} to reactivate
     * @param a_Source Source identifier of applicaton or entity used to reactivate user.
     * @param a_CreatedBy Userid or identifier of who is reactivating user (if not reactivated by the
     *        user himself).
     */
    public void reactivateUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy ) ;
    
    
    /**
     * Reset the user's password and send the newly created password to his e-mail address.
     * 
     * @param a_GcxUser {@link GcxUser} to reactivate
     * @param a_Source Source identifier of applicaton or entity used to reactivate user.
     * @param a_CreatedBy Userid or identifier of who is reactivating user (if not reactivated by the
     *        user himself).
     */
    public void resetPassword( GcxUser a_GcxUser, String a_Source, String a_CreatedBy ) ;
    
    
    /**
     * Merge the two users. Key values from the user to be merged are copied over into the primary
     * user. The user to be merged is then deactivated (if it isn't already).
     * 
     * @param a_PrimaryUser {@link GcxUser} that is the primary user.
     * @param a_UserBeingMerged {@link GcxUser} that is being merged into the primary user.
     * @param a_Source Source identifier of applicaton or entity used to reactivate user.
     * @param a_CreatedBy Userid or identifier of who is reactivating user (if not reactivated by the
     *        user himself).
     */
    public void mergeUsers( GcxUser a_PrimaryUser, GcxUser a_UserBeingMerged, String a_Source, String a_CreatedBy ) ;
    
    
    /** 
     * Locate the transitional user with the specified e-mail address.
     * 
     * @param a_Email E-mail address of user to find.
     * 
     * @return {@link GcxUser} with the specified e-mail address, or <tt>null</tt> if not found.
     */
    @Deprecated
    public GcxUser findTransitionalUserByEmail( String a_Email ) ;
    
    
    /** 
     * Locate the user (not transitional) with the specified e-mail address.
     * 
     * @param a_Email E-mail address of user to find.
     * 
     * @return {@link GcxUser} with the specified e-mail address, or <tt>null</tt> if not found.
     */
    public GcxUser findUserByEmail( String a_Email ) ;
    
    /** 
     * Locate the user (not transitional) with the specified guid.
     * 
     * @param a_Guid GUID of user to find.
     * 
     * @return {@link GcxUser} with the specified guid, or <tt>null</tt> if not found.
     */
    public GcxUser findUserByGuid( String a_Guid );

    /**
     * Locate the user with the specified facebook id.
     * 
     * @param facebookId
     *            the facebook id being search for
     * @return {@link GcxUser} with the specified facebook id, or <tt>null</tt>
     *         if not found.
     */
    public GcxUser findUserByFacebookId(final String facebookId);

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
     * @param user
     *            the {@link GcxUser} to retrieve a fresh instance of
     * @return a fresh copy of the {@link GcxUser} object
     * @throws GcxUserNotFoundException
     */
    public GcxUser getFreshUser(final GcxUser user)
	    throws GcxUserNotFoundException;
}
