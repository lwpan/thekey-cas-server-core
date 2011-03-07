package org.ccci.gcx.idm.core.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.GcxUserDao;
import org.ccci.gcx.idm.core.service.MailService;
import org.ccci.gcx.idm.core.util.RandomPasswordGenerator;
import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;

/**
 * <b>AbstractGcxUserService</b> contains common functionality for all concrete implementations of
 * {@link GcxUserService}.
 *
 * @author Greg Crider  Oct 21, 2008  1:14:00 PM
 */
public abstract class AbstractGcxUserService extends AbstractAuditableService
{
    protected static final Log log = LogFactory.getLog( AbstractGcxUserService.class ) ;

    /** DN for admin group */
    private String m_AdminGroupDN = null ;
    /** Random password generator */
    private RandomPasswordGenerator m_RandomPasswordGenerator = null ;
    /** Length of newly generated password */
    private int m_NewPasswordLength = 0 ;
    /** Template used to send new password e-mail */
    private MailSenderTemplate m_NewPasswordTemplate = null ;
    /** Message source for generating e-mail messages */
    private MessageSource m_MessageSource = null ;
    /** Reply to e-mail address */
    private String m_ReplyTo = null ;
    /** GCX login link */
    private String m_GcxLoginURL = null ;
    /** Template used to send activation e-mail */
    private MailSenderTemplate m_ActivationTemplate = null ;
    /** GCX activation link */
    private String m_GcxActivationURL = null ;
    /** Locale lookup */
    private Map<String, Locale> m_Locale = null ;
    
    
    /**
     * Return the maximum number of allowed results.
     * 
     * @return Number of maximum allowed search results or 0 (<tt>SEARCH_NO_LIMIT</tt>) if
     *         there is no imposed limit.
     */
    public int getMaxSearchResults()
    {
        return this.getGcxUserDao().getMaxSearchResults() ;
    }

    
    /**
     * @return the adminGroupDN
     */
    public String getAdminGroupDN()
    {
        return this.m_AdminGroupDN ;
    }
    /**
     * @param a_adminGroupDN the adminGroupDN to set
     */
    public void setAdminGroupDN( String a_adminGroupDN )
    {
        this.m_AdminGroupDN = a_adminGroupDN ;
    }


    /**
     * @return the randomPasswordGenerator
     */
    public RandomPasswordGenerator getRandomPasswordGenerator()
    {
        return this.m_RandomPasswordGenerator ;
    }
    /**
     * @param a_randomPasswordGenerator the randomPasswordGenerator to set
     */
    public void setRandomPasswordGenerator( RandomPasswordGenerator a_randomPasswordGenerator )
    {
        this.m_RandomPasswordGenerator = a_randomPasswordGenerator ;
    }


    /**
     * @return the newPasswordLength
     */
    public int getNewPasswordLength()
    {
        return this.m_NewPasswordLength ;
    }
    /**
     * @param a_newPasswordLength the newPasswordLength to set
     */
    public void setNewPasswordLength( int a_newPasswordLength )
    {
        this.m_NewPasswordLength = a_newPasswordLength ;
    }


    /**
     * @return the newPasswordTemplate
     */
    public MailSenderTemplate getNewPasswordTemplate()
    {
        return this.m_NewPasswordTemplate ;
    }
    /**
     * @param a_newPasswordTemplate the newPasswordTemplate to set
     */
    public void setNewPasswordTemplate( MailSenderTemplate a_newPasswordTemplate )
    {
        this.m_NewPasswordTemplate = a_newPasswordTemplate ;
    }


    /**
     * @return the messageSource
     */
    public MessageSource getMessageSource()
    {
        return this.m_MessageSource ;
    }
    /**
     * @param a_messageSource the messageSource to set
     */
    public void setMessageSource( MessageSource a_messageSource )
    {
        this.m_MessageSource = a_messageSource ;
    }


    /**
     * @return the replyTo
     */
    public String getReplyTo()
    {
        return this.m_ReplyTo ;
    }
    /**
     * @param a_replyTo the replyTo to set
     */
    public void setReplyTo( String a_replyTo )
    {
        this.m_ReplyTo = a_replyTo ;
    }


    /**
     * @return the gcxLoginURL
     */
    public String getGcxLoginURL()
    {
        return this.m_GcxLoginURL ;
    }
    /**
     * @param a_gcxLoginURL the gcxLoginURL to set
     */
    public void setGcxLoginURL( String a_gcxLoginURL )
    {
        this.m_GcxLoginURL = a_gcxLoginURL ;
    }


    /**
     * @return the activationTemplate
     */
    public MailSenderTemplate getActivationTemplate()
    {
        return this.m_ActivationTemplate ;
    }
    /**
     * @param a_activationTemplate the activationTemplate to set
     */
    public void setActivationTemplate( MailSenderTemplate a_activationTemplate )
    {
        this.m_ActivationTemplate = a_activationTemplate ;
    }


