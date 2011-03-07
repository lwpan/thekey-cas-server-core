package org.ccci.gcx.idm.core.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;

/**
 * <b>RandomGUIDTest</b> is used to unit test and prove out the {@link RandomGUID}
 * utility.
 *
 * @author Greg Crider  Oct 21, 2008  6:38:40 PM
 */
public class RandomGUIDTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( RandomGUIDTest.class ) ;

    
    public void testBasic()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testBasic" ) ;
        
        Map<String, RandomGUID> guidSet = new HashMap<String, RandomGUID>() ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Testing non-Secure" ) ;
        
        for( int i=0; i<100; i++ ) {
            RandomGUID guid = new RandomGUID() ;
            guidSet.put( guid.toString(), guid ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** non-Secure GUID: " + guid ) ;
        }
        
        Assert.assertEquals( "Expecting 100 unique GUID's but found " + guidSet.size(), 100, guidSet.size() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Testing Secure" ) ;
        
        guidSet.clear() ;
        
        for( int i=0; i<100; i++ ) {
            RandomGUID guid = new RandomGUID( true ) ;
            guidSet.put( guid.toString(), guid ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Secure GUID: " + guid ) ;
        }
        
        Assert.assertEquals( "Expecting 100 unique GUID's but found " + guidSet.size(), 100, guidSet.size() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testBasic" ) ;
    }
}
