package org.ccci.gcx.idm.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;

/**
 * <b>GcxUserServiceImplTest</b>
 *
 * @author Greg Crider  Oct 22, 2008  6:00:30 PM
 */
public class GcxUserServiceImplTest extends AbstractTransactionalTestCase {
    protected static final Log log = LogFactory.getLog( GcxUserServiceImplTest.class ) ;

    public void testAuthentication()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testAuthentication" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;

        SimpleDateFormat sdf = new SimpleDateFormat( "$$yyyyMMdd_HH:mm:ss.S$$" ) ;
        Date currentDate = new Date() ;
        String email = "gcrider-".concat( sdf.format( currentDate ) ).concat( "@me.com" ) ;

        GcxUser user = new GcxUser() ;
        
        user.setEmail( "noup1" ) ;
        user.setPassword( "novell" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting valid login" ) ;

        try {
            service.authenticate( user ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Successfully authenticated user" ) ;
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Unable to authenticate", e ) ;
            Assert.fail( "Unable to authenticate user" ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting invalid password" ) ;
        
        try {
            user.setPassword( "junk123412341234" ) ;
            service.authenticate( user ) ;
            Assert.fail( "Should not have authenticated user with invalid password" ) ;
        } catch ( Exception e ) {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Successfully blocked authentication with invalid password" ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting invalid userid" ) ;
        
        try {
            user.setEmail( email ) ;
            service.authenticate( user ) ;
            Assert.fail( "Should not have authenticated user with invalid userid" ) ;
        } catch ( Exception e ) {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Successfully blocked authentication with invalid userid" ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testAuthentication" ) ;
    }

    public void testAdminGroup()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testAdminGroup" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;

        GcxUser user = service.findUserByEmail( "russ.licht@gmail.com" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Testing for admin user" ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user ) ;
        
        boolean isAdmin = service.isUserInAdminGroup( user ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User is admin: " + isAdmin ) ;
        
        Assert.assertTrue( "User should be admin", isAdmin ) ;

        user = service.findUserByEmail( "up1" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Testing for admin user" ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user ) ;
        
        isAdmin = service.isUserInAdminGroup( user ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User is admin: " + isAdmin ) ;
        
        Assert.assertFalse( "User should not be admin", isAdmin ) ;
        
        user = service.findUserByEmail( "gcrider@emergingdigital.com" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user + "\n\tAdmin: " + service.isUserInAdminGroup( user ) ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testAdminGroup" ) ;
    }
    
    
    public void testLookup()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testLookup" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by first name pattern" ) ;
        
        List<GcxUser> users = service.findAllByFirstName( "*uss" ) ;
        if ( users != null ) {
            for( int i=0; i<users.size(); i++ ) {
                /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** user[" + i + "]: " + users.get( i ) ) ;
            }
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by last name pattern" ) ;
        
        users = service.findAllByLastName( "*icht" ) ;
        if ( users != null ) {
            for( int i=0; i<users.size(); i++ ) {
                /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** user[" + i + "]: " + users.get( i ) ) ;
            }
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by e-mail pattern" ) ;
        
        try {
        users = service.findAllByEmail( "*ICH*" ) ;
        if ( users != null ) {
            for( int i=0; i<users.size(); i++ ) {
                /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** user[" + i + "]: " + users.get( i ).getEmail() ) ;
            }
        }
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Unable to do search", e ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testLookup" ) ;
    }
    
    
    public void testPageSortLookup()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testPageSortLookup" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by e-mail pattern" ) ;
        
        try {
            List<GcxUser> users = service.findAllByEmail( "*" ) ;
            if ( users != null ) {
                for( int i=0; i<users.size(); i++ ) {
                    /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** user[" + i + "]: " + users.get( i ).getEmail() ) ;
                }
            }
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Unable to do search", e ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testPageSortLookup" ) ;
    }
    
    
    public void testResetPassword()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testResetPassword" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Locating user" ) ; 
        
        GcxUser user = service.findUserByEmail( "gcrider@emergingdigital.com" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Restting user: " + user ) ;
        
        service.resetPassword( user, "test", user.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to authenticate with new password" ) ;
        
        service.authenticate( user ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testResetPassword" ) ;
    }
}
