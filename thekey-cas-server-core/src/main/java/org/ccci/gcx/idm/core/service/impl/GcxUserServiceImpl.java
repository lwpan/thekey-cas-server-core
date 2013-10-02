package org.ccci.gcx.idm.core.service.impl;

import static org.ccci.gto.cas.Constants.ACCOUNT_DEACTIVATEDPREFIX;

import java.util.ArrayList;
import java.util.List;

import me.thekey.cas.service.UserManager;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;
import org.ccci.gto.cas.service.audit.AuditException;
import org.ccci.gto.cas.util.RandomGUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.github.inspektr.audit.annotation.Audit;

/**
 * <b>GcxUserServiceImpl</b> is the concrete implementation of {@link UserManager}.
 *
 * @author Greg Crider  Oct 21, 2008  1:35:01 PM
 */
public class GcxUserServiceImpl extends AbstractGcxUserService {
    private static final Logger LOG = LoggerFactory.getLogger(GcxUserServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public boolean doesGuidExist(final String guid) {
        if (guid != null && this.getUserDao().findByGUID(guid) != null) {
            LOG.debug("***** GUID \"{}\" already exists", guid);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesEmailExist(final String email) {
        if (email != null && this.getUserDao().findByEmail(email) != null) {
            LOG.debug("***** Email \"{}\" already exists", email);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = false)
    @Audit(applicationCode = "THEKEY", action = "CREATE_USER", actionResolverName = "THEKEY_USER_SERVICE_ACTION_RESOLVER", resourceResolverName = "THEKEY_USER_SERVICE_CREATE_USER_RESOURCE_RESOLVER")
    public void createUser(final GcxUser user) throws GcxUserAlreadyExistsException {
        // throw an error if we don't have a valid email
        if (StringUtils.isBlank(user.getEmail())) {
            // TODO: should this be a more specific exception
            throw new RuntimeException("Invalid email specified for creating a user");
        }

        // throw an error if a user already exists for this email
        if (this.doesEmailExist(user.getEmail())) {
            LOG.debug("The specified user '{}' already exists.", user.getEmail());
            throw new GcxUserAlreadyExistsException();
        }

        // generate a guid for the user if there isn't a valid one already set
        int count = 0;
        while (StringUtils.isBlank(user.getGUID()) || this.doesGuidExist(user.getGUID())) {
            user.setGUID(RandomGUID.generateGuid(true));

            // prevent an infinite loop, I doubt this exception will ever be
            // thrown
            if (count++ > 200) {
                throw new RuntimeException("Unable to create a GUID for the new user");
            }
        }

        // set a few default attributes for new users
        if (StringUtils.isBlank(user.getUserid())) {
            user.setUserid(user.getEmail());
        }
        user.setVerified(false);

        // Generate a random password for the new user if one wasn't already set
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(this.getRandomPasswordGenerator().generatePassword(this.getNewPasswordLength()));
            user.setForcePasswordChange(true);
        }

        // Save the user
        this.getUserDao().save(user);
    }

    /**
     * Update the specified {@link GcxUser}.
     * 
     * @param user
     *            {@link GcxUser} to be updated.
     * @throws GcxUserNotFoundException
     *             The specified user cannot be found to be updated
     */
    @Override
    @Transactional(readOnly = false)
    @Audit(applicationCode = "THEKEY", action = "UPDATE_USER", actionResolverName = "THEKEY_USER_SERVICE_ACTION_RESOLVER", resourceResolverName = "THEKEY_USER_SERVICE_UPDATE_USER_RESOURCE_RESOLVER")
    public void updateUser(final GcxUser user) throws GcxUserNotFoundException {
        final GcxUser original = this.getFreshUser(user);
        this.getUserDao().update(original, user);
    }

    /**
     * Deactivate the user by disabling the account and changing the e-mail
     * address.
     * 
     * @param user
     *            {@link GcxUser} to deactivate
     * @param source
     *            Source identifier of applicaton or entity used to deactivate
     *            user.
     * @param createdBy
     *            Userid or identifier of who is deactivating user (if not
     *            deactivated by the user himself).
     */
    @Transactional
    public void deactivateUser(final GcxUser user, final String source,
	    final String createdBy) {
	// Create a deep clone copy before proceeding
	final GcxUser original = (GcxUser) user.clone();

	/*
	 * Since we are not going to remove the account from the eDirectory
	 * server, we are going to lock down certain attributes to prevent
	 * sneaking back in. This includes generating a new password.
	 */
	user.setEmail(ACCOUNT_DEACTIVATEDPREFIX + "=" + user.getGUID());
	user.setPassword(this.getRandomPasswordGenerator().generatePassword(
		this.getNewPasswordLength()));
        user.removeFacebookId(original.getFacebookId());
	user.setPasswordAllowChange(false);
	user.setLoginDisabled(true);

	// update the user object
	this.getUserDao().update(original, user);

	// Audit the change
	try {
	    this.getAuditService().update(source, createdBy, user.getEmail(),
		    "Deactivating the GCX user", original, user);
	    this.getAuditService().updateProperty(source, createdBy,
		    user.getEmail(), "Deactivating the GCX user", user,
		    GcxUser.FIELD_PASSWORD);
	} catch (final AuditException e) {
	    // suppress the AuditException, but still log it
            LOG.error("error auditing account deactivation", e);
	}
    }

    /**
     * Reactivate a previously deactivated user.
     * 
     * @param user
     *            {@link GcxUser} to reactivate
     * @param source
     *            Source identifier of applicaton or entity used to reactivate
     *            user.
     * @param createdBy
     *            Userid or identifier of who is reactivating user (if not
     *            reactivated by the user himself).
     * @throws GcxUserAlreadyExistsException
     */
    @Transactional
    public void reactivateUser(final GcxUser user, final String source,
	    final String createdBy) throws GcxUserAlreadyExistsException {
	// Determine if the user already exists, and can't be reactivated
	if (this.findUserByEmail(user.getUserid()) != null) {
	    String error = "Unable to reactivate user \"" + user.getUserid()
		    + "\" because that e-mail address is currently active";
	    log.error(error);
	    throw new GcxUserAlreadyExistsException(error);
	}

	// Create a deep clone copy before proceeding
	final GcxUser original = (GcxUser) user.clone();

	// Restore several settings on the user object
	user.setEmail(user.getUserid());
	user.setLoginDisabled(false);
	user.setPasswordAllowChange(true);
	user.setForcePasswordChange(true);

	this.getUserDao().update(original, user);

	// Audit the change
	try {
	    this.getAuditService().update(source, createdBy, user.getEmail(),
		    "Activating the GCX user", original, user);
	} catch (final AuditException e) {
	    // suppress the AuditException, but still log it
            LOG.error("error auditing account reactivation", e);
	}
    }

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
    @Transactional
    public void mergeUsers(final GcxUser a_PrimaryUser,
	    final GcxUser a_UserBeingMerged, final String a_Source,
	    final String a_CreatedBy) throws GcxUserNotFoundException {
        // Transfer the GUID information
        a_PrimaryUser.addGUIDAdditional( a_UserBeingMerged.getGUID() ) ;
        a_PrimaryUser.addGUIDAdditional( a_UserBeingMerged.getGUIDAdditional() ) ;
        
        // Compile list of domains from user being merged
        List<String> domains = new ArrayList<String>() ;
        if ( a_UserBeingMerged.getDomainsVisited() != null ) {
            domains.addAll( a_UserBeingMerged.getDomainsVisited() ) ;
        }
        if ( a_UserBeingMerged.getDomainsVisitedAdditional() != null ) {
            domains.addAll( a_UserBeingMerged.getDomainsVisitedAdditional() ) ;
        }
        // Remove domains already listed with the primary user
        if ( ( a_PrimaryUser.getDomainsVisited() != null ) && ( a_PrimaryUser.getDomainsVisited().size() > 0 ) ) {
            for( int i=0; i<a_PrimaryUser.getDomainsVisited().size(); i++ ) {
                String domain = a_PrimaryUser.getDomainsVisited().get( i ) ;
                if ( domains.contains( domain ) ) {
                    domains.remove( domain ) ;
                }
            }
        }
        // If there is anything left over, transfer those domains
        if ( domains.size() > 0 ) {
            a_PrimaryUser.addDomainsVisitedAdditional( domains ) ;
        }
        
        // Deactivate the user being merged (if it isn't already deactivated)
        if ( !a_UserBeingMerged.isDeactivated() ) {
            LOG.debug("***** Deactivating the user being merged");
            this.deactivateUser( a_UserBeingMerged, a_Source, a_CreatedBy ) ;
        } else {
            LOG.debug("***** The user being merged is already deactivated");
        }
        
        // Save the primary user
        this.updateUser(a_PrimaryUser);
        
        this.getAuditService().merge( a_Source, a_CreatedBy, a_PrimaryUser, a_UserBeingMerged, 
                                      "Merged GCX user" ) ;
    }

    /** 
     * Locate the user (not transitional) with the specified e-mail address.
     * 
     * @param a_Email E-mail address of user to find.
     * 
     * @return {@link GcxUser} with the specified e-mail address, or <tt>null</tt> if not found.
     */
    @Transactional(readOnly = true)
    public GcxUser findUserByEmail( String a_Email )
    {
	GcxUser result = this.getUserDao().findByEmail(a_Email);
        
        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
    }

    
    /** 
     * Locate the user (not transitional) with the specified guid.
     * 
     * @param a_Guid GUID of user to find.
     * 
     * @return {@link GcxUser} with the specified guid, or <tt>null</tt> if not found.
     */
    @Transactional(readOnly = true)
    public GcxUser findUserByGuid( String a_Guid )
    {
	GcxUser result = this.getUserDao().findByGUID(a_Guid);
        
        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
    }

    /**
     * Locate the user with the specified facebook id.
     * 
     * @param facebookId
     *            GUID of user to find.
     * @return {@link GcxUser} with the specified guid, or <tt>null</tt> if not
     *         found.
     */
    @Transactional(readOnly = true)
    public GcxUser findUserByFacebookId(final String facebookId) {
	final GcxUser user = this.getUserDao().findByFacebookId(facebookId);
	this.validateRepairUserIntegrity(user);

	return user;
    }

    /**
     * Locate the user with the specified Relay guid.
     * 
     * @param guid
     *            Relay guid of user to find.
     * @return {@link GcxUser} with the specified Relay guid, or <tt>null</tt>
     *         if not found.
     */
    @Override
    @Transactional(readOnly = true)
    public GcxUser findUserByRelayGuid(final String guid) {
        final GcxUser user = this.getUserDao().findByRelayGuid(guid);
        this.validateRepairUserIntegrity(user);

        return user;
    }

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
    @Transactional(readOnly = true)
    public List<GcxUser> findAllByFirstName(final String pattern)
	    throws ExceededMaximumAllowedResults {
	List<GcxUser> result = this.getUserDao().findAllByFirstName(pattern);

        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
    }
    
    
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
    @Transactional(readOnly = true)
    public List<GcxUser> findAllByLastName(final String pattern)
	    throws ExceededMaximumAllowedResults {
	List<GcxUser> result = this.getUserDao().findAllByLastName(pattern);
        
        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
    }
    
    
    /**
     * Find all users matching the e-mail pattern.
     * 
     * @param pattern
     *            Pattern used for matching last name.
     * 
     * @return {@link List} of {@link GcxUser} objects, or <tt>null</tt> if none
     *         are found.
     * @throws ExceededMaximumAllowedResults
     */
    @Transactional(readOnly = true)
    public List<GcxUser> findAllByEmail(final String pattern)
	    throws ExceededMaximumAllowedResults {
	final List<GcxUser> result = this.getUserDao().findAllByEmail(pattern);
        
        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
    }

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
    @Transactional(readOnly = true)
    public List<GcxUser> findAllByUserid(final String pattern,
	    final boolean includeDeactivated)
	    throws ExceededMaximumAllowedResults {
	final List<GcxUser> result = this.getUserDao().findAllByUserid(pattern,
		includeDeactivated);
         
         this.validateRepairUserIntegrity( result ) ;
         
         return result ;
    }
}
