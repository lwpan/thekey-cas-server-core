package org.ccci.gcx.idm.common.mail.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;

/**
 * @author Ken Burcham  December 4, 2008 - based on Greg's real MailSenderImpl  
 */
public class DummyMailSenderImpl extends AbstractMailSender
{
    protected static Log log = LogFactory.getLog( DummyMailSenderImpl.class ) ;
    
    /**
     * Send an e-mail, using the specified template.
     * 
     * @param a_Template Template to be used in the construction of the e-mail message.
     * @param a_OutgoingMailMessage Contains, among other things, the data to be plugged into the
     *        e-mail template.
     */
    public void send( MailSenderTemplate a_MailSenderTemplate, OutgoingMailMessage a_OutgoingMailMessage )
    {
        log.warn("Dummy mail sender is doing nothing. (since that's what Dummy mail senders do.");
    }

}
