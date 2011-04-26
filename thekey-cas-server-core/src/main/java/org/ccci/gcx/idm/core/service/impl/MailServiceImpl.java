package org.ccci.gcx.idm.core.service.impl;

import org.ccci.gcx.idm.common.mail.MailSender;
import org.ccci.gcx.idm.common.spring2.ApplicationContext;
import org.ccci.gcx.idm.core.Constants;


/**
 * <b>MailServiceImpl</b> is the concrete implementation of {@link AbstractMailService}.
 *
 * @author Greg Crider  Nov 28, 2007  4:13:27 PM
 */
public class MailServiceImpl extends AbstractMailService
{

    /**
     * Reacquire the mail sender bean from the context.
     * 
     * @see org.ntca.common.service.impl.AbstractMailService#reacquireMailSender()
     */
    public void reacquireMailSender()
    {
        this.setMailSender( (MailSender)ApplicationContext.getObject( Constants.BEAN_MAIL_SENDER ) ) ;
    }

}
