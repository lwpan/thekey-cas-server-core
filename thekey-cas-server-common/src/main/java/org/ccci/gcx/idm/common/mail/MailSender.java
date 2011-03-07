package org.ccci.gcx.idm.common.mail;

import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;

/**
 * <b>MailSender</b> defines the functionality for an e-mail sender.
 *
 * @author Greg Crider  December 1, 2008  5:14:17 PM
 */
public interface MailSender
{

    /**
     * Send an e-mail, using the specified template.
     * 
     * @param a_MailSenderTemplate Template to be used in the construction of the e-mail message.
     * @param a_OutgoingMailMessage Contains, among other things, the data to be plugged into the
     *        e-mail template.
     */
    public void send( MailSenderTemplate a_MailSenderTemplate, OutgoingMailMessage a_OutgoingMailMessage ) ;
    
}
