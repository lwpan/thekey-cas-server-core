package org.ccci.gto.mail.impl;

import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gto.mail.TemplateMessagePreparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>AbstractTemplateMessagePreparator</b> contains the common functionality
 * needed by concrete implementations of {@link TemplateMessagePreparator}
 * objects.
 * 
 * @author Daniel Frett
 */
public abstract class AbstractTemplateMessagePreparator implements
	TemplateMessagePreparator {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /* Template to be used for e-mail construction. */
    private MailSenderTemplate template;

    /* Outgoing message information and data to plug into template. */
    private OutgoingMailMessage message;

    /**
     * @return the template
     */
    public MailSenderTemplate getTemplate() {
	return template;
    }

    /**
     * @param template
     *            the template to set
     */
    public void setTemplate(MailSenderTemplate template) {
	this.template = template;
    }

    /**
     * @return the message
     */
    public OutgoingMailMessage getMessage() {
	return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(OutgoingMailMessage message) {
	this.message = message;
    }
}
