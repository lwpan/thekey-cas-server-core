package org.ccci.gcx.idm.core.persist.ldap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.SortControl;
import javax.naming.ldap.SortResponseControl;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.GcxUserDao;
import org.ccci.gto.cas.util.RandomGUID;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

/**
 * <b>GcxUserDaoImplTest</b> is used to unit test the LDAP {@link GcxUserDao}.
 *
 * @author Greg Crider  Oct 24, 2008  6:07:49 PM
 */
public class GcxUserDaoImplTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( GcxUserDaoImplTest.class ) ;

    
    public void testBasic()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testBasic" ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_DAO ) ;

        GcxUser user  = null ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by GUID" ) ;
        
        user = dao.findByGUID( "83EC5114-CFC2-D5AA-E595-62E8639B61AD" ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User1: " + user ) ;
        
        user = dao.findByGUID( "4C6DFE04-871B-5685-CFCB-C745C6E69AE1" ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User2: " + user ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by E-mail" ) ;
        
        user = dao.findByEmail( "russ.licht@gmail.com" ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User1: " + user ) ;
        
        user = dao.findByEmail( "rnlicht@yahoo.com" ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User2: " + user ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by first name pattern" ) ;
        
        List<GcxUser> users = dao.findAllByFirstName( "*uss" ) ;
        if ( users != null ) {
            for( int i=0; i<users.size(); i++ ) {
                /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** user[" + i + "]: " + users.get( i ) ) ;
            }
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by last name pattern" ) ;
        
        users = dao.findAllByLastName( "*ich*" ) ;
        if ( users != null ) {
            for( int i=0; i<users.size(); i++ ) {
                /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** user[" + i + "]: " + users.get( i ) ) ;
            }
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Lookup by e-mail pattern" ) ;
        
        users = dao.findAllByEmail( "*ich*" ) ;
        if ( users != null ) {
            for( int i=0; i<users.size(); i++ ) {
                /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** user[" + i + "]: " + users.get( i ) ) ;
            }
        }
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testBasic" ) ;
    }
    
    
    public void testSave()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testSave" ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_DAO ) ;
        
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
        user.setDomainsVisitedString( "www.mygcx.org www.uscm.org" ) ;
        
        try {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to bind new user" ) ;
            dao.save( user ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User saved" ) ;
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Unable to save", e ) ;
            Assert.fail( "Unable to bind new entry" ) ;
        }

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to retrieve the new user" ) ;
        GcxUser user2 = (GcxUser)dao.get( email ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user2 ) ;
        
        Assert.assertEquals( "Expected cn not found", email, user2.getEmail() ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to unbind new user" ) ;
        dao.delete( user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User removed" ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testSave" ) ;
    }
    
    
    public void testUpdate()
    {
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** BEGIN: testUpdate" ) ;
        
        GcxUserDao dao = (GcxUserDao)this.getApplicationContext().getBean( Constants.BEAN_GCXUSER_DAO ) ;
        
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
        user.setDomainsVisitedString( "www.mygcx.org www.uscm.org" ) ;
        
        try {
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to bind new user" ) ;
            dao.save( user ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User saved" ) ;
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Unable to save", e ) ;
            Assert.fail( "Unable to bind new entry" ) ;
        }

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to retrieve the new user" ) ;
        GcxUser user2 = (GcxUser)dao.get( email ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Retrieved user: " + user2 ) ;
        
        Assert.assertEquals( "Expected cn not found", email, user2.getEmail() ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to modify the new user" ) ;
        user2.getDomainsVisited().add( "www.modifiedsite.com" ) ;
        user2.setForcePasswordChange( true ) ;
        user2.setLoginDisabled( true ) ;
        dao.update( user2 ) ;
        
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to unbind new user" ) ;
        //dao.delete( user ) ;
        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User removed" ) ;

        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** END: testUpdate" ) ;
    }
    
    
    /**
     * This is not to be a permanent test. It is just something to do a low level test on creating a
     * new entry.
     */
    public void testDirect()
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "$$yyyyMMdd_HH:mm:ss.S$$" ) ;
        Date currentDate = new Date() ;
        String cn = "gcrider-".concat( sdf.format( currentDate ) ).concat( "@me.com" ) ;
        //String dnActual = "cn=greg,ou=sso,dc=mygcx,dc=org" ;
        String dnActual = "cn=" + cn ;
        DistinguishedName dn = new DistinguishedName( dnActual ) ;
        
        DirContextAdapter context = new DirContextAdapter( dn ) ;
        
        context.setAttributeValues( "objectclass", new String[]{ "Top", "Person", "ndsLoginProperties", "organizationalPerson" }, true ) ;
        context.setAttributeValue( "cn", cn ) ;
        context.setAttributeValue( "sn", "Crider" ) ;
        
        try {
            LdapTemplate ldapTempate = (LdapTemplate)this.getApplicationContext().getBean( "ldap.template" ) ;
            
            LdapContext ldapContext = (LdapContext)ldapTempate.getContextSource().getReadWriteContext() ;
            Control[] controls = null ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Connection controls: " ) ;
            controls = ldapContext.getConnectControls() ;
            if ( controls != null ) {
                for( int i=0; i<controls.length; i++ ) {
                    /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** \tControl[" + i + "] -> id=" + controls[i].getID() + " critical=" + controls[i].isCritical() ) ; 
                }
            }
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Request controls: " ) ;
            controls = ldapContext.getRequestControls() ;
            if ( controls != null ) {
                for( int i=0; i<controls.length; i++ ) {
                    /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** \tControl[" + i + "] -> id=" + controls[i].getID() + " critical=" + controls[i].isCritical() ) ; 
                }
            }
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Response controls: " ) ;
            controls = ldapContext.getResponseControls() ;
            if ( controls != null ) {
                for( int i=0; i<controls.length; i++ ) {
                    /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** \tControl[" + i + "] -> id=" + controls[i].getID() + " critical=" + controls[i].isCritical() ) ; 
                }
            }
            
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attributes: " + context.getAttributes() ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Attempting to create" ) ;
            ldapTempate.getContextSource().getReadWriteContext().createSubcontext( new LdapName( dnActual ), context.getAttributes() ) ;
            /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Done!" ) ;
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Man! I hate eDirectory servers!", e ) ;
        }
    }
    
    
    /**
     * This is not to be a permanent test. It is just something to do a low level test on paging and sorting controls.
     */
    public void testDirect2()
    {
        
        try {
            LdapTemplate ldapTempate = (LdapTemplate)this.getApplicationContext().getBean( "ldap.template" ) ;

            LdapContext ctx = (LdapContext)ldapTempate.getContextSource().getReadWriteContext() ;
            
            Control sort = new SortControl("cn", true ) ;
            Control pager = new PagedResultsControl(5, true ) ;
            Control[] requestControls = new Control[2] ;
            requestControls[0] = sort ;
            requestControls[1] = pager ;
            
            ctx.setRequestControls( requestControls ) ;
            
            SearchControls searchControls = new SearchControls() ;
            searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE ) ;
            
            /*= INFO =*/ log.info( "***** Calling search" ) ;
            NamingEnumeration<?> results = ctx.search( "", "(&(objectclass=Person)(sn=*))", searchControls ) ;
            /*= INFO =*/ log.info( "***** Search done" ) ;
            
            Control[] responseControls = ctx.getResponseControls() ;
            for( int i=0; i<responseControls.length; i++ ) {
                if ( SortResponseControl.class.isAssignableFrom( responseControls[i].getClass() ) ) {
                    SortResponseControl sortResponseControl = (SortResponseControl)responseControls[i] ;
                    /*= INFO =*/ log.info( "***** Sort Response: result(" + sortResponseControl.getResultCode() + ")" ) ;
                } else if ( PagedResultsResponseControl.class.isAssignableFrom( responseControls[i].getClass() ) ) {
                    PagedResultsResponseControl pagedResponseControl = (PagedResultsResponseControl)responseControls[i] ;
                    /*= INFO =*/ log.info( "***** Paged Response: result size(" + pagedResponseControl.getResultSize() + ")" ) ;
                } else {
                    /*= INFO =*/ log.info( "***** Response Control: " + responseControls[i] ) ;
                }
            }
            
            if ( results != null ) {
                while( results.hasMore() ) {
                    Object entry = results.next() ;
                    /*= INFO =*/ log.info( "***** Entry: " + entry ) ;
                }
            }
        } catch ( Exception e ) {
            /*= ERROR =*/ log.error( "Man! I hate eDirectory servers!", e ) ;
        }
    }
    
}
