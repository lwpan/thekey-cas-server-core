package org.ccci.gcx.idm.common.mail;

/**
 * <b>JavaMailSender</b> extend the Spring based {@link JavaMailSender} to add setters
 * for various properties.
 *
 * @author Greg Crider  December 1, 2008 6:24:26 PM
 */
public interface JavaMailSender extends org.springframework.mail.javamail.JavaMailSender
{

    /**
     * Set the mail server host, typically an SMTP host.
     * 
     * @param a_Host Host name.
     */
    public void setHost( String a_Host ) ;

}
