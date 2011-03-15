package org.ccci.gcx.idm.core.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.model.impl.AbstractModelObject;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.util.RandomGUID;

/**
 * <b>GcxUser</b> defines the basic GCX user and his attributes.
 *
 * @author Greg Crider  Oct 16, 2008  6:53:40 PM
 */
public class GcxUser extends AbstractModelObject
{
    private static final long serialVersionUID = 7178098189293211694L ;
    
    private static final String[] AuditProperties = new String[]{ 
        "firstName", "lastName", "email", "GUID", "domainsVisited", "GUIDAdditional", 
        "domainsVisitedAdditional", "loginTime", "passwordAllowChange", "loginDisabled", 
        "locked", "forcePasswordChange", "userid"
    } ;
    
    public static final String FIELD_PASSWORD = "password" ;
    
    public static final String FIELD_GUID = "GUID" ;

    /**
     * LDAP eDirectory fields:
     * 
     * givenName  = first name
     * sn  = last name
     * cn (set to same value as uid) = email address
     * uid  (set to same value as cn)  = email address
     * extensionAttribute5  = (formerly Question. No longer used)
     * extensionAttribute6  = (formerly Answer. No longer used)
     * extensionAttribute1  = SSO_GUID
     * extensionAttribute2  = list of domains visited with SSO_GUID
     * extensionAttribute3  = Additional SSO_GUID(s)
     * extensionAttribute4  = list of domains visited with Additional SSO_GUID(s)
     * loginTime  = timestamp set internally by eDirectory
     * passwordAllowChange  = TRUE/FALSE
     * loginDisabled  = TRUE/FALSE
     * extensionAttribute5 = force password change
     **/
    
    private String m_FirstName = null ;
    private String m_LastName = null ;
    private String m_Email = null ;
    private String m_GUID = null ;
    private Date m_LoginTime = null ;
    private boolean m_PasswordAllowChange = false ;
    private boolean m_LoginDisabled = false ;
    private boolean m_Locked = false ;
    private boolean m_ForcePasswordChange = false ;
    private String m_Password = null ;
    private String m_Userid = null ;
    private String m_CountryCode = Constants.DEFAULT_COUNTRY_CODE ;
    // These are multi-value fields, and may need a Collection datatype
    private List<String> m_GUIDAdditional = null ;
    final private ArrayList<String> m_DomainsVisited;
    private List<String> m_DomainsVisitedAdditional = null ;
    private List<String> m_GroupMembership = null ;

    public GcxUser() {
	super();
	this.m_DomainsVisited = new ArrayList<String>();
    }

