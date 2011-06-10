package org.ccci.gcx.idm.web.admin.response.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.admin.response.impl.UserSearchResponse;

/**
 * <b>EdirUserSearchResponseTest</b>
 *
 * @author Greg Crider  Nov 17, 2008  2:36:31 PM
 */
public class EdirUserSearchResponseTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( EdirUserSearchResponseTest.class ) ;
    
    
    public void testBasic()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testBasic" ) ;
        
        GcxUserService service = (GcxUserService)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_SERVICE ) ;
        
        List<GcxUser> lookup = service.findAllByEmail( "*" ) ;
        
        UserSearchResponse response = new UserSearchResponse() ;
        response.setEntries( lookup ) ;
        response.setEntriesPerPage( 5 ) ;
        response.setPageNumber( 1 ) ;
        response.calculate() ;
        
        List<GcxUser> page = response.currentUsersPage() ;
        
        Iterator<GcxUser> it = page.iterator() ;
        while( it.hasNext() ) {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + it.next() ) ;
        }


        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testBasic" ) ;
    }

}
