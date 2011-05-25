package org.ccci.gcx.idm.common.mail.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.mail.TemplateMessagePreparator;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * <b>MailSenderImpl</b> is a concrete implementation of {@link MailSender} that
 * uses {@link JavaMailSender}.
 * 
 * @author Greg Crider December 1, 2008 5:29:26 PM
 * @author Daniel Frett
 */
public class MailSenderImpl extends AbstractMailSender
{
    protected static Log log = LogFactory.getLog( MailSenderImpl.class ) ;

    /**
     * Send an e-mail, using the specified template.
     * 
     * @param template
     *            Template to be used in the construction of the e-mail message.
     * @param message
     *            Contains, among other things, the data to be plugged into the
     *            e-mail template.
     */
    @Override
    public synchronized void send(final MailSenderTemplate template,
	    final OutgoingMailMessage message) {
	final TemplateMessagePreparator preparator = this
		.getMessagePreparator();

	// Put the necessary information into the preparator
	preparator.setMailSenderTemplate(template);
	preparator.setOutgoingMailMessage(message);

	// Send the e-mail
	if (log.isInfoEnabled()) {
	    log.info("Sending e-mail to \"" + message.getTo() + "\"");
	}
	this.m_JavaMailSender.send(preparator);
    }
}
