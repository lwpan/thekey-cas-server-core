package org.ccci.gto.mail.impl;

import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.Assert;

/**
 * <b>VelocityMimeMessagePreparator</b> is used to prepare multi-part MIME
 * messages for use with sending an e-mail. Two templates are required. One is
 * for the plaintext version, and the other is for the HTML version.
 * 
 * The preparator is agnostic to the actual content and format of the e-mail
 * message. The format is determined by the two templates, and the content is
 * driven by the injected data model.
 * 
 * @author Daniel Frett
 */
public class VelocityTemplateMessagePreparator extends
	AbstractTemplateMessagePreparator {
    /** Velocity engine */
    private VelocityEngine velocityEngine;

    /**
     * @return Returns the velocityEngine.
     */
    public VelocityEngine getVelocityEngine() {
	return this.velocityEngine;
    }

    /**
     * @param velocityEngine
     *            The velocityEngine to set.
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
	this.velocityEngine = velocityEngine;
    }

    /**
     * Prepare the multi-part MIME message.
     * 
     * @param message
     *            Message to prepare.
     */
    public void prepare(final MimeMessage message) throws Exception {
	final OutgoingMailMessage messageModel = this.getMessage();
	final MailSenderTemplate template = this.getTemplate();
	final VelocityEngine velocityEngine = this.getVelocityEngine();

	// Create e-mail header
	message.addFrom(InternetAddress.parse(messageModel.getReplyTo()));
	message.addRecipients(Message.RecipientType.TO,
		InternetAddress.parse(messageModel.getTo()));
	if (StringUtils.isNotEmpty(messageModel.getCc())) {
	    message.addRecipients(Message.RecipientType.CC,
		    InternetAddress.parse(messageModel.getCc()));
	}
	if (StringUtils.isNotEmpty(messageModel.getBcc())) {
	    message.addRecipients(Message.RecipientType.BCC,
		    InternetAddress.parse(messageModel.getBcc()));
	}
	message.setSubject(template.getSubject());

	// Generate the multipart message
	final MimeMultipart ma = new MimeMultipart("alternative");
	message.setContent(ma);

	// Create the plain text version of the e-mail
	final BodyPart plain = new MimeBodyPart();
	plain.setText(VelocityEngineUtils.mergeTemplateIntoString(
		velocityEngine, template.getPlainTextTemplate(),
		messageModel.getMessageContentModel()));
	ma.addBodyPart(plain);

	// Create the HTML version of the e-mail
	final BodyPart html = new MimeBodyPart();
	html.setContent(VelocityEngineUtils.mergeTemplateIntoString(
		velocityEngine, template.getHtmlTemplate(),
		messageModel.getMessageContentModel()), "text/html");

	// Attach the HTML to the message
	final Map<String, String> resources = messageModel
		.getResourceContentModel();
	if (resources != null && resources.size() > 0) {
	    // If there are resources, we need an additional MimeMultipart to
	    // hold the HTML and resources.
	    final BodyPart resourceParts = new MimeBodyPart();
	    ma.addBodyPart(resourceParts);

	    // Generate a related MimeMultipart that contains the html and
	    // attached resources
	    final MimeMultipart maHtml = new MimeMultipart("related");
	    maHtml.addBodyPart(html);
	    for (Entry<String, String> resource : resources.entrySet()) {
		this.attachClasspathResource(maHtml, resource.getKey(),
			resource.getValue());
	    }
	    resourceParts.setContent(maHtml);
	} else {
	    // no resources, so just add the html to the main MimeMultipart
	    ma.addBodyPart(html);
	}
    }

    /**
     * Attach the specified classpath resource to the message.
     * 
     * @param a_Msg
     *            Existing {@link MimeMessage}.
     * @param a_ResourcePath
     *            Path to resource
     * @param a_ResourceId
     *            Resource id used to identify resource in MIME body.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    protected void attachClasspathResource(MimeMultipart a_MimeMultipart,
	    String a_ResourcePath, String a_ResourceId) throws Exception {
	BodyPart resource = new MimeBodyPart();

	if (log.isDebugEnabled()) {
	    log.debug("Adding resources: Id(" + a_ResourceId + ") Path("
		    + a_ResourcePath + ")");
	}

	// Resource locator along classpath
	ClassPathResource cpr = new ClassPathResource(a_ResourcePath);
	// Acquire data source to resource
	DataSource fds = new FileDataSource(cpr.getFile());

	// Add resource
	resource.setDataHandler(new DataHandler(fds));
	resource.setHeader("Content-ID", "<" + a_ResourceId + ">");
	resource.setHeader("Content-Disposition", "inline; filename=\""
		+ a_ResourcePath + "\"");

	// Add resource to message
	a_MimeMultipart.addBodyPart(resource);
    }

    /**
     * Make sure this object has been completely configured.
     * 
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
	Assert.notNull(this.velocityEngine,
		"Must set the velocity engine property for "
			+ this.getClass().getName());
    }
}
