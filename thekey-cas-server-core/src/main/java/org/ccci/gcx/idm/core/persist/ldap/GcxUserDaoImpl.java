package org.ccci.gcx.idm.core.persist.ldap;

import static org.ccci.gto.cas.Constants.ACCOUNT_DEACTIVATEDPREFIX;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_GUID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_LASTNAME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_OBJECTCLASS;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_RELAYGUID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_USERID;
import static org.ccci.gto.cas.Constants.LDAP_NOSEARCHLIMIT;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_PERSON;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.ccci.gto.cas.persist.ldap.GcxUserMapper;
import org.jasig.cas.util.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AggregateDirContextProcessor;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.util.Assert;

import javax.naming.directory.SearchControls;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>GcxUserDaoImpl</b> is the concrete implementation of the
 * {@link GcxUserDao} with LDAP as the backing store.
 *
 * @author Daniel Frett
 */
public class GcxUserDaoImpl extends AbstractLdapCrudDao<GcxUser> implements GcxUserDao {
    private static final Logger LOG = LoggerFactory.getLogger(GcxUserDaoImpl.class);

    private static final GcxUserMapper MAPPER = new GcxUserMapper();

    /*
     * assert that this is a valid GcxUser object
     * 
     * @see org.ccci.gto.persist.AbstractDao#assertValidObject(ModelObject)
     */
    @Override
    protected void assertValidObject(final GcxUser user) {
        super.assertValidObject(user);
        Assert.hasText(user.getEmail(), "E-mail address cannot be blank.");
        Assert.hasText(user.getUserid(), "Userid cannot be blank.");
    }

    /**
     * Find all users matching the pattern specified in the filter.
     *
     * @param filter Filter for the LDAP search.
     * @param limit  limit the number of returned results to this amount
     * @return {@link List} of {@link GcxUser} objects.
     * @throws ExceededMaximumAllowedResults
     */
    private List<GcxUser> findAllByFilter(final Filter filter, final int limit) throws ExceededMaximumAllowedResults {
        final String encodedFilter = filter.encode();
        final int maxLimit = this.getMaxSearchResults();

        // set the actual limit based on the maxLimit
        final int actualLimit = (limit == 0 || (limit > maxLimit && maxLimit != LDAP_NOSEARCHLIMIT)) ? maxLimit : limit;

        if (log.isDebugEnabled()) {
            log.debug("Find: Limit: " + limit + " Actual Limit: " + actualLimit + " Filter: " + encodedFilter);
        }

        // Initialize various search filters
        final SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        final AggregateDirContextProcessor processor = new AggregateDirContextProcessor();

        // Limit number of returned results when necessary
        PagedResultsDirContextProcessor pager = null;
        if (actualLimit != 0) {
            pager = new PagedResultsDirContextProcessor(actualLimit);
            processor.addDirContextProcessor(pager);
        }

        // Execute LDAP query
        final List<?> rawResults = this.getLdapTemplate().search("", encodedFilter, controls, MAPPER, processor);

        if (log.isDebugEnabled() && pager != null) {
            log.debug("Found Results: " + pager.getResultSize());
        }

        // Throw an error if there is a maxLimit and the request is for more
        // results than the maxLimit
        if (maxLimit != LDAP_NOSEARCHLIMIT && (limit == 0 || limit > maxLimit) && pager.getResultSize() > maxLimit) {
            final String error = "Search exceeds max allowed results of " + maxLimit + ": Limit: " + limit + " " +
                    "Filter: " + encodedFilter + " Found Results: " + pager.getResultSize();
            log.error(error);
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
        try {
            final List<GcxUser> results = this.findAllByFilter(filter, 1);
            return results.size() > 0 ? results.get(0) : null;
        } catch (final ExceededMaximumAllowedResults e) {
            // this should be unreachable, but if we do reach it, log the exception and re-throw it as a runtime
            // exception
            LOG.error("ExceededMaximumAllowedResults thrown for findByFilter, this should be impossible!!!!", e);
            throw new RuntimeException(e);
        }
    }

    public GcxUser findByGUID(final String guid) {
        // Build search filter
        final AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_PERSON));
        filter.and(new EqualsFilter(LDAP_ATTR_GUID, guid.toUpperCase()));

