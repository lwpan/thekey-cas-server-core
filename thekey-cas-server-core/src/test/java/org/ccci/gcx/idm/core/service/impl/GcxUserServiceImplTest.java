package org.ccci.gcx.idm.core.service.impl;

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
}
