package org.ccci.gcx.idm.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.GcxUserAccountLockedException;
import org.ccci.gcx.idm.core.GcxUserAuthenticationErrorException;
import org.ccci.gcx.idm.core.GcxUserException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.GcxUserDao;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.core.util.RandomGUID;

/**
 * <b>GcxUserServiceImplTest</b>
 *
 * @author Greg Crider  Oct 22, 2008  6:00:30 PM
 */
public class GcxUserServiceImplTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( GcxUserServiceImplTest.class ) ;
    
    
    protected GcxUser createDummyUser()
    {
        GcxUser result = new GcxUser() ;
        
        SimpleDateFormat sdf = new SimpleDateFormat( "-yyyyMMdd_HH_mm_ss_S" ) ;
        Date currentDate = new Date() ;
        String email = "gcrider-".concat( sdf.format( currentDate ) ).concat( "@me.com" ) ;
        RandomGUID guid = new RandomGUID( true ) ;
        
        result.setCreateDate( currentDate ) ;
        result.setEmail( email ) ;
        result.setGUID( guid ) ;
        result.setPassword( "originalpassword" ) ;
        result.setFirstName( "Greg" ) ;
        result.setLastName( "Crider" ) ;
        result.setForcePasswordChange( true ) ;
        result.setLocked( false ) ;
        result.setLoginDisabled( false ) ;
        result.setLoginTime( null ) ;
        result.setPasswordAllowChange( true ) ;
        
        return result ;
    }

    
    public void testCreate()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testCreate" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;

        GcxUser user = this.createDummyUser() ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
        
        service.createTransitionalUser( user, "test" ) ;

        Assert.assertNotNull( "Did not successfully save the new user", user.getId() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Verify that we can find the new user" ) ;
        
        GcxUser user2 = service.findTransitionalUserByEmail( user.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User2: " + user2 ) ;
        
        Assert.assertEquals( "Unable to find newly created transitional user", true, user.getEmail().equals( user2.getEmail() ) ) ;
        
        boolean userExistsError = false ;
        try {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to save an existing user" ) ;
            service.createTransitionalUser( user, "test" ) ;
        } catch ( GcxUserException gue ) {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Succeeded in catching an attempt to save a user that already exists" ) ;
            userExistsError = true ;
        }
        
        Assert.assertEquals( "Did not detect the existing user", true, userExistsError ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testCreate" ) ;
    }
    
    
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
    
    
    public void testActivation()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testActivation" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;
        
        GcxUser user = this.createDummyUser() ;
        user.setEmail( "greg.crider@emergingdigital.com" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
        
        service.createTransitionalUser( user, "test" ) ;

        Assert.assertNotNull( "Did not successfully save the new user", user.getId() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to activate user" ) ;
        service.activateTransitionalUser( user, "test", user.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to authenticate user" ) ;
        service.authenticate( user ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_DAO ) ;
        GcxUser user1 = dao.findByEmail( user.getEmail() ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user1 ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testActivation" ) ;
    }
    
    
    public void testDeactivation()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testDeactivation" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;

        GcxUser user = this.createDummyUser() ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
        
        service.createTransitionalUser( user, "test" ) ;

        Assert.assertNotNull( "Did not successfully save the new user", user.getId() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to activate user" ) ;
        service.activateTransitionalUser( user, "test", user.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to authenticate user" ) ;
        service.authenticate( user ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to deactivate user" ) ;
        service.deactivateUser( user, "test", user.getEmail() ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_DAO ) ;
        GcxUser user1 = dao.findByEmail( user.getEmail() ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user1 ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testDeactivation" ) ;
    }
    
    
    public void testReactivation()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testReactivation" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;

        GcxUser user = this.createDummyUser() ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
        
        service.createTransitionalUser( user, "test" ) ;

        Assert.assertNotNull( "Did not successfully save the new user", user.getId() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to activate user" ) ;
        service.activateTransitionalUser( user, "test", user.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to authenticate user" ) ;
        service.authenticate( user ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to deactivate user" ) ;
        service.deactivateUser( user, "test", user.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to reactivate user" ) ;
        service.reactivateUser( user, "test", user.getEmail() ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_DAO ) ;
        GcxUser user1 = dao.findByEmail( user.getEmail() ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user1 ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testReactivation" ) ;
    }
    
    
    public void testMerge()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testMerge" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;

        GcxUser primaryUser = this.createDummyUser() ;
        GcxUser mergedUser = this.createDummyUser() ;
        GcxUser mergedUser2 = this.createDummyUser() ;
        
        primaryUser.setEmail( "primary" + System.currentTimeMillis() + "@emergingdigital.com" ) ;
        primaryUser.setDomainsVisitedString( "www.emergingdigital.com capitals.nhl.com" ) ;
        mergedUser.setEmail( "dummy" + System.currentTimeMillis() + "@me.my" ) ;
        mergedUser.setDomainsVisitedString( "www.yahoo.com www.google.com " ) ;
        mergedUser2.setEmail( "dummy2" + System.currentTimeMillis() + "@me.my" ) ;
        mergedUser2.setDomainsVisitedString( "www.yahoo.com www.google.com www.emergingdigital.com capitals.nhl.com" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User:\n\tPrimary:" + primaryUser + "\n\tMerged: " + mergedUser + "\n\tMerged2: " + mergedUser2 ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
        
        service.createTransitionalUser( primaryUser, "test" ) ;
        service.createTransitionalUser( mergedUser, "test" ) ;
        service.createTransitionalUser( mergedUser2, "test" ) ;

        Assert.assertNotNull( "Did not successfully save the primary user", primaryUser.getId() ) ;
        Assert.assertNotNull( "Did not successfully save the merged user", mergedUser.getId() ) ;
        Assert.assertNotNull( "Did not successfully save the merged user 2", mergedUser2.getId() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to activate users" ) ;
        service.activateTransitionalUser( primaryUser, "test", primaryUser.getEmail() ) ;
        service.activateTransitionalUser( mergedUser, "test", mergedUser.getEmail() ) ;
        service.activateTransitionalUser( mergedUser2, "test", mergedUser2.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to deactivate merged user 2" ) ;
        service.deactivateUser( mergedUser2, "test", mergedUser2.getEmail() ) ;
        
        Assert.assertTrue( "Merged user 2 is not deactivated", mergedUser2.isDeactivated() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to merge users" ) ;
        service.mergeUsers( primaryUser, mergedUser, "test", primaryUser.getEmail() ) ;
        service.mergeUsers( primaryUser, mergedUser2, "test", primaryUser.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup users to verify merge" ) ;
        primaryUser = service.findUserByEmail( primaryUser.getEmail() ) ;
        // Lookup by e-mail even though it should be deactivated; it is still a unique value
        mergedUser = service.findUserByEmail( mergedUser.getEmail() ) ;
        
        Assert.assertTrue( "Merged user is not deactivated", mergedUser.isDeactivated() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Primary user: " + primaryUser ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testMerge" ) ;
    }

    
    public void testUpdate()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testUpdate" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;

        GcxUser user = this.createDummyUser() ;
        user.setLoginTime( new Date() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
        
        service.createTransitionalUser( user, "test" ) ;

        Assert.assertNotNull( "Did not successfully save the new user", user.getId() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to activate user" ) ;
        service.activateTransitionalUser( user, "test", user.getEmail() ) ;
        

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to activate user" ) ;
        user.setDomainsVisitedString( "www.modifiedsite.com www.addednew.com" ) ;
        user.setLoginDisabled( true ) ;
        
        service.updateUser( user, false, "test", "test" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to authenticate user" ) ;
        try {
            service.authenticate( user ) ;
            Assert.fail( "Authentication should have been disabled" ) ;
        } catch ( GcxUserAuthenticationErrorException e ) {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Successfully blocked login attempt so the update worked" ) ;
        }
        
        user.setPassword( "newpassword" ) ;
        user.setLoginDisabled( false ) ;
        
        service.updateUser( user, true, "test", "test" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to authenticate user with new password and login enabled" ) ;
        try {
            service.authenticate( user ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Successfully authenticated user" ) ;
        } catch ( GcxUserAccountLockedException e ) {
            Assert.fail( "Authentication should have passed" ) ;
        }
        

        GcxUser user1 = service.findUserByEmail( user.getEmail() ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user1 ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testUpdate" ) ;
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
