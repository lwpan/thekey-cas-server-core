package org.ccci.gcx.idm.core.service.impl;

import static org.ccci.gcx.idm.core.Constants.BEAN_TRANS_GCXUSER_DAO;
import static org.ccci.gcx.idm.core.Constants.INTERNAL_CREATEDBY;
import static org.ccci.gcx.idm.core.Constants.INTERNAL_SOURCE;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.core.GcxUserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.ccci.gto.cas.util.RandomPasswordGenerator;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * <b>AbstractGcxUserService</b> contains common functionality for all concrete implementations of
 * {@link GcxUserService}.
 *
 * @author Greg Crider  Oct 21, 2008  1:14:00 PM
 */
public abstract class AbstractGcxUserService extends AbstractAuditableService
	implements GcxUserService {
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
    private String loginUri;
    /** Template used to send activation e-mail */
    private MailSenderTemplate m_ActivationTemplate = null ;
    
    @NotNull
    private GcxUserDao userDao;
    
    @NotNull
    private MailSender mailSender;

    /**
     * Return the maximum number of allowed results.
     * 
     * @return Number of maximum allowed search results or 0 (<tt>SEARCH_NO_LIMIT</tt>) if
     *         there is no imposed limit.
     */
    public int getMaxSearchResults()
    {
	return this.getUserDao().getMaxSearchResults();
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
	return this.loginUri;
    }

    /**
     * @param uri
     *            the login uri to use for The Key
     */
    public void setLoginUri(final String uri) {
	this.loginUri = uri;
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
     * Convenience method to acquire the transitional {@link GcxUserDao}.
     * 
     * @return Transitional {@link GcxUserDao}.
     */
    @Deprecated
    protected GcxUserDao getTransitionalGcxUserDao()
    {
	return (GcxUserDao) this.getDao(BEAN_TRANS_GCXUSER_DAO);
    }

    /**
     * @return the mailSender
     */
    protected MailSender getMailSender() {
	return mailSender;
    }

    /**
     * @param mailSender
     *            the mailSender to set
     */
    public void setMailSender(final MailSender mailSender) {
	this.mailSender = mailSender;
    }

    /**
     * @param userDao
     *            the userDao to use
     */
    public void setUserDao(final GcxUserDao userDao) {
	this.userDao = userDao;
    }

    /**
     * @return the userDao to use
     */
    protected GcxUserDao getUserDao() {
	return this.userDao;
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
		this.getUserDao().update(a_GcxUser);
                // Audit the change
		this.getAuditService().update(INTERNAL_SOURCE,
			INTERNAL_CREATEDBY, a_GcxUser.getEmail(),
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

    @Transactional(readOnly = true)
    public GcxUser getFreshUser(final GcxUser original)
	    throws GcxUserNotFoundException {
	Assert.notNull(original);
	// attempt retrieving the fresh object using the original object's guid
	final GcxUser fresh = this.findUserByGuid(original.getGUID());

	// throw an error if the guid wasn't found
	if (fresh == null) {
	    throw new GcxUserNotFoundException(
		    "Cannot find a fresh instance of the specified user");
	}

	// return the fresh user object
	return fresh;
    }
}
