package org.ccci.gcx.idm.core.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.directory.SearchControls;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gcx.idm.core.GcxUserAccountLockedException;
import org.ccci.gcx.idm.core.GcxUserAlreadyExistsException;
import org.ccci.gcx.idm.core.GcxUserAuthenticationErrorException;
import org.ccci.gcx.idm.core.GcxUserException;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.core.util.LdapUtil;
import org.ccci.gto.cas.Constants;
import org.ccci.gto.cas.persist.ldap.GcxUserMapper;
import org.ccci.gto.cas.util.RandomGUID;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

/**
 * <b>GcxUserServiceImpl</b> is the concrete implementation of {@link GcxUserService}.
 *
 * @author Greg Crider  Oct 21, 2008  1:35:01 PM
 */
public class GcxUserServiceImpl extends AbstractGcxUserService {
    /** various constants used in this class */
    private static final String ACCOUNT_DEACTIVATEDPREFIX = Constants.ACCOUNT_DEACTIVATEDPREFIX;
    private static final String LDAP_ATTR_PASSWORD = Constants.LDAP_ATTR_PASSWORD;
    private static final String PARAMETER_ACTIVATION_FLAG = Constants.PARAMETER_ACTIVATION_FLAG;
    private static final String PARAMETER_ACTIVATION_FLAGVALUE = Constants.PARAMETER_ACTIVATION_FLAGVALUE;
    private static final String PARAMETER_ACTIVATION_USERNAME = Constants.PARAMETER_ACTIVATION_USERNAME;
    private static final String PARAMETER_ACTIVATION_KEY = Constants.PARAMETER_ACTIVATION_KEY;

    @NotNull
    private UriBuilder activationUriBuilder;

    /** Pattern for DN used in authenticating a user. */
    private String m_AuthenticationDNPattern = null ;
    /** List of substitution properties for authentication dn pattern. */
    private List<String> m_AuthenticationSubstitutionProperties = null ;
    /** LdapTemplate, not proxied or used in transaction, for authenticating user. */
    private LdapTemplate m_LdapTemplateNoTX = null ;
    
    
    /**
     * @return the authenticationDNPattern
     */
    public String getAuthenticationDNPattern()
    {
        return this.m_AuthenticationDNPattern ;
    }
    /**
     * @param a_authenticationDNPattern the authenticationDNPattern to set
     */
    public void setAuthenticationDNPattern( String a_authenticationDNPattern )
    {
        this.m_AuthenticationDNPattern = a_authenticationDNPattern ;
    }


    /**
     * @return the authenticationSubstitutionProperties
     */
    public List<String> getAuthenticationSubstitutionProperties()
    {
        return this.m_AuthenticationSubstitutionProperties ;
    }
    /**
     * @param a_authenticationSubstitutionProperties the authenticationSubstitutionProperties to set
     */
    public void setAuthenticationSubstitutionProperties( List<String> a_authenticationSubstitutionProperties )
    {
        this.m_AuthenticationSubstitutionProperties = a_authenticationSubstitutionProperties ;
    }


    /**
     * @return the ldapTemplateNoTX
     */
    public LdapTemplate getLdapTemplateNoTX()
    {
        return this.m_LdapTemplateNoTX ;
    }
    /**
     * @param a_ldapTemplateNoTX the ldapTemplateNoTX to set
     */
    public void setLdapTemplateNoTX( LdapTemplate a_ldapTemplateNoTX )
    {
        this.m_LdapTemplateNoTX = a_ldapTemplateNoTX ;
    }
    
    
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
        Locale locale = this.getLocaleByCountryCode( a_GcxUser.getCountryCode() ) ;
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

        this.getMailService().send( this.getNewPasswordTemplate(), message ) ;
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
        Locale locale = this.getLocaleByCountryCode( a_GcxUser.getCountryCode() ) ;
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

