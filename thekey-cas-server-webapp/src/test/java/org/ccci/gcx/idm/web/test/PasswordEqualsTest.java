package org.ccci.gcx.idm.web.test;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

public class PasswordEqualsTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( PasswordEqualsTest.class ) ;

    
    public void testEquals()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testEquals" ) ;
        
        Filter passwordFilter = new EqualsFilter( Constants.LDAP_KEY_PASSWORD, "Kendog" ) ;
        System.out.println("Encoded 'Kendog' = "+passwordFilter.encode());
        
        passwordFilter = new EqualsFilter( Constants.LDAP_KEY_PASSWORD, "Kendog!" ) ;
        System.out.println("Encoded 'Kendog!' = "+passwordFilter.encode());

        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testEquals" ) ;
    }
}
