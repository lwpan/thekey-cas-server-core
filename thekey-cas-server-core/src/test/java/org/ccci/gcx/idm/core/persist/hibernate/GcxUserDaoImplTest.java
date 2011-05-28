package org.ccci.gcx.idm.core.persist.hibernate;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.ccci.gto.cas.util.RandomGUID;
import org.springframework.util.Assert;

/**
 * <b>GcxUserDaoImplTest</b> is used to unit test the {@link GcxUserDaoImpl}
 * class.
 *
 * @author Greg Crider  Oct 21, 2008  1:58:31 PM
 */
public class GcxUserDaoImplTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( GcxUserDaoImplTest.class ) ;

    
    public void testBasic()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testBasic" ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_TRANS_GCXUSER_DAO ) ;
        
        SimpleDateFormat sdf = new SimpleDateFormat( "$$yyyyMMdd_HH:mm:ss.S$$" ) ;
        Date currentDate = new Date() ;
        String email = "gcrider-".concat( sdf.format( currentDate ) ).concat( "@me.com" ) ;

        GcxUser user = new GcxUser() ;
	user.setGUID(RandomGUID.generateGuid(true));
        
        user.setCreateDate( currentDate ) ;
        user.setEmail( email ) ;
        user.setPassword( "originalpassword" ) ;
        user.setFirstName( "Greg" ) ;
        user.setLastName( "Crider" ) ;
        user.setForcePasswordChange( true ) ;
        user.setLocked( false ) ;
        user.setLoginDisabled( false ) ;
        user.setLoginTime( null ) ;
        user.setPasswordAllowChange( true ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to save: " + user ) ;
        
        dao.save( user ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Save complete" ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recover saved user object by GUID" ) ;
        
        GcxUser userRecovered = dao.findByGUID( user.getGUID() ) ;
        
        Assert.notNull( userRecovered, "Unable to locate the user by GUID; persistence failed." ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recovered: " + userRecovered ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recover saved user object by e-mail" ) ;
        
        userRecovered = dao.findByEmail( email ) ;
        
        Assert.notNull( userRecovered, "Unable to locate user by e-mail; persistence failed." ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Recovered: " + userRecovered ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testBasic" ) ;
    }

}
