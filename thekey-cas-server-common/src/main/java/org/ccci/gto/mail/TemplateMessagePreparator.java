package org.ccci.gto.mail;

import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * <b>TemplateMessagePreparator</b> extends the basic definition of the
 * {@link MimeMessagePreparator} so that other, required data can be
 * encapsulated.
 * 
 * @author Daniel Frett
 */
public interface TemplateMessagePreparator extends MimeMessagePreparator {
    /**
     * Set the {@link OutgoingMailMessage} object which contains the recipient
     * information and data to plug into the template.
     * 
     * @param a_OutgoingMailMessage
     *            {@link OutgoingMailMessge} for recipient.
     */
    public void setMessage(OutgoingMailMessage message);

    /**
     * Set the {@link MailSenderTemplate} which contains the templates to be
     * used, as well as other mail template meta data.
     * 
     * @param a_MailSenderTemplate
     *            {@link MailSenderTemplate} to be used in constructing the
     *            e-mail message.
     */
    public void setTemplate(MailSenderTemplate template);
}
