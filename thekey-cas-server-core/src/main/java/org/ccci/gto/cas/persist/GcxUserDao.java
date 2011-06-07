package org.ccci.gto.cas.persist;

import java.util.List;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.persist.CrudDao;

/**
 * <b>GcxUserDao</b> defines the {@link CrudDao} functionality for persisting
 * and retrieving {@link GcxUser} entities.
 * 
 * @author Daniel Frett
 */
public interface GcxUserDao extends CrudDao {
    /**
     * Return the maximum number of allowed results.
     * 
     * @return Number of maximum allowed search results or 0 (
     *         <tt>SEARCH_NO_LIMIT</tt>) if there is no imposed limit.
     */
    public int getMaxSearchResults();

    /**
     * Find the user with the specified e-mail.
     * 
     * @param email
     *            Email for lookup.
     * 
     * @return Requested {@link GcxUser} or <tt>null</tt> if not found.
     */
    public GcxUser findByEmail(final String email);

    /**
     * Find the user with the specified GUID.
     * 
     * @param guid
     *            GUID for lookup.
     * 
     * @return Request {@link GcxUser} or <tt>null</tt> if not found.
     */
    public GcxUser findByGUID(final String guid);

    /**
     * Find the user with the specified Facebook Id
     * 
     * @param facebookId
     *            the facebook id to search for
     * @return Requested {@link GcxUser} or <tt>null</tt> if not found.
     */
    public GcxUser findByFacebookId(final String facebookId);

    /**
     * Find all users matching the first name pattern.
     * 
     * @param pattern
     *            Pattern used for matching first name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     */
    public List<GcxUser> findAllByFirstName(final String pattern);

    /**
     * Find all users matching the last name pattern.
     * 
     * @param pattern
     *            Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     */
    public List<GcxUser> findAllByLastName(final String pattern);

    /**
     * Find all users matching the e-mail pattern.
     * 
     * @param pattern
     *            Pattern used for matching e-mail.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     */
    public List<GcxUser> findAllByEmail(final String pattern);

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
     */
    public List<GcxUser> findAllByUserid(final String pattern,
	    final boolean includeDeactivated);
}
