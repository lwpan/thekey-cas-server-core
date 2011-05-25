package org.ccci.gcx.idm.common.mail.impl;

import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.mail.TemplateMessagePreparator;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * <b>AbstractMailSender</b> contains common functionality to be used by all concrete implementations
 * of {@link MailSender}.
 *
 * @author Greg Crider  December 1, 2008 5:27:19 PM
 */
public abstract class AbstractMailSender implements MailSender
{
    /** JavaMailSender implementation */
    protected JavaMailSender m_JavaMailSender = null;

    /** Message preparator used to build up e-mail */
    private TemplateMessagePreparator m_MessagePreparator;

    /**
     * @return the messagePreparator
     */
    protected TemplateMessagePreparator getMessagePreparator() {
	return this.m_MessagePreparator;
    }

    /**
     * @param a_messagePreparator the messagePreparator to set
     */
    public void setMessagePreparator( TemplateMessagePreparator a_messagePreparator )
    {
        this.m_MessagePreparator = a_messagePreparator ;
    }

    /**
     * @return the javaMailSender
     */
    public JavaMailSender getJavaMailSender() {
	return this.m_JavaMailSender;
    }

    /**
     * @param a_javaMailSender
     *            the javaMailSender to set
     */
    public void setJavaMailSender(JavaMailSender a_javaMailSender) {
	this.m_JavaMailSender = a_javaMailSender;
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
