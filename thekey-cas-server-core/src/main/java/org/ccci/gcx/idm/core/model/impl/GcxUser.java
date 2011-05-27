package org.ccci.gcx.idm.core.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.common.model.impl.AbstractModelObject;
import org.ccci.gcx.idm.core.Constants;

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

    public static final String FIELD_GUID = "GUID";
    public static final String FIELD_PASSWORD = "password";
    private static final String DEFAULT_COUNTRY_CODE = Constants.DEFAULT_COUNTRY_CODE;

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

    // Attributes
    private String email = null;
    private String password = null;
    private String guid = null;
    private String firstName = null;
    private String lastName = null;

    // Multi-value attributes
    private final ArrayList<String> domainsVisited = new ArrayList<String>();
    private List<String> guidAdditional = null;
    private List<String> domainsVisitedAdditional = null;
    private List<String> groupMembership = null;

    // flags
    private boolean passwordAllowChange = false;
    private boolean loginDisabled = false;
    private boolean locked = false;
    private boolean forcePasswordChange = false;

    // Meta-data
    private String countryCode = DEFAULT_COUNTRY_CODE;
    private Date loginTime = null;
    private String userId = null;

    /**
     * Return auditable property names.
     * 
     * @return {@link String} array of auditable property names.
     * 
     * @see org.ccci.gcx.idm.common.model.impl.AbstractModelObject#getAuditProperties()
     */
    @Override
    public String[] getAuditProperties()
    {
        return GcxUser.AuditProperties ;
    }

    
    /**
     * @return the firstName
     */
    public String getFirstName()
    {
	return this.firstName;
    }
    /**
     * @param a_firstName the firstName to set
     */
    public void setFirstName( String a_firstName )
    {
	this.firstName = a_firstName;
    }

    
    /**
     * @return the lastName
     */
    public String getLastName()
    {
	return this.lastName;
    }
    /**
     * @param a_lastName the lastName to set
     */
    public void setLastName( String a_lastName )
    {
	this.lastName = a_lastName;
    }

    
    /**
     * @return the email
     */
    public String getEmail()
    {
	return this.email;
    }
    /**
     * @param a_email the email to set
     */
    public void setEmail( String a_email )
    {
	this.email = a_email;
    }
    
    
    public boolean isDeactivated()
    {
	return StringUtils.isNotBlank(this.email)
		&& this.email.startsWith(Constants.PREFIX_DEACTIVATED);
    }

    
    /**
     * @return the gUID
     */
    public String getGUID()
    {
	return this.guid;
    }
    /**
     * @param a_guid the gUID to set
     */
    public void setGUID( String a_guid )
    {
	this.guid = a_guid;
    }

    /**
     * @return the domainsVisited
     */
    public List<String> getDomainsVisited() {
	return Collections.unmodifiableList(this.domainsVisited);
    }

    /**
     * @param a_domainsVisited the domainsVisited to set
     */
    public void setDomainsVisited(List<String> a_domainsVisited) {
	this.domainsVisited.clear();
	if (a_domainsVisited != null) {
	    this.domainsVisited.addAll(a_domainsVisited);
	}
    }

    public void addDomainsVisited(String a_domainsVisited) {
	if (StringUtils.isNotBlank(a_domainsVisited)) {
	    this.domainsVisited.add(a_domainsVisited);
	}
    }

    public void setDomainsVisitedString(String a_domainsVisited) {
	this.setDomainsVisited(Arrays.asList(StringUtils
		.split(a_domainsVisited)));
    }

    public String getDomainsVisitedString() {
	return StringUtils.join(this.domainsVisited.toArray(), " ");
    }

    /**
     * @return the GUIDAdditional
     */
    public List<String> getGUIDAdditional()
    {
	return this.guidAdditional;
    }
    /**
     * @param a_additional the gUIDAdditional to set
     */
    public void setGUIDAdditional( List<String> a_additional )
    {
	this.guidAdditional = a_additional;
    }
    public void setGUIDAdditionalString( String a_additional )
    {
	this.guidAdditional = Arrays.asList(StringUtils.split(a_additional));
    }
    public String getGUIDAdditionalString()
    {
        String result = null ;
        
	if (this.guidAdditional != null) {
	    result = StringUtils.join(this.guidAdditional.toArray(), " ");
        }
        
        return result ;
    }
    public void addGUIDAdditional( String a_additional )
    {
        if ( StringUtils.isNotBlank( a_additional ) ) {
	    if (this.guidAdditional == null) {
		this.guidAdditional = new ArrayList<String>();
            }
        
	    this.guidAdditional.add(a_additional);
        }
    }
    public void addGUIDAdditional( List<String> a_additional )
    {
        if ( ( a_additional != null ) && ( a_additional.size() > 0 ) ) {
	    if (this.guidAdditional == null) {
		this.guidAdditional = new ArrayList<String>(a_additional);
	    } else {
		this.guidAdditional.addAll(a_additional);
            }
        }
    }

    
    /**
     * @return the domainsVisitedAdditional
     */
    public List<String> getDomainsVisitedAdditional()
    {
	return this.domainsVisitedAdditional;
    }
    /**
     * @param a_domainsVisitedAdditional the domainsVisitedAdditional to set
     */
    public void setDomainsVisitedAdditional( List<String> a_domainsVisitedAdditional )
    {
	this.domainsVisitedAdditional = a_domainsVisitedAdditional;
    }
    public void setDomainsVisitedAdditionalString( String a_domainsVisitedAdditional )
    {
	this.domainsVisitedAdditional = Arrays.asList(StringUtils
		.split(a_domainsVisitedAdditional));
    }
    public String getDomainsVisitedAdditionalString()
    {
	if (this.domainsVisitedAdditional != null) {
	    return StringUtils.join(this.domainsVisitedAdditional.toArray(),
		    " ");
        }

	return null;
    }
    public void addDomainsVisitedAdditional( String a_domainsVisitedAdditional )
    {
        if ( StringUtils.isNotBlank( a_domainsVisitedAdditional ) ) {
	    if (this.domainsVisitedAdditional == null) {
		this.domainsVisitedAdditional = new ArrayList<String>();
            }
        
	    this.domainsVisitedAdditional.add(a_domainsVisitedAdditional);
        }
    }
    public void addDomainsVisitedAdditional( List<String> a_domainsVisitedAdditional ) 
    {
        if ( ( a_domainsVisitedAdditional != null ) && ( a_domainsVisitedAdditional.size() > 0 ) ) {
	    if (this.domainsVisitedAdditional == null) {
		this.domainsVisitedAdditional = new ArrayList<String>(
			a_domainsVisitedAdditional);
	    } else {
		this.domainsVisitedAdditional
			.addAll(a_domainsVisitedAdditional);
            }
        }
    }

    
    /**
     * @return the loginTime
     */
    public Date getLoginTime()
    {
	return this.loginTime;
    }
    /**
     * @param a_loginTime the loginTime to set
     */
    public void setLoginTime( Date a_loginTime )
    {
	this.loginTime = a_loginTime;
    }

    
    /**
     * @return the passwordAllowChange
     */
    public boolean isPasswordAllowChange()
    {
	return this.passwordAllowChange;
    }
    /**
     * @param a_passwordAllowChange the passwordAllowChange to set
     */
    public void setPasswordAllowChange( boolean a_passwordAllowChange )
    {
	this.passwordAllowChange = a_passwordAllowChange;
    }

    
    /**
     * @return the loginDisabled
     */
    public boolean isLoginDisabled()
    {
	return this.loginDisabled;
    }
    /**
     * @param a_loginDisabled the loginDisabled to set
     */
    public void setLoginDisabled( boolean a_loginDisabled )
    {
	this.loginDisabled = a_loginDisabled;
    }

    
    /**
     * @return the locked
     */
    public boolean isLocked()
    {
	return this.locked;
    }
    /**
     * @param a_locked the locked to set
     */
    public void setLocked( boolean a_locked )
    {
	this.locked = a_locked;
    }

    
    /**
     * @return the forcePasswordChange
     */
    public boolean isForcePasswordChange()
    {
	return this.forcePasswordChange;
    }
    /**
     * @param a_forcePasswordChange the forcePasswordChange to set
     */
    public void setForcePasswordChange( boolean a_forcePasswordChange )
    {
	this.forcePasswordChange = a_forcePasswordChange;
    }


    /**
     * @return the password
     */
    public String getPassword()
    {
	return this.password;
    }
    /**
     * @param a_password the password to set
     */
    public void setPassword( String a_password )
    {
	this.password = a_password;
    }


    /**
     * @return the userid
     */
    public String getUserid()
    {
	return this.userId;
    }
    /**
     * @param a_userid the userid to set
     */
    public void setUserid( String a_userid )
    {
	this.userId = a_userid;
    }


    /**
     * @return the groupMembership
     */
    public List<String> getGroupMembership()
    {
	return this.groupMembership;
    }
    /**
     * @param a_groupMembership the groupMembership to set
     */
    public void setGroupMembership( List<String> a_groupMembership )
    {
	this.groupMembership = a_groupMembership;
    }
    public void setGroupMembershipString( String a_groupMembership )
    {
	this.groupMembership = Arrays.asList(StringUtils
		.split(a_groupMembership));
    }
    public String getGroupMembershipString()
    {
	if (this.groupMembership != null) {
	    return StringUtils.join(this.groupMembership.toArray(), " ");
        }

	return null;
    }


    /**
     * @return the countryCode
     */
    public String getCountryCode()
    {
	return this.countryCode;
    }
    /**
     * @param a_countryCode the countryCode to set
     */
    public void setCountryCode( String a_countryCode )
    {
	this.countryCode = a_countryCode;
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
