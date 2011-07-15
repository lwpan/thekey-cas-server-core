package org.ccci.gcx.idm.core.persist.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.persist.hibernate.AbstractCrudDao;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.springframework.util.Assert;

/**
 * <b>GcxUserDaoImpl</b> is the concrete implementation of {@link GcxUserDao}.
 *
 * @author Greg Crider  Oct 21, 2008  1:21:50 PM
 */
public class GcxUserDaoImpl extends AbstractCrudDao<GcxUser> implements
	GcxUserDao
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
    public GcxUser findByGUID( String a_GUID )
    {
	List<?> list = this.findByNamedQueryAndNamedParam(
                                Constants.QUERY_GCXUSER_FINDBYGUID,
                                "guid", 
                                a_GUID 
                             ) ;

	// Find the first valid GcxUser to return
	if (list != null) {
	    for (Object user : list) {
		if (user instanceof GcxUser) {
		    return (GcxUser) user;
		}
	    }
        }

	// No valid GcxUser found, return null
	return null;
    }

    
    /**
     * @param a_Email
     * @return
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#findByEmail(java.lang.String)
     */
    public GcxUser findByEmail( String a_Email ) 
    {
	List<?> list = this.findByNamedQueryAndNamedParam(
                                Constants.QUERY_GCXUSER_FINDBYEMAIL,
                                "email", 
                                a_Email.toLowerCase() 
                             ) ;

	// Find the first valid GcxUser to return
	if (list != null) {
	    for (Object user : list) {
		if (user instanceof GcxUser) {
		    return (GcxUser) user;
		}
	    }
	}

	// No valid GcxUser found, return null
	return null;
    }

    public GcxUser findByFacebookId(final String facebookId) {
	throw new UnsupportedOperationException(
		"This method is not currently implemented.");
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
     * @param object
     *            New {@link GcxUser} object to be saved.
     */
    @Override
    public void save(final GcxUser object) {
	this.assertModelObject(object);
	final GcxUser user = (GcxUser) object;

	Assert.hasText(user.getEmail(), "E-mail address cannot be blank.");
	String email = (user.getEmail()
		.startsWith(Constants.PREFIX_DEACTIVATED)) ? user.getEmail()
		: user.getEmail().toLowerCase();

	user.setEmail(email);

	if (log.isTraceEnabled()) {
	    log.trace("********** Saving new user: " + user);
	}

	super.save(user);
    }

    /**
     * @param object
     * @see org.ccci.gcx.idm.core.persist.GcxUserDao#saveOrUpdate(org.ccci.gcx.idm.core.model.impl.GcxUser)
     */
    @Override
    public void saveOrUpdate(final GcxUser object) {
	this.assertModelObject(object);
	final GcxUser user = (GcxUser) object;

	Assert.hasText(user.getEmail(), "E-mail address cannot be blank.");
	String email = (user.getEmail()
		.startsWith(Constants.PREFIX_DEACTIVATED)) ? user.getEmail()
		: user.getEmail().toLowerCase();

	user.setEmail(email);

	super.saveOrUpdate(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gcx.idm.common.persist.hibernate.AbstractDao#getModelClass()
     */
    @Override
    protected Class<? extends GcxUser> getModelClass() {
	return GcxUser.class;
    }
}
