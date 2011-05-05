package org.ccci.gcx.idm.common.mail.impl;

import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.mail.TemplateMessagePreparator;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;

/**
 * <b>AbstractMailSender</b> contains common functionality to be used by all concrete implementations
 * of {@link MailSender}.
 *
 * @author Greg Crider  December 1, 2008 5:27:19 PM
 */
public abstract class AbstractMailSender implements MailSender
{
    /** Message preparator used to build up e-mail */
    protected TemplateMessagePreparator m_MessagePreparator = null ;
    

    /**
     * @return the messagePreparator
     */
    public TemplateMessagePreparator getMessagePreparator()
    {
        return this.m_MessagePreparator ;
    }
    /**
     * @param a_messagePreparator the messagePreparator to set
     */
    public void setMessagePreparator( TemplateMessagePreparator a_messagePreparator )
    {
        this.m_MessagePreparator = a_messagePreparator ;
    }

    /**
     * Send an e-mail, using the specified template.
     * 
     * @param template
     *            Template to be used in the construction of the e-mail message.
     * @param message
     *            Contains, among other things, the data to be plugged into the
     *            e-mail template.
     */
    public abstract void send(MailSenderTemplate template,
	    OutgoingMailMessage message);
}
