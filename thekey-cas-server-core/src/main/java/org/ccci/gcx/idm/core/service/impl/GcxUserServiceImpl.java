package org.ccci.gcx.idm.core.service.impl;

import static org.ccci.gto.cas.Constants.ACCOUNT_DEACTIVATEDPREFIX;
import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_FLAG;
import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_FLAGVALUE;
import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_KEY;
import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_USERNAME;
import static org.ccci.gto.cas.Constants.PARAMETER_LOGINTICKET;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ExceededMaximumAllowedResults;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>GcxUserServiceImpl</b> is the concrete implementation of {@link GcxUserService}.
 *
 * @author Greg Crider  Oct 21, 2008  1:35:01 PM
 */
public class GcxUserServiceImpl extends AbstractGcxUserService {
    @NotNull
    private UriBuilder activationUriBuilder;

    /**
     * Send user a notification that his password has been changed.
     * 
     * @param a_GcxUser The {@link GcxUser} object that was updated with a new password.
     */
    private void sendResetNotification( GcxUser a_GcxUser )
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Preparing e-mail notification" ) ;
        
        /*
         * TODO: We need to acquire the right locale for the user in order to pull out the
         *       proper resource file from the message source. Right now, everything will
         *       use the default.
         */
	final Locale locale = org.springframework.util.StringUtils
		.parseLocaleString(a_GcxUser.getCountryCode());
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "User Locale: " + locale ) ;
        
        Map<String, Object> model = new HashMap<String, Object>() ;
        
        model.put( "title",          this.getMessageSource().getMessage( "newpassword.title", null, "?", locale ) ) ;
        model.put( "body",           this.getMessageSource().getMessage( "newpassword.body", null, "?", locale ) ) ;
        model.put( "passwordlabel",  this.getMessageSource().getMessage( "newpassword.passwordlabel", null, "?", locale ) ) ;
        model.put( "loginlinklabel", this.getMessageSource().getMessage( "newpassword.loginlinklabel", null, "?", locale ) ) ;
        model.put( "user",           a_GcxUser ) ;
        model.put( "loginlink",      this.getGcxLoginURL() ) ;
        
        OutgoingMailMessage message = new OutgoingMailMessage() ;
        message.setTo( a_GcxUser.getEmail() ) ;
        message.setReplyTo( this.getReplyTo() ) ;
        message.setMessageContentModel( model ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Sending e-mail notification" ) ;

	this.getMailSender().send(this.getNewPasswordTemplate(), message);
    }
    
    
    /**
     * Send user a notification that his account needs to be activated.
     * 
     * @param a_GcxUser The {@link GcxUser} object that was updated with a new password.
     */
    private void sendActivationNotification( GcxUser a_GcxUser )
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Preparing e-mail notification" ) ;
        
        /*
         * TODO: We need to acquire the right locale for the user in order to pull out the
         *       proper resource file from the message source. Right now, everything will
         *       use the default.
         */
	final Locale locale = org.springframework.util.StringUtils
		.parseLocaleString(a_GcxUser.getCountryCode());
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "User Locale: " + locale ) ;

	URI activationUri = this.activationUriBuilder.build(
		a_GcxUser.getEmail(),
		a_GcxUser.getPassword());
        
        Map<String, Object> model = new HashMap<String, Object>() ;
        
        model.put( "title",               this.getMessageSource().getMessage( "activation.title", null, "?", locale ) ) ;
        model.put( "body",                this.getMessageSource().getMessage( "activation.body", null, "?", locale ) ) ;
        model.put( "useridlabel",         this.getMessageSource().getMessage( "activation.useridlabel", null, "?", locale ) ) ;
        model.put( "passwordlabel",       this.getMessageSource().getMessage( "activation.passwordlabel", null, "?", locale ) ) ;
        model.put( "user",                a_GcxUser ) ;
        model.put( "activationlinklabel", 	  this.getMessageSource().getMessage( "activation.activationlinklabel", null, "?", locale ) ) ;
	model.put("activationlink", activationUri.toString());
        
        OutgoingMailMessage message = new OutgoingMailMessage() ;
        message.setTo( a_GcxUser.getEmail() ) ;
        message.setReplyTo( this.getReplyTo() ) ;
        message.setMessageContentModel( model ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Sending e-mail notification" ) ;

	this.getMailSender().send(this.getActivationTemplate(), message);
    }

    /**
     * Determine if the specified user already exists in the permanent backing
     * store.
     * 
     * @param user
     *            {@link GcxUser} to be verified.
     */
    @Transactional(readOnly = true)
    public boolean doesUserExist(final GcxUser user) {
	log.debug("***** Checking to see if the specified user exists in the user backing store");
	final GcxUserDao userDao = this.getUserDao();
        
	if (userDao.findByGUID(user.getGUID()) != null) {
	    if (log.isDebugEnabled()) {
		log.debug("***** GUID \"" + user.getGUID()
			+ "\" already exists");
	    }
	    return true;
	} else if (userDao.findByEmail(user.getEmail()) != null) {
	    if (log.isDebugEnabled()) {
		log.debug("***** Email \"" + user.getEmail()
			+ "\" already exists");
	    }
	    return true;
	}

	return false;
    }

    @Transactional
    public void createUser(final GcxUser user, final String source,
	    final boolean sendEmail) {
	if (log.isDebugEnabled()) {
	    log.debug("***** Preparing to create user: " + user);
	}

	// check to see if the user already exists
	if (this.doesUserExist(user)) {
	    final String error = "The specified user with e-mail \""
		    + user.getEmail() + "\" already exists.";
	    log.error(error);
	    throw new GcxUserAlreadyExistsException(error);
	}
	log.debug("***** User not found, so we'll attempt to save it");

	// set a few default attributes for new users
	if (StringUtils.isBlank(user.getUserid())) {
	    user.setUserid(user.getEmail());
	}
	user.setVerified(false);
	// Generate a random password for the new user if one wasn't already set
	if (StringUtils.isBlank(user.getPassword())) {
	    user.setPassword(this.getRandomPasswordGenerator()
		    .generatePassword(this.getNewPasswordLength()));
	    user.setForcePasswordChange(true);
	}

	// Save the user
	this.getUserDao().save(user);

	// Audit the change
	log.debug("***** Creating audit of new user creation");
	this.getAuditService().create(source, user.getEmail(), user.getEmail(),
		"Creating new user for The Key", user);

	// Send activation e-mail to user if required
	if (sendEmail) {
	    this.sendActivationNotification(user);
	}
    }

    /**
     * Update the specified {@link GcxUser}.
     * 
     * @param user {@link GcxUser} to be updated.
     * @param a_HasPasswordChange If <tt>true</tt> then the password has been changed.
     * @param a_Source Source identifier of applicaton or entity used to update user.
     * @param a_CreatedBy Userid or identifier of who is updating user (if not updated by the
     *        user himself).
     */
    @Transactional
    public void updateUser(final GcxUser user, boolean a_HasPasswordChange,
	    String a_Source, String a_CreatedBy) {
	if (log.isDebugEnabled()) {
	    log.debug("***** Preparing to update user: " + user);
	}

	final GcxUser original = this.getFreshUser(user);
	this.getUserDao().update(original, user);

        // Audit the change
	this.getAuditService().update(a_Source, a_CreatedBy, user.getEmail(),
		"Updating the GCX user", original, user);
        if ( a_HasPasswordChange ) {
	    this.getAuditService().updateProperty(a_Source, a_CreatedBy,
		    user.getEmail(), "Updating the GCX user password", user,
                    GcxUser.FIELD_PASSWORD 
                    ) ;
        }
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
	user.setFacebookId(null);
	user.setPasswordAllowChange(false);
	user.setLoginDisabled(true);

	// update the user object
	this.getUserDao().update(original, user);

	// Audit the change
	this.getAuditService().update(source, createdBy, user.getEmail(),
		"Deactivating the GCX user", original, user);
	this.getAuditService().updateProperty(source, createdBy,
		user.getEmail(), "Deactivating the GCX user", user,
		GcxUser.FIELD_PASSWORD);
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
     */
    @Transactional
    public void reactivateUser(final GcxUser user, final String source,
	    final String createdBy) {
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
	this.getAuditService().update(source, createdBy, user.getEmail(),
		"Activating the GCX user", original, user);

	// Now we need to reset the user's password since it was wiped out with
	// the deactivation
	this.resetPassword(user, source, createdBy);
    }

    /**
     * Reset the user's password and send the newly created password to his e-mail address.
     * 
     * @param a_GcxUser {@link GcxUser} to reactivate
     * @param a_Source Source identifier of applicaton or entity used to reactivate user.
     * @param a_CreatedBy Userid or identifier of who is reactivating user (if not reactivated by the
     *        user himself).
     */
    @Transactional
    public void resetPassword( GcxUser a_GcxUser, String a_Source, String a_CreatedBy )
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Generating new password for user \"" + a_GcxUser.getEmail() + "\"" ) ;

        String password = this.getRandomPasswordGenerator().generatePassword( this.getNewPasswordLength() ) ;
        
        // Set the newly generated password
        a_GcxUser.setPassword( password ) ;
        // Force the user to change his password after login
        a_GcxUser.setForcePasswordChange( true ) ;
        
        // Save the change
	this.getUserDao().update(a_GcxUser);
        
        // Audit the change
        this.getAuditService().updateProperty( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Resetting the GCX user password", 
                a_GcxUser, 
                GcxUser.FIELD_PASSWORD 
                ) ;
        
        // Send notification to user with his new password
        
        this.sendResetNotification( a_GcxUser ) ;
    }
    
    
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
    @Transactional
    public void mergeUsers( GcxUser a_PrimaryUser, GcxUser a_UserBeingMerged, String a_Source, String a_CreatedBy )
    {
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
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Deactivating the user being merged" ) ;
            this.deactivateUser( a_UserBeingMerged, a_Source, a_CreatedBy ) ;
        } else {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** The user being merged is already deactivated" ) ;
        }
        
        // Save the primary user
        this.updateUser( a_PrimaryUser, false, a_Source, a_CreatedBy ) ;
        
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

    public void setLoginUri(final String uri) {
	super.setLoginUri(uri);

	// generate the activation uri
	{
	    activationUriBuilder = UriBuilder.fromUri(uri);
	    activationUriBuilder.replaceQueryParam(PARAMETER_LOGINTICKET, "");
	    activationUriBuilder.replaceQueryParam(PARAMETER_ACTIVATION_FLAG,
		    PARAMETER_ACTIVATION_FLAGVALUE);
	    activationUriBuilder.replaceQueryParam(
		    PARAMETER_ACTIVATION_USERNAME, "{arg1}");
	    activationUriBuilder.replaceQueryParam(PARAMETER_ACTIVATION_KEY,
		    "{arg2}");
	}
    }
}
