package me.thekey.cas.service;

import java.util.List;

import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;

/**
 * <b>UserService</b> defines the available functionality for accessing and
 * updating Key user data.
 * 
 * @author Greg Crider Oct 21, 2008 1:06:31 PM
 * @author Daniel Frett
 */
public interface UserManager {
    /**
     * Return the maximum number of allowed results.
     * 
     * @return Number of maximum allowed search results or 0 (<tt>SEARCH_NO_LIMIT</tt>) if
     *         there is no imposed limit.
     */
    public int getMaxSearchResults() ;
    
    boolean doesGuidExist(GcxUser user);

    boolean doesEmailExist(GcxUser user);

    /**
     * Create a new {@link GcxUser} object in the GcxUser data store.
     * 
     * @param user
     *            {@link GcxUser} object to be saved.
     * @throws GcxUserAlreadyExistsException
     */
    void createUser(GcxUser user) throws GcxUserAlreadyExistsException;

    /**
     * Update the specified {@link GcxUser}.
     * 
     * @param user
     *            {@link GcxUser} to be updated.
     * @throws GcxUserNotFoundException
     *             The specified user cannot be found to be updated
     */
    void updateUser(final GcxUser user) throws GcxUserNotFoundException;

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
     * @param a_GcxUser
     *            {@link GcxUser} to reactivate
     * @param a_Source
     *            Source identifier of applicaton or entity used to reactivate
     *            user.
     * @param a_CreatedBy
     *            Userid or identifier of who is reactivating user (if not
     *            reactivated by the user himself).
     * @throws GcxUserAlreadyExistsException
     *             thrown if the user being reactivated already exists
     */
    public void reactivateUser(GcxUser a_GcxUser, String a_Source,
	    String a_CreatedBy) throws GcxUserAlreadyExistsException;

    /**
     * Reset the user's password and send the newly created password to his
     * e-mail address.
     * 
     * @param user
     *            {@link GcxUser} to reactivate
     * @param source
     *            Source identifier of applicaton or entity used to reactivate
     *            user.
     * @param createdBy
     *            Userid or identifier of who is reactivating user (if not
     *            reactivated by the user himself).
     */
    void resetPassword(GcxUser user, String source, String createdBy, String uriParams);

    /**
     * Merge the two users. Key values from the user to be merged are copied
     * over into the primary user. The user to be merged is then deactivated (if
     * it isn't already).
     * 
     * @param a_PrimaryUser
     *            {@link GcxUser} that is the primary user.
     * @param a_UserBeingMerged
     *            {@link GcxUser} that is being merged into the primary user.
     * @param a_Source
     *            Source identifier of applicaton or entity used to reactivate
     *            user.
     * @param a_CreatedBy
     *            Userid or identifier of who is reactivating user (if not
     *            reactivated by the user himself).
     * @throws GcxUserNotFoundException
     */
    public void mergeUsers(final GcxUser a_PrimaryUser,
	    final GcxUser a_UserBeingMerged, final String a_Source,
	    final String a_CreatedBy) throws GcxUserNotFoundException;

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
     * Locate the user with the specified Relay guid.
     * 
     * @param guid
     *            the Relay guid being search for
     * @return {@link GcxUser} with the specified Relay guid, or <tt>null</tt>
     *         if not found.
     */
    public GcxUser findUserByRelayGuid(final String guid);

    /**
     * Find all users matching the first name pattern.
     * 
     * @param pattern
     *            Pattern used for matching first name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     * @throws ExceededMaximumAllowedResults
     */
    public List<GcxUser> findAllByFirstName(final String pattern)
	    throws ExceededMaximumAllowedResults;

    /**
     * Find all users matching the last name pattern.
     * 
     * @param pattern
     *            Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     * @throws ExceededMaximumAllowedResults
     */
    public List<GcxUser> findAllByLastName(final String pattern)
	    throws ExceededMaximumAllowedResults;

    /**
     * Find all users matching the e-mail pattern.
     * 
     * @param pattern
     *            Pattern used for matching e-mail.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     * @throws ExceededMaximumAllowedResults
     */
    public List<GcxUser> findAllByEmail(final String pattern)
	    throws ExceededMaximumAllowedResults;

    /**
     * Find all users matching the userid pattern.
     * 
     * @param pattern
     *            Pattern used for matching userid.
     * @param includeDeactivated
     *            If <tt>true</tt> then deactivated accounts are included.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     * @throws ExceededMaximumAllowedResults
     */
    public List<GcxUser> findAllByUserid(final String pattern,
	    final boolean includeDeactivated)
	    throws ExceededMaximumAllowedResults;

    /**
     * @param user
     *            the {@link GcxUser} to retrieve a fresh instance of
     * @return a fresh copy of the {@link GcxUser} object
     * @throws GcxUserNotFoundException
     *             if the user can't be found
     */
    public GcxUser getFreshUser(final GcxUser user)
	    throws GcxUserNotFoundException;
}
