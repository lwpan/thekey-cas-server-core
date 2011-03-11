package org.ccci.gcx.idm.core.service.impl;

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
 * @author Ken Burcham
 */

public class GcxUserLdapUpdateTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( GcxUserLdapUpdateTest.class ) ;
    
    
    
    // -- Background: we neeed to be able to modify a user that has a different UID+CN.
    public void testUpdateChangedUser()
    {
    	
    	String username="karin.tome@ccci.org";
    	String pw = "Philosophy02";
    	
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;
        
        GcxUser user = new GcxUser() ;
        
        user.setEmail( username ) ;
        user.setPassword( pw ) ;
        
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
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Find by email" ) ;
        
        try {
	    service.findUserByEmail(username);
        } catch (Exception e) {
        	 /*= ERROR =*/ log.error( "Unable to find user", e ) ;
             Assert.fail( "Unable to find user" ) ;
        }
        
    }
    
}