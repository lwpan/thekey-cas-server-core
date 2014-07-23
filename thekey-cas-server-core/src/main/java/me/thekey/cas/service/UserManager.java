package me.thekey.cas.service;

import com.google.common.collect.ListMultimap;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;

import java.util.List;

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
     * @return Number of maximum allowed search results or 0 (<tt>SEARCH_NO_LIMIT</tt>) if there is no imposed limit.
     */
    public int getMaxSearchResults();

    boolean doesEmailExist(String email);

    /**
     * Create a new {@link GcxUser} object in the GcxUser data store.
     *
     * @param user {@link GcxUser} object to be saved.
     * @throws UserAlreadyExistsException
     */
    void createUser(GcxUser user) throws UserAlreadyExistsException;

    /**
     * Update the specified {@link GcxUser}.
     *
     * @param user {@link GcxUser} to be updated.
     * @throws UserNotFoundException The specified user cannot be found to be updated
     */
    void updateUser(final GcxUser user) throws UserNotFoundException;

    /**
     * Deactivate the user by disabling the account and changing the e-mail address.
     *
     * @param user      {@link GcxUser} to deactivate
     * @param source    Source identifier of applicaton or entity used to deactivate user.
     * @param createdBy Userid or identifier of who is deactivating user (if not deactivated by the user himself).
     */
    void deactivateUser(GcxUser user, String source, String createdBy);


    /**
     * Reactivate a previously deactivated user.
     *
     * @param user      {@link GcxUser} to reactivate
     * @param source    Source identifier of applicaton or entity used to reactivate user.
     * @param createdBy Userid or identifier of who is reactivating user (if not reactivated by the user himself).
     * @throws UserAlreadyExistsException thrown if the user being reactivated already exists
     */
    void reactivateUser(GcxUser user, String source, String createdBy) throws UserAlreadyExistsException;

    /**
     * Merge the two users. Key values from the user to be merged are copied
     * over into the primary user. The user to be merged is then deactivated (if
     * it isn't already).
     *
     * @param a_PrimaryUser     {@link GcxUser} that is the primary user.
     * @param a_UserBeingMerged {@link GcxUser} that is being merged into the primary user.
     * @param a_Source          Source identifier of applicaton or entity used to reactivate
     *                          user.
     * @param a_CreatedBy       Userid or identifier of who is reactivating user (if not
     *                          reactivated by the user himself).
     * @throws UserNotFoundException
     */
    public void mergeUsers(final GcxUser a_PrimaryUser, final GcxUser a_UserBeingMerged, final String a_Source,
                           final String a_CreatedBy) throws UserNotFoundException;

    /**
     * Locate the user (not transitional) with the specified e-mail address.
     *
     * @param a_Email E-mail address of user to find.
     * @return {@link GcxUser} with the specified e-mail address, or <tt>null</tt> if not found.
     */
    public GcxUser findUserByEmail(String a_Email);

    /**
     * Locate the user (not transitional) with the specified guid.
     *
     * @param a_Guid GUID of user to find.
     * @return {@link GcxUser} with the specified guid, or <tt>null</tt> if not found.
     */
    public GcxUser findUserByGuid(String a_Guid);

    /**
     * Locate the user with the specified facebook id.
     *
     * @param facebookId the facebook id being search for
     * @return {@link GcxUser} with the specified facebook id, or <tt>null</tt> if not found.
     */
    public GcxUser findUserByFacebookId(final String facebookId);

    /**
     * Find all users matching the first name pattern.
     *
     * @param pattern Pattern used for matching first name.
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     * @throws ExceededMaximumAllowedResults
     */
    public List<GcxUser> findAllByFirstName(final String pattern) throws ExceededMaximumAllowedResults;

    /**
     * Find all users matching the last name pattern.
     *
     * @param pattern Pattern used for matching last name.
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     * @throws ExceededMaximumAllowedResults
     */
    public List<GcxUser> findAllByLastName(final String pattern) throws ExceededMaximumAllowedResults;

    /**
     * Find all users matching the userid pattern.
     *
     * @param pattern            Pattern used for matching userid.
     * @param includeDeactivated If <tt>true</tt> then deactivated accounts are included.
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     * @throws ExceededMaximumAllowedResults
     */
    public List<GcxUser> findAllByUserid(final String pattern, final boolean includeDeactivated) throws
            ExceededMaximumAllowedResults;

    /**
     * @param user the {@link GcxUser} to retrieve a fresh instance of
     * @return a fresh copy of the {@link GcxUser} object
     * @throws UserNotFoundException if the user can't be found
     */
    public GcxUser getFreshUser(final GcxUser user) throws UserNotFoundException;

    /**
     * return all the attributes for the specified user
     *
     * @param user Who we want the attributes for
     * @return All the attributes for the provided user
     */
    ListMultimap<String, String> getUserAttributes(GcxUser user);
}
