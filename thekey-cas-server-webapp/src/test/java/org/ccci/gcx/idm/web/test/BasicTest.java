package org.ccci.gcx.idm.web.test;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;

public class BasicTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( BasicTest.class ) ;

    
    public void testBasic()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testBasic" ) ;
        
        String t_str = "ken";
        
        Assert.assertEquals( "expecting equals. ", "ken", t_str ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testBasic" ) ;
    }
}
