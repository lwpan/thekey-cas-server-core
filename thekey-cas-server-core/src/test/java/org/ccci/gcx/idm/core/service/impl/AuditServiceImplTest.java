package org.ccci.gcx.idm.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.Audit;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.AuditService;
import org.ccci.gto.cas.persist.AuditDao;
import org.springframework.util.Assert;

/**
 * <b>AuditServiceImplTest</b> is used to unit test the {@link AuditServiceImpl} class.
 *
 * @author Greg Crider  Oct 19, 2008  9:27:15 PM
 */
public class AuditServiceImplTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( AuditServiceImplTest.class ) ;

    
    public void testCreate()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testCreate" ) ;
        
        SimpleDateFormat sdf = new SimpleDateFormat( "$$yyyyMMdd_HH:mm:ss.S$$" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Acquire AuditService" ) ;
        
        AuditService service = (AuditService)this.getApplicationContext().getBean( Constants.BEAN_AUDIT_SERVICE ) ;
        Date currentDate = new Date() ;
        String userid = "userid".concat( sdf.format( currentDate ) ) ;
        
        GcxUser user = new GcxUser() ;

        service.create( "test", "gcrider", userid, "Test for creating a new audit using createGeneric method", user ) ;
        
        // TODO: Verify insert into db
        
        AuditDao dao = (AuditDao)this.getApplicationContext().getBean( Constants.BEAN_AUDIT_DAO ) ;

        List<Audit> auditList = dao.findAllByUserid( userid ) ;
        
        Assert.notNull( auditList, "No audit objects were recovered during test; persistence failed." ) ;
        
        Iterator<Audit> it = auditList.iterator() ;
        while( it.hasNext() ) {
            Audit recoveredAudit = it.next() ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recovered: " + recoveredAudit ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testCreate" ) ;
    }
    
    
    public void testUpdate()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testUpdate" ) ;
        
        SimpleDateFormat sdf = new SimpleDateFormat( "$$yyyyMMdd_HH:mm:ss.S$$" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Acquire AuditService" ) ;
        
        AuditService service = (AuditService)this.getApplicationContext().getBean( Constants.BEAN_AUDIT_SERVICE ) ;
        Date currentDate = new Date() ;
        String userid = "userid".concat( sdf.format( currentDate ) ) ;
        
        GcxUser userOriginal = new GcxUser() ;
        GcxUser userCurrent = new GcxUser() ;
        
        userOriginal.setEmail( "orignal@my.com" ) ;
        userOriginal.setGUID( "0000000000" ) ;
        userOriginal.setPassword( "originalpassword" ) ;
        
        userCurrent.setEmail( "current@my.com" ) ;
        userCurrent.setGUID( "1111111111" ) ;
        userCurrent.setPassword( "currentpassword" ) ;
        
        service.update( "test", "gcrider", userid, "Test for updating a GCX user", userOriginal, userCurrent ) ;
        
        // TODO: Verify insert into db
        
        AuditDao dao = (AuditDao)this.getApplicationContext().getBean( Constants.BEAN_AUDIT_DAO ) ;

        List<Audit> auditList = dao.findAllByUserid( userid ) ;
        
        Assert.notNull( auditList, "No audit objects were recovered during test; persistence failed." ) ;
        
        Iterator<Audit> it = auditList.iterator() ;
        while( it.hasNext() ) {
            Audit recoveredAudit = it.next() ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recovered: " + recoveredAudit ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testUpdate" ) ;
    }
    
}
