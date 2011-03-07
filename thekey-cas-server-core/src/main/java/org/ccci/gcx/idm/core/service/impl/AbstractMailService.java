package org.ccci.gcx.idm.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gcx.idm.common.service.impl.AbstractDataAccessService;
import org.ccci.gcx.idm.core.service.MailService;

/**
 * <b>AbstractMailService</b> is the concrete implementation of {@link MailService}. This is an
 * abstract simply because some of the remaining implementation features will be specific to the
 * application using it.
 *
 * @author Greg Crider  December 1, 2008  3:52:20 PM
 */
public abstract class AbstractMailService extends AbstractDataAccessService implements MailService
{
    protected static final Log log = LogFactory.getLog( AbstractMailService.class ) ;

    /** MailSender used for outgoing e-mail */
    private MailSender m_MailSender = null ;

    
    /**
     * @return the mailSender
     */
    public MailSender getMailSender()
    {
        return this.m_MailSender ;
    }
    /**
     * @param a_mailSender the mailSender to set
     */
    public void setMailSender( MailSender a_mailSender )
    {
        this.m_MailSender = a_mailSender ;
    }
    

    /**
     * Typically, the underlying Java mail provider is not thread safe, and must
     * be reloaded each time an outgoing e-mail is required. The concrete implementation
     * of this method should re-instantiate the mail sender, or if using Spring, reload
     * it from the context; in this case, make sure the bean is not a singleton.
     */
    public abstract void reacquireMailSender() ;
    
    
    /**
     * Send an e-mail using the specified template and model object to the recipient contained within
     * the {@link OutgoingMailMessage} object.
     * 
     * @param a_MailSenderTemplate Template used to create the message.
     * @param a_OutgoingMailMessage Recipient information.
     */
    public void send( MailSenderTemplate a_MailSenderTemplate, OutgoingMailMessage a_OutgoingMailMessage )
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) {
            String sep = System.getProperty( "line.separator" ) ;
            StringBuffer msg = new StringBuffer() ;
            msg.append( "Attempting to send message:" )
               .append( sep )
               .append( "\tTo: " ).append( a_OutgoingMailMessage.getTo() ).append( sep )
               .append( "\tCC: " ).append( a_OutgoingMailMessage.getCc() ).append( sep )
               .append( "\tBCC: " ).append( a_OutgoingMailMessage.getBcc() ).append( sep )
               .append( "\tTemplate: HTML(" )
               .append( a_MailSenderTemplate.getHtmlTemplate() )
               .append( ") Plain(" )
               .append( a_MailSenderTemplate.getPlainTextTemplate() )
               .append( ")" )
               ;
                log.info( msg ) ;
        }
        
        // The mail sender is a prototype but this service is a singleton, so we can't use
        // IoC to wire it in; we must grab it each time we need it.
        this.reacquireMailSender() ;
        
        // Send the e-mail
        this.getMailSender().send( a_MailSenderTemplate, a_OutgoingMailMessage ) ;
    }

}