        // Execute search
        return this.findByFilter(filter);
    }

    public GcxUser findByEmail(final String email) {
        // Build search filter
        final AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_PERSON));
        filter.and(new EqualsFilter(LDAP_ATTR_EMAIL, email));

        // Execute search
        return this.findByFilter(filter);
    }

    @Override
    public GcxUser findByFacebookId(final String facebookId) {
        // Build search filter
        final AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_PERSON));
        filter.and(new EqualsFilter(LDAP_ATTR_FACEBOOKID, facebookId));

        // Execute search
        return this.findByFilter(filter);
    }

    @Override
    public GcxUser findByRelayGuid(final String guid) {
        // Build search filter
        final AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_PERSON));
        filter.and(new EqualsFilter(LDAP_ATTR_RELAYGUID, guid.toUpperCase()));

        // Execute search
        return this.findByFilter(filter);
    }

    /**
     * Find all users matching the first name pattern.
     *
     * @param pattern Pattern used for matching first name.
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     * @throws ExceededMaximumAllowedResults
     */
    @Override
    public List<GcxUser> findAllByFirstName(final String pattern) throws ExceededMaximumAllowedResults {
        // Build search filter
        final AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_PERSON));
        filter.and(new LikeFilter(LDAP_ATTR_FIRSTNAME, pattern));

        // Execute search
        return this.findAllByFilter(filter, 0);
    }

    /**
     * Find all users matching the last name pattern.
     *
     * @param pattern Pattern used for matching last name.
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     * @throws ExceededMaximumAllowedResults
     */
    @Override
    public List<GcxUser> findAllByLastName(final String pattern) throws ExceededMaximumAllowedResults {
        // Build search filter
        final AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_PERSON));
        filter.and(new LikeFilter(LDAP_ATTR_LASTNAME, pattern));

        // Execute search
        return this.findAllByFilter(filter, 0);
    }

    /**
     * Find all users matching the userid pattern.
     *
     * @param pattern            Pattern used for matching userids.
     * @param includeDeactivated If <tt>true</tt> then deactivated accounts are included.
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none are found.
     * @throws ExceededMaximumAllowedResults
     */
    @Override
    public List<GcxUser> findAllByUserid(final String pattern, final boolean includeDeactivated) throws
            ExceededMaximumAllowedResults {
        // Build search filter
        final AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_PERSON));
        filter.and(new LikeFilter(LDAP_ATTR_USERID, pattern));

        // Don't include deactivated accounts unless requested
        if (!includeDeactivated) {
            filter.and(new NotFilter(new LikeFilter(LDAP_ATTR_EMAIL, ACCOUNT_DEACTIVATEDPREFIX + "*")));
        }

        // Execute search
        return this.findAllByFilter(filter, 0);
    }

    @Override
    public GcxUser get(final Serializable key) {
        Assert.isAssignable(String.class, key.getClass(), "Key must be a String");
        return this.findByEmail((String) key);
    }

    @Override
    public void update(final GcxUser original, final GcxUser user) {
        this.assertValidObject(original);
        this.assertValidObject(user);

        // rename the user before updating if the email address has changed
        if (!user.getEmail().equals(original.getEmail())) {
            final LdapTemplate template = this.getLdapTemplate();
            template.rename(this.generateModelDN(original), this.generateModelDN(user));
        }

        this.update(user);
    }

    @Override
    protected DistinguishedName generateModelDN(final GcxUser user) {
        return new DistinguishedName(LdapUtils.getFilterWithValues(this.getModelDN(), user.getEmail()));
    }
}