    /**
     * @return the gcxActivationURL
     */
    public String getGcxActivationURL()
    {
        return this.m_GcxActivationURL ;
    }
    /**
     * @param a_gcxActivationURL the gcxActivationURL to set
     */
    public void setGcxActivationURL( String a_gcxActivationURL )
    {
        this.m_GcxActivationURL = a_gcxActivationURL ;
    }


    /**
     * @return the locale
     */
    public Map<String, Locale> getLocale()
    {
        return this.m_Locale ;
    }
    /**
     * @param a_locale the locale to set
     */
    public void setLocale( Map<String, Locale> a_locale )
    {
        this.m_Locale = a_locale ;
    }


    /**
     * Convenience method to acquire the transitional {@link GcxUserDao}.
     * 
     * @return Transitional {@link GcxUserDao}.
     */
    protected GcxUserDao getTransitionalGcxUserDao()
    {
        return (GcxUserDao)this.getDao( Constants.BEAN_TRANS_GCXUSER_DAO ) ;
    }
    
    
    /**
     * Convenience method to acquire the {@link GcxUserDao}.
     * 
     * @return {@link GcxUserDao}.
     */
    protected GcxUserDao getGcxUserDao()
    {
        return (GcxUserDao)this.getDao( Constants.BEAN_GCXUSER_DAO ) ;
    }
    
    
    /**
     * Convenience method to acquire the {@link MailService}.
     * 
     * @return {@link MailService}
     */
    protected MailService getMailService()
    {
        return (MailService)this.getService( Constants.BEAN_MAIL_SERVICE ) ;
    }
    
    
    /**
     * Return appropriate {@link Locale} for the specified country code. If it is not
     * found, the default {@link Locale} is returned.
     * 
     * @param a_CountryCode Two letter country code (e.g., EN, ES) used for lookup.
     * 
     * @return {@link Locale} for country code, or default {@link Locale}.
     */
    protected Locale getLocaleByCountryCode( String a_CountryCode )
    {
        Locale result = Locale.getDefault() ;
        
        if ( this.getLocale() != null ) {
            if ( this.getLocale().containsKey( a_CountryCode ) ) {
                result = this.getLocale().get( a_CountryCode ) ;
            }
        }
        
        return result ;
    }
    
    
    /**
     * Validate the integrity of the specified {@link GcxUser} object. If there are
     * problems, such as those created when a user is manually updated outside of the
     * applications, we attempt to repair them here, and then save the new version.
     * 
     * @param a_GcxUser {@link GcxUser} to be validated and repaired.
     */
    protected void validateRepairUserIntegrity( GcxUser a_GcxUser )
    {
        if ( a_GcxUser != null ) {
            boolean changed = false ;
            GcxUser original = (GcxUser)a_GcxUser.clone() ;
        
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Validating user: " + a_GcxUser ) ;
        
            /*
             * If the user is not deactivated, but the e-mail and userid aren't equal, reset the userid
             * so that it matches the e-mail.
             */
            if ( 
                    ( !a_GcxUser.isDeactivated() ) &&
                    ( StringUtils.isNotBlank( a_GcxUser.getEmail() ) ) &&
                    ( !a_GcxUser.getEmail().equals( a_GcxUser.getUserid()) ) 
                ) { 
                a_GcxUser.setUserid( a_GcxUser.getEmail() ) ;
                changed = true ;
            }
        
            // Was the user object changed? If so, we need to save it and audit the change
            if ( changed ) {
                /*= WARN =*/ if ( log.isWarnEnabled() ) log.warn( "The user failed the integrity test and was modified\n\t:user: " + a_GcxUser ) ;
                this.getGcxUserDao().update( a_GcxUser ) ;
                // Audit the change
                this.getAuditService().update( 
                        Constants.INTERNAL_SOURCE, Constants.INTERNAL_CREATEDBY, a_GcxUser.getEmail(), 
                        "Repairing GCX User integrity", 
                        original,
                        a_GcxUser
                        ) ;
            }
        }
    }
    
    
    /**
     * Validate the {@link List} of {@link GcxUser} objects. If an object has
     * a problem, such as those created when a user is manually updated outside of the
     * applications, we attempt to repair them here, and then save the new version.
     * 
     * @param a_Users {@link List} of {@link GcxUser} objects to be validated and repaired.
     */
    protected void validateRepairUserIntegrity( List<GcxUser> a_Users )
    {
        if ( !CollectionUtils.isEmpty( a_Users ) ) {
            for( int i=0; i<a_Users.size(); i++ ) {
                this.validateRepairUserIntegrity( a_Users.get( i ) ) ;
            }
        }
    }
}
