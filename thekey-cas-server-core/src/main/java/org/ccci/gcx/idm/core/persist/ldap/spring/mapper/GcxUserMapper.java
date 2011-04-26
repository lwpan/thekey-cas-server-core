package org.ccci.gcx.idm.core.persist.ldap.spring.mapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;

/**
 * <b>GcxUserMapper</b> is used to map {@link GcxUser} from the attributes returned by certain
 * LDAP lookups.
 *
 * @author Greg Crider  Oct 24, 2008  5:43:22 PM
 */
public class GcxUserMapper extends AbstractAttributesMapper
{

    /**
     * @param a_Attributes
     * @return
     * @throws NamingException
     * @see org.springframework.ldap.core.AttributesMapper#mapFromAttributes(javax.naming.directory.Attributes)
     */
    public Object mapFromAttributes( Attributes a_Attributes ) throws NamingException
    {
        GcxUser result = new GcxUser() ;
        
        result.setEmail( (String)this.getAttributeSafely( a_Attributes,  Constants.LDAP_KEY_EMAIL ) ) ;
        result.setUserid( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_USERID ) ) ;
        result.setFirstName( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_FIRSTNAME ) ) ;
        result.setLastName( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_LASTNAME ) ) ;
        result.setGUID( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_GUID ) ) ;
        
        result.setDomainsVisited( this.getAllAttributeStringSafely( a_Attributes, Constants.LDAP_KEY_DOMAINSVISITED ) ) ;
        result.setGUIDAdditional( this.getAllAttributeStringSafely( a_Attributes, Constants.LDAP_KEY_GUIDADDITIONAL ) ) ;
        result.setDomainsVisitedAdditional( this.getAllAttributeStringSafely( a_Attributes, Constants.LDAP_KEY_DOMAINSVISITEDADDITIONAL ) ) ;
        
        result.setPasswordAllowChange( Boolean.parseBoolean( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_PASSWORDALLOWCHANGE ) ) ) ;
        result.setLoginDisabled( Boolean.parseBoolean( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_LOGINDISABLED ) ) ) ;
        result.setLocked( Boolean.parseBoolean( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_LOCKED ) ) ) ;
        
        result.setLoginTime( this.getGeneralizedTimeAttributeSafely( a_Attributes, Constants.LDAP_KEY_LOGINTIME ) ) ;
        
        result.setGroupMembership( this.getAllAttributeStringSafely( a_Attributes, Constants.LDAP_KEY_GROUPMEMBERSHIP ) ) ;
        
        result.setForcePasswordChange( Boolean.parseBoolean( (String)this.getAttributeSafely( a_Attributes, Constants.LDAP_KEY_FORCEPASSWORDCHANGE ) ) ) ;
        
        return result ;
    }

}
