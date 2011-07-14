package org.ccci.gcx.idm.common.mail.impl;

import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.mail.TemplateMessagePreparator;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>AbstractTemplateMessagePreparator</b> contains the common functionality needed
 * by concrete implementations of {@link TemplateMessagePreparator} objects.
 *
 * @author Greg Crider  December 1, 2008  5:57:23 PM
 */
public abstract class AbstractTemplateMessagePreparator implements TemplateMessagePreparator
{
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** Template to be used for e-mail construction. */
    protected MailSenderTemplate m_MailSenderTemplate = null ;
    /** Outgoing message information and data to plug into template. */
    protected OutgoingMailMessage m_OutgoingMailMessage = null ;
    
    
    /**
     * @return the mailSenderTemplate
     */
    public MailSenderTemplate getMailSenderTemplate()
    {
        return this.m_MailSenderTemplate ;
    }
    /**
     * @param a_mailSenderTemplate the mailSenderTemplate to set
     */
    public void setMailSenderTemplate( MailSenderTemplate a_mailSenderTemplate )
    {
        this.m_MailSenderTemplate = a_mailSenderTemplate ;
    }
    
    
    /**
     * @return the outgoingMailMessage
     */
    public OutgoingMailMessage getOutgoingMailMessage()
    {
        return this.m_OutgoingMailMessage ;
    }
    /**
     * @param a_outgoingMailMessage the outgoingMailMessage to set
     */
    public void setOutgoingMailMessage( OutgoingMailMessage a_outgoingMailMessage )
    {
        this.m_OutgoingMailMessage = a_outgoingMailMessage ;
    }
   
}