        this.getMailService().send( this.getActivationTemplate(), message ) ;
    }
    
    
    /**
     * Determine if the specified user already exists in the transitional backing store.
     * 
     * @param a_GcxUser {@link GcxUser} to be verified.
     */
    @Deprecated
    public boolean doesTransitionalUserExist( GcxUser a_GcxUser )
    {
        boolean result = false ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Verify that user doesn't already exist in transitional backing store" ) ;

        if ( this.getTransitionalGcxUserDao().findByGUID( a_GcxUser.getGUID() ) != null ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** GUID \"" + a_GcxUser.getGUID() + "\" already exists" ) ;
            result = true ;
        } else if ( this.getTransitionalGcxUserDao().findByEmail( a_GcxUser.getEmail() ) != null ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Email \"" + a_GcxUser.getEmail() + "\" already exists" ) ;
            result = true ;
        }
        
        return result ;
    }
    
    
    /**
     * Determine if the specified user already exists in the permanent backing store.
     * 
     * @param a_GcxUser {@link GcxUser} to be verified.
     */
    public boolean doesUserExist( GcxUser a_GcxUser ) 
    {
        boolean result = false ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Verify that user doesn't already exist in permanent backing store" ) ;

        if ( this.getGcxUserDao().findByGUID( a_GcxUser.getGUID() ) != null ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** GUID \"" + a_GcxUser.getGUID() + "\" already exists" ) ;
            result = true ;
        } else if ( this.getGcxUserDao().findByEmail( a_GcxUser.getEmail() ) != null ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Email \"" + a_GcxUser.getEmail() + "\" already exists" ) ;
            result = true ;
        }
        
        return result ;
    }
    

    /**
     * Save the newly create {@link GcxUser} object in the transitional backing
     * store. Afterwards, an activation e-mail notification is sent to the user.
     * 
     * @param a_GcxUser {@link GcxUser} object to be saved.
     * @param a_Source Source identifier of applicaton or entity used to create user.
     * @param a_CreatedBy Userid or identifier of who is creating user (if not created by the
     *        user himself).
     *        
     * @exception GcxUserAlreadyExistsException is an unchecked exception that is thrown if
     *        an attempt is made to create a user whose e-mail address or GUID already exists.
     * 
     * @see org.ccci.gcx.idm.core.service.GcxUserService#createTransitionalUser(org.ccci.gcx.idm.core.model.impl.GcxUser,java.lang.String,java.lang.String)
     */
    @Deprecated
    public void createTransitionalUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy )
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Preparing to save transitional user: " + a_GcxUser ) ;
        
        if ( this.doesTransitionalUserExist( a_GcxUser ) || this.doesUserExist( a_GcxUser ) ) {
            String error = "The specified user with e-mail \"" + a_GcxUser.getEmail() + "\" already exists." ;
            /*= ERROR =*/ log.error( error ) ;
            throw new GcxUserAlreadyExistsException( error ) ;
        }
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** User not found, so we'll attempt to save it" ) ;
        
        // Generate random password for new user
        String password = this.getRandomPasswordGenerator().generatePassword( this.getNewPasswordLength() ) ;
        
        // Set the newly generated password
        a_GcxUser.setPassword( password ) ;
        // Force the user to change his password after login
        a_GcxUser.setForcePasswordChange( true ) ;

        // Save the user in the transitional backing store
        this.getTransitionalGcxUserDao().save( a_GcxUser ) ;

        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Creating audit of new user creation" ) ;

        // Audit the change
        this.getAuditService().create( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Creating new transitional GCX user", 
                a_GcxUser
                ) ;
        
        // Send activation e-mail to user
        
        this.sendActivationNotification( a_GcxUser ) ;
    }


    /**
     * Save the newly created {@link GcxUser} object in the transitional backing store. Use
     * this method if the the user is self-created (by the user himself).
     * 
     * @param a_GcxUser {@link GcxUser} object to be saved.
     * @param a_Source Source identifier of applicaton or entity used to create user.
     * 
     * @see org.ccci.gcx.idm.core.service.GcxUserService#createTransitionalUser(org.ccci.gcx.idm.core.model.impl.GcxUser, java.lang.String)
     */
    @Deprecated
    public void createTransitionalUser( GcxUser a_GcxUser, String a_Source )
    {
        this.createTransitionalUser( a_GcxUser, a_Source, a_GcxUser.getEmail() ) ;
    }

    public void createUser(final GcxUser user, final String source) {
	this.createUser(user, source, user.getEmail());
    }

    public void createUser(final GcxUser user, final String source,
	    final String creator) {
	if (log.isDebugEnabled()) {
	    log.debug("***** Preparing to create user: " + user);
	}

	// check to see if the user already exists
	if (this.doesUserExist(user) || this.doesTransitionalUserExist(user)) {
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
	this.getGcxUserDao().save(user);

	// Audit the change
	log.debug("***** Creating audit of new user creation");
	this.getAuditService().create(source, creator, user.getEmail(),
		"Creating new user for The Key", user);

	// Send activation e-mail to user
	this.sendActivationNotification(user);
    }

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
    public void activateTransitionalUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy ) 
    {
        // Make sure the proper information is available
        if ( ( StringUtils.isBlank( a_GcxUser.getEmail() ) ) && ( StringUtils.isBlank( a_GcxUser.getGUID() ) ) ) {
            String error = "An e-mail address or GUID value must be present in order to create the user." ;
            throw new IllegalArgumentException( error ) ;
        }
        // Make sure the transitional user exists
        GcxUser recoveredUser = null ;
        // Try to locate by e-mail address first.
        if ( StringUtils.isNotBlank( a_GcxUser.getEmail() ) ) {
            recoveredUser = this.getTransitionalGcxUserDao().findByEmail( a_GcxUser.getEmail() ) ;
        }
        // If we can't find the user by e-mail address, then try by GUID
        if ( ( recoveredUser == null ) && ( StringUtils.isNotBlank( a_GcxUser.getGUID() ) ) ) {
            recoveredUser = this.getTransitionalGcxUserDao().findByGUID( a_GcxUser.getGUID() ) ;
        }
        // Throw an exception if we were not successful in locating the transitional user
        if ( recoveredUser == null ) {
            String error = "Unable to locate the transitional user \"" + a_GcxUser.getEmail() + " [optional GUID = \"" + a_GcxUser.getGUID() + "\"]." ;
            throw new GcxUserException( error ) ;
        }
        
        // Set the userid
        recoveredUser.setUserid( recoveredUser.getEmail() ) ;
        
        // Activated users always have to change their password 6/29/10, kb
        recoveredUser.setForcePasswordChange(true);
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Recovered transitional user: " + recoveredUser ) ;
        
        // Create new, permanent account with recovered information
        this.getGcxUserDao().save( recoveredUser ) ;
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Successfully created new, permanent user" ) ;
        
        // Now remove the transitional user
        this.getTransitionalGcxUserDao().delete( recoveredUser ) ;
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Successfully removed transitional user" ) ;

        // Audit the change
        this.getAuditService().create( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Creating new GCX user through activation", 
                a_GcxUser
                ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "Successfully created the new user: " + recoveredUser ) ;
    }
    
    
    /**
     * Permanently delete the specified {@link GcxUser}.
     * 
     * @param a_GcxUser {@link GcxUser} to be deleted.
     * @param a_Source Source identifier of applicaton or entity used to delete user.
     * @param a_CreatedBy Userid or identifier of who is deleting user (if not deleted by the
     *        user himself).
     */
    public void deleteUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy ) 
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Preparing to delete user: " + a_GcxUser ) ;
        this.getGcxUserDao().delete( a_GcxUser ) ;

        // Audit the change
        this.getAuditService().delete( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Permanently deleted the GCX user", 
                a_GcxUser
                ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "Successfully deleted the user: " + a_GcxUser ) ;
    }

    /**
     * Authenticate the user in the specified {@link GcxUser} object. The {@link GcxUser} object
     * does not necessarily need to be fully populated, but should contain the essential information
     * used for authentication challenges. <b>Presently, the {@link GcxUser} object must contain
     * the <tt>email</tt> and <tt>password</tt> properties.
     * 
     * @param a_GcxUser {@link GcxUser} to authenticate.
     * 
     * @exception {@link GcxUserAuthenticationErrorException} when an invalid userid/password is specified.
     * @exception {@link GcxUserAccountLockedException} when the specified user account is locked or disabled.
     */
    @SuppressWarnings("unchecked")
    public void authenticate( GcxUser a_GcxUser )
    {
        // Enforce lower case on e-mail
        a_GcxUser.setEmail( a_GcxUser.getEmail().toLowerCase() ) ;
        
        // Create the DN used for authentication
        String dn = LdapUtil.generateModelDNFromPattern( a_GcxUser, this.getAuthenticationDNPattern(), this.getAuthenticationSubstitutionProperties() ) ;

        /*
         * Do a search on the specified userid and filter with the password. Use the out of band, non-tx version
         * of the LDAP template so if an exception is thrown, it won't cause a rollback. 
         */
        try {
	    Filter passwordFilter = new EqualsFilter(LDAP_ATTR_PASSWORD,
		    a_GcxUser.getPassword());
            List<GcxUser> searchResults = (List<GcxUser>)this.getLdapTemplateNoTX().search( dn, passwordFilter.encode(), SearchControls.OBJECT_SCOPE, new String[]{}, new GcxUserMapper() ) ;
            if ( ( searchResults == null ) || ( searchResults.size() != 1 ) ) {
                String error = "The user \"" + a_GcxUser.getEmail() + "\" or the specified password is not valid" ;
                /*= ERROR =*/ log.error( error ) ;
                throw new GcxUserAuthenticationErrorException( error ) ;
            }
        } catch ( NameNotFoundException nnfe ) {
            String error = "The user \"" + a_GcxUser.getEmail() + "\" does not exist" ;
            /*= ERROR =*/ log.error( error, nnfe ) ;
            throw new GcxUserNotFoundException( error, nnfe ) ;
        } catch ( NamingException ne ) {
            String error = "Unable to authenticate probably because of an invalid password" ;
            /*= ERROR =*/ log.error( error, ne ) ;
            throw new GcxUserAuthenticationErrorException( error, ne ) ;
        }
        
        /*
         *  Okay, since we've made it this far we know that the userid exists, and the password is correct. 
         *  That means it is safe to use the DAO within the current transaction to do a lookup (it shouldn't
         *  fail and cause a rollback).
         */
        GcxUser user = this.findUserByEmail( a_GcxUser.getEmail() ) ;
        if ( user.isLoginDisabled() ) {
            String error = "The user account has been disabled" ;
            /*= ERROR =*/ log.error( error ) ;
            throw new GcxUserAuthenticationErrorException( error ) ;
        } if ( user.isLocked() ) {
            String error = "The user account has been administratively locked" ;
            /*= ERROR =*/ log.error( error ) ;
            throw new GcxUserAccountLockedException( error ) ;
        }
    }
    
    
    /**
     * Update the specified {@link GcxUser}.
     * 
     * @param a_GcxUser {@link GcxUser} to be updated.
     * @param a_HasPasswordChange If <tt>true</tt> then the password has been changed.
     * @param a_Source Source identifier of applicaton or entity used to update user.
     * @param a_CreatedBy Userid or identifier of who is updating user (if not updated by the
     *        user himself).
     */
    public void updateUser( GcxUser a_GcxUser, boolean a_HasPasswordChange, String a_Source, String a_CreatedBy )
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Preparing to update user: " + a_GcxUser ) ;
        
        // Get the original version of this object for the purpose of an audit (there is a race condition here
        // if somebody else has successfully modified it while this call is running). Do the lookup by GUID in
        // case the e-mail address was changed.
        GcxUser original = this.getGcxUserDao().findByGUID( a_GcxUser.getGUID() ) ;
        
        // If the e-mail address didn't change, we can do a straight save
        if ( a_GcxUser.getEmail().equals( original.getEmail() ) ) {
            // Perform the update
            this.getGcxUserDao().update( a_GcxUser ) ;
        // If the e-mail address changed, we need to delete the current entry and save a new one
        } else {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** E-mail address changed, so we must delete and resave the entry" ) ;
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Deleting original user: " + original ) ;
            this.deleteUser( original, a_Source, a_CreatedBy ) ;
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Saving new user: " + a_GcxUser ) ;
            this.getGcxUserDao().save( a_GcxUser ) ;
        }
        
        // Audit the change
        this.getAuditService().update( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Updating the GCX user", 
                original,
                a_GcxUser
                ) ;
        if ( a_HasPasswordChange ) {
            this.getAuditService().updateProperty( 
                    a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                    "Updating the GCX user password", 
                    a_GcxUser, 
                    GcxUser.FIELD_PASSWORD 
                    ) ;
        }
    }
    
    
    /**
     * Deactivate the user by disabling the account and changing the e-mail address.
     * 
     * @param a_GcxUser {@link GcxUser} to deactivate
     * @param a_Source Source identifier of applicaton or entity used to deactivate user.
     * @param a_CreatedBy Userid or identifier of who is deactivating user (if not deactivated by the
     *        user himself).
     */
    public void deactivateUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy )
    {
        StringBuffer newEmail = new StringBuffer() ;
        
        // Create a deep clone copy before proceeding
        GcxUser original = (GcxUser)a_GcxUser.clone() ;
        
        /*
         * Since we are not going to remove the account from the eDirectory server, we are
         * going to lock down certain attributes to prevent sneaking back in. This includes
         * using the GUID generator to create a password that would be hard to guess.
         */
        
	newEmail.append(ACCOUNT_DEACTIVATEDPREFIX)
                .append( "-" )
                .append( a_GcxUser.getGUID() )
                ;
        
        a_GcxUser.setLoginDisabled( true ) ;
        a_GcxUser.setPasswordAllowChange( false ) ;
        a_GcxUser.setForcePasswordChange( false ) ;
        a_GcxUser.setEmail( newEmail.toString() ) ;
	a_GcxUser.setPassword(StringUtils.replace(
		RandomGUID.generateGuid(true), "-", ""));
        
        // Since the e-mail address is changing, we can't do an update. We have to save the new
        // entry and delete the old one. Do it in that order in case the save fails.
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Deleting original user: " + original ) ;
        this.deleteUser( original, a_Source, a_CreatedBy ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Saving new, deactivated user: " + a_GcxUser ) ;
        this.getGcxUserDao().save( a_GcxUser ) ;
        
        // Audit the change
        this.getAuditService().update( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Deactivating the GCX user", 
                original,
                a_GcxUser
                ) ;
        this.getAuditService().updateProperty( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Deactivating the GCX user", 
                a_GcxUser, 
                GcxUser.FIELD_PASSWORD 
                ) ;
    }
    
    
    /**
     * Reactivate a previously deactivated user.
     * 
     * @param a_GcxUser {@link GcxUser} to reactivate
     * @param a_Source Source identifier of applicaton or entity used to reactivate user.
     * @param a_CreatedBy Userid or identifier of who is reactivating user (if not reactivated by the
     *        user himself).
     */
    public void reactivateUser( GcxUser a_GcxUser, String a_Source, String a_CreatedBy )
    {
        // Determine if the user already exists, and can't be reactivated
        GcxUser existingUser = this.findUserByEmail( a_GcxUser.getUserid() ) ;
        if ( existingUser != null ) {
            String error = "Unable to reactivate user \"" + a_GcxUser.getUserid() + "\" because that e-mail address is currently active" ;
            /*= ERROR =*/ log.error( error ) ;
            throw new GcxUserAlreadyExistsException( error ) ;
        }
        
        // Create a deep clone copy before proceeding
        GcxUser original = (GcxUser)a_GcxUser.clone() ;

        // Restore the e-mail address
        a_GcxUser.setEmail( a_GcxUser.getUserid() ) ;
        
        a_GcxUser.setLoginDisabled( false ) ;
        a_GcxUser.setPasswordAllowChange( true ) ;
        // When deactivated, the old password is scrubbed out, so force the user to create a new one on login
        a_GcxUser.setForcePasswordChange( true ) ;
        
        // Since the e-mail address is changing, we can't do an update. We have to save the new
        // entry and delete the old one. Do it in that order in case the save fails.
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Deleting original user: " + original ) ;
        this.deleteUser( original, a_Source, a_CreatedBy ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Saving new, reactivated user: " + a_GcxUser ) ;
        this.getGcxUserDao().save( a_GcxUser ) ;
        
        // Audit the change
        this.getAuditService().update( 
                a_Source, a_CreatedBy, a_GcxUser.getEmail(), 
                "Activating the GCX user", 
                original,
                a_GcxUser
                ) ;
        
        // Now we need to reset the user's password since it was wiped out with the deactivation
        this.resetPassword( a_GcxUser, a_Source, a_CreatedBy ) ;
    }
    
    
    /**
     * Reset the user's password and send the newly created password to his e-mail address.
     * 
     * @param a_GcxUser {@link GcxUser} to reactivate
     * @param a_Source Source identifier of applicaton or entity used to reactivate user.
     * @param a_CreatedBy Userid or identifier of who is reactivating user (if not reactivated by the
     *        user himself).
     */
    public void resetPassword( GcxUser a_GcxUser, String a_Source, String a_CreatedBy )
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Generating new password for user \"" + a_GcxUser.getEmail() + "\"" ) ;

        String password = this.getRandomPasswordGenerator().generatePassword( this.getNewPasswordLength() ) ;
        
        // Set the newly generated password
        a_GcxUser.setPassword( password ) ;
        // Force the user to change his password after login
        a_GcxUser.setForcePasswordChange( true ) ;
        
        // Save the change
        this.getGcxUserDao().update( a_GcxUser ) ;
        
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
     * Locate the transitional user with the specified e-mail address.
     * 
     * @param a_Email E-mail address of user to find.
     * 
     * @return {@link GcxUser} with the specified e-mail address, or <tt>null</tt> if not found.
     */
    @Deprecated
    public GcxUser findTransitionalUserByEmail( String a_Email )
    {
        return this.getTransitionalGcxUserDao().findByEmail( a_Email ) ;
    }

    
    /** 
     * Locate the user (not transitional) with the specified e-mail address.
     * 
     * @param a_Email E-mail address of user to find.
     * 
     * @return {@link GcxUser} with the specified e-mail address, or <tt>null</tt> if not found.
     */
    public GcxUser findUserByEmail( String a_Email )
    {
        GcxUser result = this.getGcxUserDao().findByEmail( a_Email ) ;
        
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
    public GcxUser findUserByGuid( String a_Guid )
    {
        GcxUser result = this.getGcxUserDao().findByGUID(a_Guid) ;
        
        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
    }
    
    /** 
     * Locate the transitional user with the specified guid.
     * 
     * @param a_Guid guid of user to find.
     * 
     * @return {@link GcxUser} with the specified guids, or <tt>null</tt> if not found.
     */
    @Deprecated
    public GcxUser findTransitionalUserByGuid( String a_Guid )
    {
        return this.getTransitionalGcxUserDao().findByEmail( a_Guid ) ;
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
        List<GcxUser> result = this.getGcxUserDao().findAllByFirstName( a_FirstNamePattern ) ;

        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
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
        List<GcxUser> result = this.getGcxUserDao().findAllByLastName( a_LastNamePattern ) ;
        
        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
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
        List<GcxUser> result = this.getGcxUserDao().findAllByEmail( a_EmailPattern ) ;
        
        this.validateRepairUserIntegrity( result ) ;
        
        return result ;
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
         List<GcxUser> result = this.getGcxUserDao().findAllByUserid( a_UseridPattern, a_IncludeDeactivated ) ;
         
         this.validateRepairUserIntegrity( result ) ;
         
         return result ;
    }

    
    /**
     * Test whether or not the specified user is part of the admin group.
     * 
     * @param a_GcxUser {@link GcxUser} to be tested for admin group.
     * 
     * @return <tt>True</tt> if the user is part of the admin group, otherwise <tt>false</tt>.
     */
    public boolean isUserInAdminGroup( GcxUser a_GcxUser )
    {
        boolean result = false ;
        
        if ( a_GcxUser.getGroupMembership() != null ) {
            Iterator<String> it = a_GcxUser.getGroupMembership().iterator() ;
            while( !result && it.hasNext() ) {
                result = it.next().equals( this.getAdminGroupDN() ) ;
            }
        }
        
        return result ;
    }

    public void setLoginUri(String uri) {
	// generate the activation uri
	{
	    activationUriBuilder = UriBuilder.fromUri(uri);
	    activationUriBuilder.replaceQueryParam(PARAMETER_ACTIVATION_FLAG,
		    PARAMETER_ACTIVATION_FLAGVALUE);
	    activationUriBuilder.replaceQueryParam(
		    PARAMETER_ACTIVATION_USERNAME, "{arg1}");
	    activationUriBuilder.replaceQueryParam(PARAMETER_ACTIVATION_KEY,
		    "{arg2}");
	}
    }
}
