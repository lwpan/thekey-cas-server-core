package org.ccci.gcx.idm.core.persist.ldap.bind.impl;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.persist.ldap.bind.AttributeBind;
import org.ccci.gcx.idm.core.util.LdapUtil;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.util.Assert;

/**
 * <b>GcxUserAttributeBind</b> is the concrete impelementation of {@link AttributeBind} for
 * converting {@link GcxUser} entities into their LDAP representation.
 *
 * @author Greg Crider  Oct 29, 2008  2:34:44 PM
 */
public class GcxUserAttributeBind extends AbstractAttributeBind
{
    protected static final Log log = LogFactory.getLog( GcxUserAttributeBind.class ) ;

    
    /**
     * @param a_ModelObject
     * @return
     * @see org.ccci.gcx.idm.core.persist.ldap.bind.AttributeBind#build(org.ccci.gcx.idm.common.model.ModelObject)
     */
    public Attributes build( ModelObject a_ModelObject )
    {
        this.assertModelObject( a_ModelObject, GcxUser.class ) ;
        
        GcxUser user = (GcxUser)a_ModelObject ;
        Attributes result = null ;
        
        if ( user != null ) { 
            Assert.hasText( user.getEmail(), "E-mail address cannot be blank." ) ;
            Assert.hasText( user.getUserid(), "Userid cannot be blank." ) ;
            
            String email = ( user.getEmail().startsWith( Constants.PREFIX_DEACTIVATED ) ) ? user.getEmail() : user.getEmail().toLowerCase() ;
            String userid = user.getUserid().toLowerCase() ;

            result = new BasicAttributes( true ) ;
            Attribute ocattr = new BasicAttribute( "objectclass" ) ;
            ocattr.add( Constants.LDAP_OBJECTCLASS_TOP ) ;
            ocattr.add( Constants.LDAP_OBJECTCLASS_PERSON ) ;
            ocattr.add( Constants.LDAP_OBJECTCLASS_NDSLOGIN ) ;
            ocattr.add( Constants.LDAP_OBJECTCLASS_ORGANIZATIONALPERSON ) ;
            ocattr.add( Constants.LDAP_OBJECTCLASS_INETORGPERSON ) ;
            result.put( ocattr ) ;

            result.put( Constants.LDAP_KEY_EMAIL, email ) ;
            result.put( Constants.LDAP_KEY_LASTNAME, user.getLastName() ) ;
            result.put( Constants.LDAP_KEY_FIRSTNAME, user.getFirstName() ) ;
            result.put( Constants.LDAP_KEY_GUID, user.getGUID() ) ;
            result.put( Constants.LDAP_KEY_PASSWORDALLOWCHANGE, Boolean.toString( user.isPasswordAllowChange() ).toUpperCase() ) ;
            result.put( Constants.LDAP_KEY_LOGINDISABLED, Boolean.toString( user.isLoginDisabled() ).toUpperCase() ) ;
            if ( StringUtils.isNotBlank( user.getPassword() ) ) {
                result.put( Constants.LDAP_KEY_PASSWORD, user.getPassword() ) ;
            }
            result.put( Constants.LDAP_KEY_USERID, userid ) ;
            if ( user.getLoginTime() != null ) {
                result.put( Constants.LDAP_KEY_LOGINTIME, this.convertToGeneralizedTime( user.getLoginTime() ) ) ;
            }
            result.put( Constants.LDAP_KEY_FORCEPASSWORDCHANGE, Boolean.toString( user.isForcePasswordChange() ).toUpperCase() ) ;

            this.addAttributeList( result, Constants.LDAP_KEY_DOMAINSVISITED, user.getDomainsVisited(), false ) ;
            this.addAttributeList( result, Constants.LDAP_KEY_GUIDADDITIONAL, user.getGUIDAdditional(), false ) ;
            this.addAttributeList( result, Constants.LDAP_KEY_DOMAINSVISITEDADDITIONAL, user.getDomainsVisitedAdditional(), false ) ;
            this.addAttributeList( result, Constants.LDAP_KEY_GROUPMEMBERSHIP, user.getGroupMembership(), false ) ;
            
            /*
             * The attribute for locking out a user is read-only, and shouldn't be set here because it might create a race
             * condition with the LDAP server. This is how you would normally set it.
             */
            //result.put( Constants.LDAP_KEY_LOCKED, Boolean.toString( user.isLocked() ).toUpperCase() ) ;

        }
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** GcxUser LDAP: " + LdapUtil.attributesToString( result ) ) ;
        
        return result ;
    }