    /**
     * Return auditable property names.
     * 
     * @return {@link String} array of auditable property names.
     * 
     * @see org.ccci.gcx.idm.common.model.impl.AbstractModelObject#getAuditProperties()
     */
    public String[] getAuditProperties()
    {
        return GcxUser.AuditProperties ;
    }

    
    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return this.m_FirstName ;
    }
    /**
     * @param a_firstName the firstName to set
     */
    public void setFirstName( String a_firstName )
    {
        this.m_FirstName = a_firstName ;
    }

    
    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return this.m_LastName ;
    }
    /**
     * @param a_lastName the lastName to set
     */
    public void setLastName( String a_lastName )
    {
        this.m_LastName = a_lastName ;
    }

    
    /**
     * @return the email
     */
    public String getEmail()
    {
        return this.m_Email ;
    }
    /**
     * @param a_email the email to set
     */
    public void setEmail( String a_email )
    {
        this.m_Email = a_email ;
    }
    
    
    public boolean isDeactivated()
    {
        return ( StringUtils.isNotBlank( this.m_Email ) && ( this.m_Email.startsWith( Constants.PREFIX_DEACTIVATED ) ) ) ;
    }

    
    /**
     * @return the gUID
     */
    public String getGUID()
    {
        return this.m_GUID ;
    }
    /**
     * @param a_guid the gUID to set
     */
    public void setGUID( String a_guid )
    {
        this.m_GUID = a_guid ;
    }
    public void setGUID( RandomGUID a_RandomGUID )
    {
        this.m_GUID = a_RandomGUID.toString() ;
    }

    
    /**
     * @return the domainsVisited
     */
    public ArrayList<String> getDomainsVisited() {
	return this.m_DomainsVisited;
    }

    /**
     * @param a_domainsVisited the domainsVisited to set
     */
    public void setDomainsVisited(List<String> a_domainsVisited) {
	this.m_DomainsVisited.clear();
	if (a_domainsVisited != null) {
	    this.m_DomainsVisited.addAll(a_domainsVisited);
	}
    }

    public void addDomainsVisited(String a_domainsVisited) {
	if (StringUtils.isNotBlank(a_domainsVisited)) {
	    this.m_DomainsVisited.add(a_domainsVisited);
	}
    }

    public void setDomainsVisitedString(String a_domainsVisited) {
	this.setDomainsVisited(Arrays.asList(StringUtils
		.split(a_domainsVisited)));
    }

    public String getDomainsVisitedString() {
	return StringUtils.join(this.m_DomainsVisited.toArray(), " ");
    }

    /**
     * @return the GUIDAdditional
     */
    public List<String> getGUIDAdditional()
    {
        return this.m_GUIDAdditional ;
    }
    /**
     * @param a_additional the gUIDAdditional to set
     */
    public void setGUIDAdditional( List<String> a_additional )
    {
        this.m_GUIDAdditional = a_additional ;
    }
    public void setGUIDAdditionalString( String a_additional )
    {
        this.m_GUIDAdditional = Arrays.asList( StringUtils.split( a_additional ) ) ;
    }
    public String getGUIDAdditionalString()
    {
        String result = null ;
        
        if ( this.m_GUIDAdditional != null ) {
            result = StringUtils.join( this.m_GUIDAdditional.toArray(), " " ) ;
        }
        
        return result ;
    }
    public void addGUIDAdditional( String a_additional )
    {
        if ( StringUtils.isNotBlank( a_additional ) ) {
            if ( this.m_GUIDAdditional == null ) {
                this.m_GUIDAdditional = new ArrayList<String>() ;
            }
        
            this.m_GUIDAdditional.add( a_additional ) ;
        }
    }
    public void addGUIDAdditional( List<String> a_additional )
    {
        if ( ( a_additional != null ) && ( a_additional.size() > 0 ) ) {
            if ( this.m_GUIDAdditional == null ) {
                this.m_GUIDAdditional = new ArrayList<String>( a_additional ) ;
            } else {
                this.m_GUIDAdditional.addAll( a_additional ) ;
            }
        }
    }

    
    /**
     * @return the domainsVisitedAdditional
     */
    public List<String> getDomainsVisitedAdditional()
    {
        return this.m_DomainsVisitedAdditional ;
    }
    /**
     * @param a_domainsVisitedAdditional the domainsVisitedAdditional to set
     */
    public void setDomainsVisitedAdditional( List<String> a_domainsVisitedAdditional )
    {
        this.m_DomainsVisitedAdditional = a_domainsVisitedAdditional ;
    }
    public void setDomainsVisitedAdditionalString( String a_domainsVisitedAdditional )
    {
        this.m_DomainsVisitedAdditional = Arrays.asList( StringUtils.split( a_domainsVisitedAdditional ) ) ;
    }
    public String getDomainsVisitedAdditionalString()
    {
        String result = null ;
        
        if ( this.m_DomainsVisitedAdditional != null ) {
            result = StringUtils.join( this.m_DomainsVisitedAdditional.toArray(), " " ) ;
        }
        
        return result ;
    }
    public void addDomainsVisitedAdditional( String a_domainsVisitedAdditional )
    {
        if ( StringUtils.isNotBlank( a_domainsVisitedAdditional ) ) {
            if ( this.m_DomainsVisitedAdditional == null ) {
                this.m_DomainsVisitedAdditional = new ArrayList<String>() ;
            }
        
            this.m_DomainsVisitedAdditional.add( a_domainsVisitedAdditional ) ;
        }
    }
    public void addDomainsVisitedAdditional( List<String> a_domainsVisitedAdditional ) 
    {
        if ( ( a_domainsVisitedAdditional != null ) && ( a_domainsVisitedAdditional.size() > 0 ) ) {
            if ( this.m_DomainsVisitedAdditional == null ) {
                this.m_DomainsVisitedAdditional = new ArrayList<String>( a_domainsVisitedAdditional ) ;
            } else {
                this.m_DomainsVisitedAdditional.addAll( a_domainsVisitedAdditional ) ;
            }
        }
    }

    
    /**
     * @return the loginTime
     */
    public Date getLoginTime()
    {
        return this.m_LoginTime ;
    }
    /**
     * @param a_loginTime the loginTime to set
     */
    public void setLoginTime( Date a_loginTime )
    {
        this.m_LoginTime = a_loginTime ;
    }

    
    /**
     * @return the passwordAllowChange
     */
    public boolean isPasswordAllowChange()
    {
        return this.m_PasswordAllowChange ;
    }
    /**
     * @param a_passwordAllowChange the passwordAllowChange to set
     */
    public void setPasswordAllowChange( boolean a_passwordAllowChange )
    {
        this.m_PasswordAllowChange = a_passwordAllowChange ;
    }

    
    /**
     * @return the loginDisabled
     */
    public boolean isLoginDisabled()
    {
        return this.m_LoginDisabled ;
    }
    /**
     * @param a_loginDisabled the loginDisabled to set
     */
    public void setLoginDisabled( boolean a_loginDisabled )
    {
        this.m_LoginDisabled = a_loginDisabled ;
    }

    
    /**
     * @return the locked
     */
    public boolean isLocked()
    {
        return this.m_Locked ;
    }
    /**
     * @param a_locked the locked to set
     */
    public void setLocked( boolean a_locked )
    {
        this.m_Locked = a_locked ;
    }

    
    /**
     * @return the forcePasswordChange
     */
    public boolean isForcePasswordChange()
    {
        return this.m_ForcePasswordChange ;
    }
    /**
     * @param a_forcePasswordChange the forcePasswordChange to set
     */
    public void setForcePasswordChange( boolean a_forcePasswordChange )
    {
        this.m_ForcePasswordChange = a_forcePasswordChange ;
    }


    /**
     * @return the password
     */
    public String getPassword()
    {
        return this.m_Password ;
    }
    /**
     * @param a_password the password to set
     */
    public void setPassword( String a_password )
    {
        this.m_Password = a_password ;
    }


    /**
     * @return the userid
     */
    public String getUserid()
    {
        return this.m_Userid ;
    }
    /**
     * @param a_userid the userid to set
     */
    public void setUserid( String a_userid )
    {
        this.m_Userid = a_userid ;
    }


    /**
     * @return the groupMembership
     */
    public List<String> getGroupMembership()
    {
        return this.m_GroupMembership ;
    }
    /**
     * @param a_groupMembership the groupMembership to set
     */
    public void setGroupMembership( List<String> a_groupMembership )
    {
        this.m_GroupMembership = a_groupMembership ;
    }
    public void setGroupMembershipString( String a_groupMembership )
    {
        this.m_GroupMembership = Arrays.asList( StringUtils.split( a_groupMembership ) ) ;
    }
    public String getGroupMembershipString()
    {
        String result = null ;
        
        if ( this.m_GroupMembership != null ) {
            result = StringUtils.join( this.m_GroupMembership.toArray(), " " ) ;
        }
        
        return result ;
    }


    /**
     * @return the countryCode
     */
    public String getCountryCode()
    {
        return this.m_CountryCode ;
    }
    /**
     * @param a_countryCode the countryCode to set
     */
    public void setCountryCode( String a_countryCode )
    {
        this.m_CountryCode = a_countryCode ;
    }
 
    
    /**
     * Determine if this object is equal to the specified one. The comparison is done
     * based on GUID which should be unique to all {@link GcxUser} objects. If this
     * object and the specified one have <tt>null</tt> for the GUID, the result of 
     * this method is <tt>false</tt>.
     * 
     * @param a_Object Other object to be compared with this one.
     * 
     * @return <tt>True</tt> if both are equal by virtue of the GUID value, except
     * where they are both <tt>null</tt>
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object a_Object )
    {
        boolean result = false ;
        
        if ( ( a_Object != null ) && ( GcxUser.class.isAssignableFrom( a_Object.getClass() ) ) ) {
            GcxUser other = (GcxUser)a_Object ;
            if ( 
                    ( ( this.getGUID() != null ) && ( other.getGUID() != null ) ) &&
                    (
                            ( this == a_Object ) ||
                            ( this.getGUID().equals( other.getGUID() ) )
                    )
               ) {
                result = true ;
            }
        }
        
        return result ;
    }
}
