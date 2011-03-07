package org.ccci.gcx.idm.common.mail.impl;

import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.mail.JavaMailSender;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;

/**
 * <b>MailSenderImpl</b> is a concrete implementation of {@link MailSender} that
 * uses {@link JavaMailSender}.
 *
 * @author Greg Crider  December 1, 2008  5:29:26 PM
 */
public class MailSenderImpl extends AbstractMailSender
{
    protected static Log log = LogFactory.getLog( MailSenderImpl.class ) ;
    
    /** JavaMailSender implementation */
    protected JavaMailSender m_JavaMailSender = null ;
    

    /**
     * @return the javaMailSender
     */
    public JavaMailSender getJavaMailSender()
    {
        return this.m_JavaMailSender ;
    }
    /**
     * @param a_javaMailSender the javaMailSender to set
     */
    public void setJavaMailSender( JavaMailSender a_javaMailSender )
    {
        this.m_JavaMailSender = a_javaMailSender ;
    }


    /**
     * Send an e-mail, using the specified template.
     * 
     * @param a_Template Template to be used in the construction of the e-mail message.
     * @param a_OutgoingMailMessage Contains, among other things, the data to be plugged into the
     *        e-mail template.
     */
    public void send( MailSenderTemplate a_MailSenderTemplate, OutgoingMailMessage a_OutgoingMailMessage )
    {
        super.send( a_MailSenderTemplate, a_OutgoingMailMessage ) ;
        
        // Put the necessary information into the preparator
        this.getMessagePreparator().setMailSenderTemplate( a_MailSenderTemplate ) ;
        this.getMessagePreparator().setOutgoingMailMessage( a_OutgoingMailMessage ) ;
        
        // Send the e-mail
        /*= INFO =*/ log.info( "Sending e-mail to \"" + a_OutgoingMailMessage.getTo() + "\"" ) ;
        
         this.m_JavaMailSender.send( this.getMessagePreparator() ) ;
 
    }

}