    /**
     * @param a_ModelObject
     * @param a_DirContextOperations
     * @see org.ccci.gcx.idm.core.persist.ldap.bind.AttributeBind#mapToContext(org.ccci.gcx.idm.common.model.ModelObject, org.springframework.ldap.core.DirContextOperations)
     */
    public void mapToContext( ModelObject a_ModelObject, DirContextOperations a_DirContextOperations )
    {
        this.assertModelObject( a_ModelObject, GcxUser.class ) ;
        
        GcxUser user = (GcxUser)a_ModelObject ;
        
        if ( user != null ) {
            Assert.hasText( user.getEmail(), "E-mail address cannot be blank." ) ;
            Assert.hasText( user.getUserid(), "Userid cannot be blank." ) ;
            
            String email = ( user.getEmail().startsWith( Constants.PREFIX_DEACTIVATED ) ) ? user.getEmail() : user.getEmail().toLowerCase() ;
            String userid = user.getUserid().toLowerCase() ;

            a_DirContextOperations.setAttributeValues( "objectclass", 
                    new String[] { Constants.LDAP_OBJECTCLASS_TOP, Constants.LDAP_OBJECTCLASS_PERSON,
                                   Constants.LDAP_OBJECTCLASS_NDSLOGIN, Constants.LDAP_OBJECTCLASS_ORGANIZATIONALPERSON,
                                   Constants.LDAP_OBJECTCLASS_INETORGPERSON } ) ;
        
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_EMAIL, email ) ;
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_LASTNAME, user.getLastName() ) ;
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_FIRSTNAME, user.getFirstName() ) ;
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_GUID, user.getGUID() ) ;
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_PASSWORDALLOWCHANGE, Boolean.toString( user.isPasswordAllowChange() ).toUpperCase() ) ;
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_LOGINDISABLED, Boolean.toString( user.isLoginDisabled() ).toUpperCase() ) ;
            if ( StringUtils.isNotBlank( user.getPassword() ) ) {
                a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_PASSWORD, user.getPassword() ) ;
            }
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_USERID, userid ) ;
            if ( user.getLoginTime() != null ) {
                a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_LOGINTIME, this.convertToGeneralizedTime( user.getLoginTime() ) ) ;
            }
            a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_FORCEPASSWORDCHANGE, Boolean.toString( user.isForcePasswordChange() ).toUpperCase() ) ;

            if ( user.getDomainsVisited() != null ) {
                a_DirContextOperations.setAttributeValues( Constants.LDAP_KEY_DOMAINSVISITED, user.getDomainsVisited().toArray() ) ;
            }
            if ( user.getGUIDAdditional() != null ) {
                a_DirContextOperations.setAttributeValues( Constants.LDAP_KEY_GUIDADDITIONAL, user.getGUIDAdditional().toArray() ) ;
            }
            if ( user.getDomainsVisitedAdditional() != null ) {
                a_DirContextOperations.setAttributeValues( Constants.LDAP_KEY_DOMAINSVISITEDADDITIONAL, user.getDomainsVisitedAdditional().toArray() ) ;
            }
            if ( user.getGroupMembership() != null ) {
                a_DirContextOperations.setAttributeValues( Constants.LDAP_KEY_GROUPMEMBERSHIP, user.getGroupMembership().toArray() ) ;
            }
            
            /*
             * The attribute for locking out a user is read-only, and shouldn't be set here because it might create a race
             * condition with the LDAP server. This is how you would normally set it.
             */
            //a_DirContextOperations.setAttributeValue( Constants.LDAP_KEY_LOCKED, Boolean.toString( user.isLocked() ).toUpperCase() ) ;
        }
    }

}
