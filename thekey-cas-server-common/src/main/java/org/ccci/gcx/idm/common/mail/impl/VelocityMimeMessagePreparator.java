package org.ccci.gcx.idm.common.mail.impl;

import java.util.Iterator;
import java.util.Map;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.Assert;

/**
 * <b>VelocityMimeMessagePreparator</b> is used to prepare multi-part MIME
 * messages for use with sending an e-mail. Two templates are required. One
 * is for the plaintext version, and the other is for the HTML version.
 * 
 * The preparator is agnostic to the actual content and format of the e-mail
 * message. The format is determined by the two templates, and the content
 * is driven by the injected data model.
 *
 * @author Greg Crider  December 1, 2008  2:14:13 PM
 */
public class VelocityMimeMessagePreparator extends AbstractTemplateMessagePreparator
{
    /** Velocity engine */
    protected VelocityEngine m_VelocityEngine = null ;
    
    
    /**
     * @return Returns the velocityEngine.
     */
    public VelocityEngine getVelocityEngine()
    {
        return this.m_VelocityEngine ;
    }
    /**
     * @param a_velocityEngine The velocityEngine to set.
     */
    public void setVelocityEngine( VelocityEngine a_velocityEngine )
    {
        this.m_VelocityEngine = a_velocityEngine ;
    }
    
    
    /**
     * Prepare the multi-part MIME message.
     * 
     * @param a_Msg Message to prepare.
     */
    public void prepare( MimeMessage a_Msg ) throws Exception
    {
        // Create e-mail header
        a_Msg.addFrom( InternetAddress.parse( this.getOutgoingMailMessage().getReplyTo() ) ) ;
        a_Msg.addRecipients( Message.RecipientType.TO, InternetAddress.parse( this.getOutgoingMailMessage().getTo() ) ) ;
        if ( StringUtils.isNotEmpty( this.getOutgoingMailMessage().getCc() ) ) {
            a_Msg.addRecipients( Message.RecipientType.CC, InternetAddress.parse( this.getOutgoingMailMessage().getCc() ) ) ;
        }
        if ( StringUtils.isNotEmpty( this.getOutgoingMailMessage().getBcc() ) ) {
            a_Msg.addRecipients( Message.RecipientType.BCC, InternetAddress.parse( this.getOutgoingMailMessage().getBcc() ) ) ;
        }
        a_Msg.setSubject( this.getMailSenderTemplate().getSubject() ) ;
        
        // Create multipart wrapper
        MimeMultipart ma = new MimeMultipart( "alternative" ) ;
        a_Msg.setContent( ma ) ;
        
        // Create the plain text version of the e-mail
        BodyPart plain = new MimeBodyPart() ;
        plain.setText( VelocityEngineUtils.mergeTemplateIntoString( 
                this.m_VelocityEngine, this.getMailSenderTemplate().getPlainTextTemplate(), 
                this.getOutgoingMailMessage().getMessageContentModel() ) ) ;
        ma.addBodyPart( plain ) ;

        // If there are images, we need an additional MimeMultipart to hold the HTML and
        // resources, otherwise, we will just add it to the existing one along with the
        // plain text
        boolean hasResources = false ;
        MimeMultipart maHtml = null ;
        if ( ( this.getOutgoingMailMessage().getResourceContentModel() != null ) &&
                ( this.getOutgoingMailMessage().getResourceContentModel().size() > 0 ) ) {
            // When including resources referenced by the HTML, the content type must be
            // related in order to instruct the client as to how to reference things
            hasResources = true ;
            maHtml = new MimeMultipart( "related" ) ;
        } else {
            // No images, so add to existing MimeMultipart
            maHtml = ma ;
        }
        
        // Create the HTML version of the e-mail
        BodyPart html = new MimeBodyPart() ;
        html.setContent( VelocityEngineUtils.mergeTemplateIntoString( 
                this.m_VelocityEngine, this.getMailSenderTemplate().getHtmlTemplate(), 
                this.getOutgoingMailMessage().getMessageContentModel() ), "text/html" ) ;
        maHtml.addBodyPart( html ) ;

        // Are resources present?
        if ( hasResources ) {
            // Attach any resource items if present; these go after the HTML content
            // added above
            Map<String,String> resources = this.getOutgoingMailMessage().getResourceContentModel() ;
            Iterator<String> it = resources.keySet().iterator() ;
            while( it.hasNext() ) {
                String id = it.next() ;
                String path = resources.get( id ) ;
                this.attachClasspathResource( maHtml, path, id ) ;
            }
            // Create a new BodyPart that will go into the primary MimeMultipart
            // container; this new BodyPart is a complete MimeMultipart in and
            // of itself
            BodyPart resourceParts = new MimeBodyPart() ;
            resourceParts.setContent( maHtml ) ;
            ma.addBodyPart( resourceParts ) ;
        }
    }
    

    /**
     * Attach the specified classpath resource to the message.
     * 
     * @param a_Msg Existing {@link MimeMessage}.
     * @param a_ResourcePath Path to resource
     * @param a_ResourceId Resource id used to identify resource in MIME body.
     * 
     * @throws Exception If an error occurs.
     */
    protected void attachClasspathResource( MimeMultipart a_MimeMultipart, String a_ResourcePath, String a_ResourceId ) throws Exception
    {
        BodyPart resource = new MimeBodyPart() ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "Adding resources: Id(" + a_ResourceId + ") Path(" + a_ResourcePath + ")" ) ;
        
        // Resource locator along classpath
        ClassPathResource cpr = new ClassPathResource( a_ResourcePath ) ;
        // Acquire data source to resource
        DataSource fds = new FileDataSource( cpr.getFile() ) ;
        
        // Add resource
        resource.setDataHandler( new DataHandler( fds ) ) ;
        resource.setHeader( "Content-ID", "<" + a_ResourceId + ">" ) ;
        resource.setHeader( "Content-Disposition", "inline; filename=\"" + a_ResourcePath + "\"" ) ;
        
        // Add resource to message
        a_MimeMultipart.addBodyPart( resource ) ;
    }
    
    
    /**
     * Make sure this object has been completely configured.
     * 
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull( this.m_VelocityEngine, "Must set the velocity engine property for " + this.getClass().getName() ) ;
    }
}
