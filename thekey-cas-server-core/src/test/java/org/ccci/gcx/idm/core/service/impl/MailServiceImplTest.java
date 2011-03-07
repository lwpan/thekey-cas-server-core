package org.ccci.gcx.idm.core.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.mail.MailSenderTemplate;
import org.ccci.gcx.idm.common.model.impl.OutgoingMailMessage;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.MailService;
import org.springframework.context.MessageSource;

/**
 * <b>MailServiceImplTest</b> is used to unit test the {@link MailService} implementation.
 *
 * @author Greg Crider  Dec 1, 2008  5:01:30 PM
 */
public class MailServiceImplTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( GcxUserServiceImplTest.class ) ;

    
    public void testNewPassword()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testNewPassword" ) ;
        
        MailService service = (MailService)this.getApplicationContext().getBean( Constants.BEAN_MAIL_SERVICE ) ;
        MailSenderTemplate template = (MailSenderTemplate)this.getApplicationContext().getBean( "mail.templateNewPassword" ) ;
        MessageSource resource = (MessageSource)this.getApplicationContext().getBean( Constants.BEAN_MESSAGE_SOURCE ) ;
        
        GcxUser user = new GcxUser() ;
        user.setPassword( "dummypassword" ) ;
        
        Locale locale = Locale.getDefault() ;
        
        Map<String, Object> model = new HashMap<String, Object>() ;
        
        model.put( "title",          resource.getMessage( "newpassword.title", null, "?", locale ) ) ;
        model.put( "body",           resource.getMessage( "newpassword.body", null, "?", locale ) ) ;
        model.put( "passwordlabel",  resource.getMessage( "newpassword.passwordlabel", null, "?", locale ) ) ;
        model.put( "loginlinklabel", resource.getMessage( "newpassword.loginlinklabel", null, "?", locale ) ) ;
        model.put( "user",           user ) ;
        model.put( "loginlink",      "https://signin.mygcx.org/sso/login/login.jsp" ) ;
        
        OutgoingMailMessage message = new OutgoingMailMessage() ;
        message.setTo( "gcrider@emergingdigital.com" ) ;
        message.setReplyTo( "gcrider@emergingdigital.com" ) ;
        message.setMessageContentModel( model ) ;
                
        service.send( template, message ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testNewPassword" ) ;
    }

    
    public void testActivation()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testActivation" ) ;
        
        MailService service = (MailService)this.getApplicationContext().getBean( Constants.BEAN_MAIL_SERVICE ) ;
        MailSenderTemplate template = (MailSenderTemplate)this.getApplicationContext().getBean( "mail.templateActivation" ) ;
        MessageSource resource = (MessageSource)this.getApplicationContext().getBean( Constants.BEAN_MESSAGE_SOURCE ) ;
        
        GcxUser user = new GcxUser() ;
        user.setEmail( "gcrider@emergingdigital.com" ) ;
        
        Locale locale = Locale.getDefault() ;
        
        StringBuffer activationURL = new StringBuffer() ;
        activationURL.append( "https://cas1.mygcx.org/sso/activate.htm?k=" )
                     .append( user.getEmail() )
                     ;
        
        Map<String, Object> model = new HashMap<String, Object>() ;
        
        model.put( "title",               resource.getMessage( "activation.title", null, "?", locale ) ) ;
        model.put( "body",                resource.getMessage( "activation.body", null, "?", locale ) ) ;
        model.put( "activationlinklabel", resource.getMessage( "activation.activationlinklabel", null, "?", locale ) ) ;
        model.put( "user",                user ) ;
        model.put( "activationlink",      activationURL.toString() ) ;
        
        OutgoingMailMessage message = new OutgoingMailMessage() ;
        message.setTo( "gcrider@emergingdigital.com" ) ;
        message.setReplyTo( "gcrider@emergingdigital.com" ) ;
        message.setMessageContentModel( model ) ;
                
        service.send( template, message ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testActivation" ) ;
    }
}
