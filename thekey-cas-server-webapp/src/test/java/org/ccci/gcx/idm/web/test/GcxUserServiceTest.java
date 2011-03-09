package org.ccci.gcx.idm.web.test;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.GcxUserDao;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.core.util.RandomGUID;

/**
 * <b>GcxUserServiceImplTest</b>
 *
 * @author Greg Crider  Oct 22, 2008  6:00:30 PM
 */
public class GcxUserServiceTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( GcxUserServiceTest.class ) ;
    
    
    protected GcxUser createDummyUser()
    {
        Date currentDate = new Date() ;
        RandomGUID guid = new RandomGUID( true ) ;

        GcxUser user = new GcxUser() ;
        
        user.setCreateDate( currentDate ) ;
        //user.setEmail( form.getUsername() ) ;
        user.setGUID( guid ) ;
        //user.setPassword( "changeme" ) ; JIRA: IDM-11 - gcxuserservice now provides a random password.
        user.setFirstName( "Fred" ) ;
        user.setLastName( "Flintstone" ) ;
        user.setForcePasswordChange( true ) ;
        user.setLocked( false ) ;
        user.setLoginDisabled( false ) ;
        user.setLoginTime( null ) ;
        user.setPasswordAllowChange( true ) ;
        user.setDomainsVisitedString("");
        user.setDomainsVisitedAdditionalString("");
        user.setGUIDAdditionalString("");
        return user;
    }

    
   
    
    public void testActivation()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testActivation" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;
        
        GcxUser user = this.createDummyUser() ;
        user.setEmail( "kenburcham@gmail.com" ) ;
        
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
        
        service.createTransitionalUser( user, "test" ) ;

        log.info("Password is temporary and set to: "+user.getPassword());

        
        Assert.assertNotNull( "Did not successfully save the new user", user.getId() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to activate user" ) ;
        service.activateTransitionalUser( user, "test", user.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to authenticate user" ) ;
        GcxUser authuser = service.findUserByEmail(user.getEmail()) ;
        authuser.setPassword(user.getPassword());
        //log.info(authuser.getUserid());
        //log.info(authuser.getEmail());
        //log.info(authuser.getPassword());
        //log.info(authuser.getGUID());
        log.info("Looking for: "+user.getEmail() + "  with password: "+ user.getPassword());
        
        service.authenticate( authuser ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_DAO ) ;
        GcxUser user1 = dao.findByEmail( user.getEmail() ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user1 ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testActivation" ) ;
        service.deleteUser(user, "test", "test");
    }
    
    
}
