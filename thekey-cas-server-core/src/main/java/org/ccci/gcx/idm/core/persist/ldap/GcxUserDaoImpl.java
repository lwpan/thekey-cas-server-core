package org.ccci.gcx.idm.core.persist.ldap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;
import org.ccci.gto.cas.Constants;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.ccci.gto.cas.persist.ldap.GcxUserMapper;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.control.SortControlDirContextProcessor;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.util.Assert;

/**
 * <b>GcxUserDaoImpl</b> is the concrete implementation of the
 * {@link GcxUserDao} with LDAP as the backing store.
 * 
 * @author Daniel Frett
 */
public class GcxUserDaoImpl extends AbstractLdapCrudDao implements GcxUserDao {
    private static final GcxUserMapper MAPPER = new GcxUserMapper();

    // LDAP settings
    private static final int NOSEARCHLIMIT = Constants.LDAP_NOSEARCHLIMIT;

    // LDAP Attributes in use
    private static final String ATTR_OBJECTCLASS = Constants.LDAP_ATTR_OBJECTCLASS;
    private static final String ATTR_EMAIL = Constants.LDAP_ATTR_EMAIL;
    private static final String ATTR_GUID = Constants.LDAP_ATTR_GUID;
    private static final String ATTR_FIRSTNAME = Constants.LDAP_ATTR_FIRSTNAME;
    private static final String ATTR_LASTNAME = Constants.LDAP_ATTR_LASTNAME;
    private static final String ATTR_USERID = Constants.LDAP_ATTR_USERID;

    // LDAP objectClass values
    private static final String OBJECTCLASS_PERSON = Constants.LDAP_OBJECTCLASS_PERSON;

    /*
     * assert that this is a valid GcxUser ModelObject
     * 
     * @see org.ccci.gto.persist.AbstractDao#assertModelObject(ModelObject)
     */
    @Override
    protected void assertModelObject(ModelObject object) {
	super.assertModelObject(object);
	final GcxUser user = (GcxUser) object;
	Assert.hasText(user.getEmail(), "E-mail address cannot be blank.");
	Assert.hasText(user.getUserid(), "Userid cannot be blank.");
    }

    @Override
    protected Class<? extends ModelObject> getModelClass() {
	return GcxUser.class;
    }

    /**
     * Find all users matching the pattern specified in the filter.
     * 
     * @param filter
     *            Filter for the LDAP search.
     * @param sortKey
     *            Attribute key for sorting the results.
     * @param limit
     *            limit the number of returned results to this amount
     * @return {@link List} of {@link GcxUser} objects.
     */
    private List<GcxUser> findAllByFilter(final Filter filter,
	    final String sortKey, final int limit) {
	final String encodedFilter = filter.encode();
	final int maxLimit = this.getMaxSearchResults();

	// set the actual limit based on the maxLimit
	final int actualLimit = (limit == 0 || (limit > maxLimit && maxLimit != NOSEARCHLIMIT)) ? maxLimit
		: limit;

	/* = DEBUG = */if (log.isDebugEnabled()) {
	    log.debug("Find: SortKey: " + sortKey + " Limit: " + limit
		    + " Filter: " + encodedFilter);
	}

	// Initialize various search filters
	SearchControls controls = new SearchControls();
	controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	AggregateDirContextProcessor processor = new AggregateDirContextProcessor();
	if (sortKey != null) {
	    processor
		    .addDirContextProcessor(new SortControlDirContextProcessor(
			    sortKey));
	}

	// Limit number of returned results when necessary
	PagedResultsDirContextProcessor pager = null;
	if (actualLimit != 0) {
	    pager = new PagedResultsDirContextProcessor(limit);
	    processor.addDirContextProcessor(pager);
	}

	// Execute LDAP query
	final List<?> rawResults = this.getLdapTemplate().search("",
		encodedFilter, controls, MAPPER, processor);

	// Throw an error if there is a maxLimit and the request is for more
	// results than the maxLimit
	if (maxLimit != NOSEARCHLIMIT && (limit == 0 || limit > maxLimit)
		&& pager.getResultSize() > maxLimit) {
	    final String error = "Search exceeds max allowed results of "
		    + maxLimit + ": SortKey: " + sortKey + " Limit: " + limit
		    + " Filter: " + encodedFilter + " Found Results: "
		    + pager.getResultSize();
	    /* = ERROR = */log.error(error);
	    throw new ExceededMaximumAllowedResults(error);
	}

	// Filter results to make sure only GcxUser objects are returned
	final ArrayList<GcxUser> results = new ArrayList<GcxUser>();
	for (Object user : rawResults) {
	    if (user instanceof GcxUser) {
		results.add((GcxUser) user);
	    }
	}

	// return filtered users
	return results;
    }

