package org.ccci.gcx.idm.core.service.impl;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gcx.idm.core.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>MailServiceImpl</b> is the concrete implementation of {@link MailService}.
 * 
 * @author Daniel Frett
 */
public class MailServiceImpl implements MailService {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** MailSender used for outgoing e-mail */
    @NotNull
    private MailSender mailSender;

    /**
     * @return the mailSender
     */
    private MailSender getMailSender() {
	return this.mailSender;
    }

    /**
     * @param mailSender
     *            the mailSender to set
     */
    public void setMailSender(MailSender mailSender) {
	this.mailSender = mailSender;
    }

    /**
     * Send an e-mail using the specified template and model object to the
     * recipient contained within the {@link OutgoingMailMessage} object.
     * 
     * @param template
     *            Template used to create the message.
     * @param message
     *            Recipient information.
     */
    public void send(MailSenderTemplate template, OutgoingMailMessage message) {
	if (log.isInfoEnabled()) {
	    String sep = System.getProperty("line.separator");
	    StringBuffer msg = new StringBuffer();
	    msg.append("Attempting to send message:").append(sep)
		    .append("\tTo: ").append(message.getTo()).append(sep)
		    .append("\tCC: ").append(message.getCc()).append(sep)
		    .append("\tBCC: ").append(message.getBcc()).append(sep)
		    .append("\tTemplate: HTML(")
		    .append(template.getHtmlTemplate()).append(") Plain(")
		    .append(template.getPlainTextTemplate()).append(")");
	    log.info(msg.toString());
	}

	// Send the e-mail
	this.getMailSender().send(template, message);
    }
}
