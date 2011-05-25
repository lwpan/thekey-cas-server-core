package org.ccci.gto.mail.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gto.mail.TemplateMessagePreparator;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Daniel Frett
 */
public abstract class AbstractTemplateMailSender implements MailSender {
    protected final Log log = LogFactory.getLog(getClass());

    /* JavaMailSender implementation */
    protected JavaMailSender mailSender;

    /**
     * @return a new TemplateMessagePreparator for the specified template and
     *         message
     */
    protected abstract TemplateMessagePreparator getTemplatePreparator();

    /**
     * Send an e-mail, using the specified template.
     * 
     * @param template
     *            Template to be used in the construction of the e-mail message.
     * @param message
     *            Contains, among other things, the data to be plugged into the
     *            e-mail template.
     */
    public void send(final MailSenderTemplate template,
	    final OutgoingMailMessage message) {
	final TemplateMessagePreparator preparator = this
		.getTemplatePreparator();

	// Put the necessary information into the preparator
	preparator.setTemplate(template);
	preparator.setMessage(message);

	// Send the e-mail
	if (log.isInfoEnabled()) {
	    log.info("Sending e-mail to \"" + message.getTo() + "\"");
	}
	this.getMailSender().send(preparator);
    }

    /**
     * @return the javaMailSender
     */
    public JavaMailSender getMailSender() {
	return this.mailSender;
    }

    /**
     * @param mailSender
     *            the javaMailSender to set
     */
    public void setMailSender(final JavaMailSender mailSender) {
	this.mailSender = mailSender;
    }
}
