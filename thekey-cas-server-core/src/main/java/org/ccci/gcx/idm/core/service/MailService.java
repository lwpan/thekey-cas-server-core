package org.ccci.gcx.idm.core.service;

import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gcx.idm.common.service.BusinessService;

/**
 * <b>MailService</b> is used to send outgoing e-mail messages.
 *
 * @author Greg Crider  December 1, 2008  3:51:42 PM
 */
public interface MailService extends BusinessService
{
    
    /**
     * Send an e-mail using the specified template and model object to the recipient contained within
     * the {@link OutgoingMailMessage} object.
     * 
     * @param a_MailSenderTemplate Template used to create the message.
     * @param a_OutgoingMailMessage Recipient information.
     */
    public void send( MailSenderTemplate a_MailSenderTemplate, OutgoingMailMessage a_OutgoingMailMessage ) ;

}
