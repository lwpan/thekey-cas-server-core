package org.ccci.gcx.idm.core.persist.ldap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;
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
 * <b>GcxUserDaoImpl</b> is the concrete implementation of the {@link GcxUserDao} with
 * LDAP as the backing store.
 *
 * @author Greg Crider  Oct 21, 2008  2:08:57 PM
 */
public class GcxUserDaoImpl extends AbstractLdapCrudDao implements GcxUserDao
{
    private final GcxUserMapper mapper = new GcxUserMapper();

    private void assertGcxUser(final ModelObject object) {
	Assert.notNull(object, "No ModelObject was provided");
	Assert.isAssignable(GcxUser.class, object.getClass(),
		"Object must be a GcxUser: ");
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
	final int actualLimit = (limit == 0 || (limit > maxLimit && maxLimit != Constants.SEARCH_NO_LIMIT)) ? maxLimit
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
		encodedFilter, controls, this.mapper, processor);

	// Throw an error if there is a maxLimit and the request is for more
	// results than the maxLimit
	if (maxLimit != Constants.SEARCH_NO_LIMIT
		&& (limit == 0 || limit > maxLimit)
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
    private GcxUser findByFilter(Filter filter) {
	List<GcxUser> results = this.findAllByFilter(filter, null, 1);
	return results.size() > 0 ? results.get(0) : null;
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

	return this.findByFilter(filter);
    }

    /**
     * @param a_Email
     * @return
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#findByEmail(java.lang.String)
     */
    public GcxUser findByEmail( String a_Email )
    {
        // Search filter
        AndFilter filter = new AndFilter() ;
        filter.and( new EqualsFilter( "objectclass", Constants.LDAP_OBJECTCLASS_PERSON ) ) 
              .and( new EqualsFilter( Constants.LDAP_KEY_EMAIL, a_Email.toLowerCase() ) )
              ;

	return this.findByFilter(filter);
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

	return this.findAllByFilter(filter, Constants.LDAP_KEY_FIRSTNAME, 0);
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

	return this.findAllByFilter(filter, Constants.LDAP_KEY_LASTNAME, 0);
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

	return this.findAllByFilter(filter, Constants.LDAP_KEY_EMAIL, 0);
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

	return this.findAllByFilter(filter, Constants.LDAP_KEY_USERID, 0);
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

    /**
     * Save a new {@link GcxUser} entity.
     * 
     * @param object
     *            {@link GcxUser} to be persisted.
     */
    @Override
    public void save(final ModelObject object) {
	this.assertModelObject(object);
	final GcxUser user = (GcxUser) object;

	Assert.hasText(user.getEmail(), "E-mail address cannot be blank.");
	Assert.hasText(user.getUserid(), "Userid cannot be blank.");

	String email = (user.getEmail()
		.startsWith(Constants.PREFIX_DEACTIVATED)) ? user.getEmail()
		: user.getEmail().toLowerCase();
	String userid = user.getUserid().toLowerCase();

	user.setEmail(email);
	user.setUserid(userid);

	super.save(user);
    }

    /**
     * Update the existing {@link GcxUser} entity.
     * 
     * @param object
     *            {@link GcxUser} to be updated.
     */
    @Override
    public void update(final ModelObject object) {
	this.assertGcxUser(object);
	GcxUser user = (GcxUser) object;

	Assert.hasText(user.getEmail(), "E-mail address cannot be blank.");
	Assert.hasText(user.getUserid(), "Userid cannot be blank.");

	String email = (user.getEmail()
		.startsWith(Constants.PREFIX_DEACTIVATED)) ? user.getEmail()
		: user.getEmail().toLowerCase();
	String userid = user.getUserid().toLowerCase();

	user.setEmail(email);
	user.setUserid(userid);

	super.update(user);
    }

    @Override
    protected Class<? extends ModelObject> getModelClass() {
	return GcxUser.class;
    }
}
