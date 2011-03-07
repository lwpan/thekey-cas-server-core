package org.ccci.gcx.idm.core.persist.hibernate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.Audit;
import org.ccci.gcx.idm.core.model.type.impl.AuditActionTypeCode;
import org.ccci.gcx.idm.core.persist.AuditDao;
import org.springframework.util.Assert;

/**
 * <b>AuditDaoImplTest</b> is used to unit test the {@link AuditDaoImpl} class.
 *
 * @author Greg Crider  Oct 19, 2008  6:11:23 PM
 */
public class AuditDaoImplTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( AuditDaoImplTest.class ) ;

    
    public void testBasic()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testBasic" ) ;
        
        SimpleDateFormat sdf = new SimpleDateFormat( "$$yyyyMMdd_HH:mm:ss.S$$" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Acquire AuditDao" ) ;
        
        AuditDao dao = (AuditDao)this.getApplicationContext().getBean( Constants.BEAN_AUDIT_DAO ) ;
        
        Audit audit = new Audit() ;
        
        Date currentDate = new Date() ;
        String userid = "userid".concat( sdf.format( currentDate ) ) ;
        
        audit.setCreateDate( currentDate ) ;
        audit.setAuditActionTypeCode( AuditActionTypeCode.CREATE ) ;
        audit.setSource( "test" ) ;
        audit.setChangeDate( currentDate ) ;
        audit.setChangedBy( "gcrider" ) ;
        audit.setUserid( userid ) ;
        audit.setDescription( "Creating a new user" ) ;
        audit.setObjectType( "user" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to save: " + audit ) ;
        
        dao.save( audit ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Save complete" ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recover saved audit objects" ) ;
        
        List<Audit> auditList = dao.findAllByUserid( userid ) ;
        
        Assert.notNull( auditList, "No audit objects were recovered during test; persistence failed." ) ;
        
        Iterator<Audit> it = auditList.iterator() ;
        while( it.hasNext() ) {
            Audit recoveredAudit = it.next() ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recovered: " + recoveredAudit ) ;
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testBasic" ) ;
    }
}