    /**
     * searches for the first GcxUser that matches the specified filter
     * 
     * @param filter
     * @return
     */
    private GcxUser findByFilter(final Filter filter) {
	final List<GcxUser> results = this.findAllByFilter(filter, null, 1);
	return results.size() > 0 ? results.get(0) : null;
    }

    /**
     * @param guid
     * @return
     * @see GcxUserDao#findByGUID(String)
     */
    public GcxUser findByGUID(final String guid) {
	// Build search filter
	final AndFilter filter = new AndFilter();
	filter.and(new EqualsFilter(ATTR_OBJECTCLASS, OBJECTCLASS_PERSON));
	filter.and(new EqualsFilter(ATTR_GUID, guid));

	// Execute search
	return this.findByFilter(filter);
    }

    /**
     * @param email
     * @return
     * @see GcxUserDao#findByEmail(String)
     */
    public GcxUser findByEmail(final String email) {
	// Build search filter
	final AndFilter filter = new AndFilter();
	filter.and(new EqualsFilter(ATTR_OBJECTCLASS, OBJECTCLASS_PERSON));
	filter.and(new EqualsFilter(ATTR_EMAIL, email));

	// Execute search
	return this.findByFilter(filter);
    }

    /**
     * Find all users matching the first name pattern.
     * 
     * @param pattern
     *            Pattern used for matching first name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     */
    public List<GcxUser> findAllByFirstName(final String pattern) {
	// Build search filter
	final AndFilter filter = new AndFilter();
	filter.and(new EqualsFilter(ATTR_OBJECTCLASS, OBJECTCLASS_PERSON));
	filter.and(new LikeFilter(ATTR_FIRSTNAME, pattern));

	// Execute search
	return this.findAllByFilter(filter, ATTR_FIRSTNAME, 0);
    }

    /**
     * Find all users matching the last name pattern.
     * 
     * @param pattern
     *            Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     */
    public List<GcxUser> findAllByLastName(final String pattern) {
	// Build search filter
	final AndFilter filter = new AndFilter();
	filter.and(new EqualsFilter(ATTR_OBJECTCLASS, OBJECTCLASS_PERSON));
	filter.and(new LikeFilter(ATTR_LASTNAME, pattern));

	// Execute search
	return this.findAllByFilter(filter, ATTR_LASTNAME, 0);
    }

    /**
     * Find all users matching the e-mail pattern.
     * 
     * @param pattern
     *            Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     */
    public List<GcxUser> findAllByEmail(final String pattern) {
	// Build search filter
	final AndFilter filter = new AndFilter();
	filter.and(new EqualsFilter(ATTR_OBJECTCLASS, OBJECTCLASS_PERSON));
	filter.and(new LikeFilter(ATTR_EMAIL, pattern));

	// Execute search
	return this.findAllByFilter(filter, ATTR_EMAIL, 0);
    }

    /**
     * Find all users matching the userid pattern.
     * 
     * @param pattern
     *            Pattern used for matching userids.
     * @param includeDeactivated
     *            If <tt>true</tt> then deactivated accounts are included.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     */
    public List<GcxUser> findAllByUserid(final String pattern,
	    final boolean includeDeactivated) {
	// Build search filter
	final AndFilter filter = new AndFilter();
	filter.and(new EqualsFilter(ATTR_OBJECTCLASS, OBJECTCLASS_PERSON));
	filter.and(new LikeFilter(ATTR_USERID, pattern));

	// Don't include deactivated accounts unless requested
	if (!includeDeactivated) {
	    filter.and(new NotFilter(new LikeFilter(ATTR_EMAIL,
		    Constants.ACCOUNT_DEACTIVATEDPREFIX + "*")));
	}

	// Execute search
	return this.findAllByFilter(filter, ATTR_USERID, 0);
    }

    /**
     * @param key
     * @return
     * @see org.ccci.gcx.idm.common.persist.QueryDao#get(Serializable)
     */
    @Override
    public ModelObject get(final Serializable key) {
	Assert.isAssignable(String.class, key.getClass(),
		"Key must be a String");
	return this.findByEmail((String) key);
    }
}
