package org.ccci.gto.cas.persist.ldap;

import static org.ccci.gto.cas.Constants.LDAP_ATTR_FACEBOOKID;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.Constants;

/**
 * <b>GcxUserMapper</b> is used to map {@link GcxUser} from the attributes
 * returned by certain LDAP lookups.
 * 
 * @author Daniel Frett
 */
public class GcxUserMapper extends AbstractAttributesMapper {
    // LDAP Attributes in use
    private static final String ATTR_EMAIL = Constants.LDAP_ATTR_EMAIL;
    private static final String ATTR_GUID = Constants.LDAP_ATTR_GUID;
    private static final String ATTR_FIRSTNAME = Constants.LDAP_ATTR_FIRSTNAME;
    private static final String ATTR_LASTNAME = Constants.LDAP_ATTR_LASTNAME;
    private static final String ATTR_LOGINTIME = Constants.LDAP_ATTR_LOGINTIME;
    private static final String ATTR_USERID = Constants.LDAP_ATTR_USERID;
    private static final String ATTR_GROUPS = Constants.LDAP_ATTR_GROUPS;
    private static final String ATTR_DOMAINSVISITED = Constants.LDAP_ATTR_DOMAINSVISITED;
    private static final String ATTR_ADDITIONALGUIDS = Constants.LDAP_ATTR_ADDITIONALGUIDS;
    private static final String ATTR_ADDITIONALDOMAINSVISITED = Constants.LDAP_ATTR_ADDITIONALDOMAINSVISITED;
    private static final String FLAG_ALLOWPASSWORDCHANGE = Constants.LDAP_ATTR_ALLOWPASSWORDCHANGE;
    private static final String FLAG_LOGINDISABLED = Constants.LDAP_ATTR_LOGINDISABLED;
    private static final String FLAG_LOCKED = Constants.LDAP_ATTR_LOCKED;
    private static final String FLAG_STALEPASSWORD = Constants.LDAP_ATTR_STALEPASSWORD;
    private static final String FLAG_VERIFIED = Constants.LDAP_ATTR_VERIFIED;

    /**
     * @param attrs
     * @return a GcxUser object
     * @throws NamingException
     * @see org.springframework.ldap.core.AttributesMapper#mapFromAttributes(javax.naming.directory.Attributes)
     */
    public Object mapFromAttributes(final Attributes attrs)
	    throws NamingException {
	final GcxUser user = new GcxUser();

	// Base attributes
	user.setEmail(this.getStringValue(attrs, ATTR_EMAIL));
	user.setGUID(this.getStringValue(attrs, ATTR_GUID));
	user.setFirstName(this.getStringValue(attrs, ATTR_FIRSTNAME));
	user.setLastName(this.getStringValue(attrs, ATTR_LASTNAME));

	// Meta-data
	user.setLoginTime(this.getTimeValue(attrs, ATTR_LOGINTIME));
	user.setUserid(this.getStringValue(attrs, ATTR_USERID));
	user.setFacebookId(this.getStringValue(attrs, LDAP_ATTR_FACEBOOKID));

	// Multi-value attributes
	user.setGroupMembership(this.getStringValues(attrs, ATTR_GROUPS));
	user.setDomainsVisited(this.getStringValues(attrs, ATTR_DOMAINSVISITED));
	user.setGUIDAdditional(this
		.getStringValues(attrs, ATTR_ADDITIONALGUIDS));
	user.setDomainsVisitedAdditional(this.getStringValues(attrs,
		ATTR_ADDITIONALDOMAINSVISITED));

	// Flags
	user.setPasswordAllowChange(this.getBooleanValue(attrs,
		FLAG_ALLOWPASSWORDCHANGE));
	user.setLoginDisabled(this.getBooleanValue(attrs, FLAG_LOGINDISABLED));
	user.setLocked(this.getBooleanValue(attrs, FLAG_LOCKED));
	user.setForcePasswordChange(this.getBooleanValue(attrs,
		FLAG_STALEPASSWORD));
	user.setVerified(this.getBooleanValue(attrs, FLAG_VERIFIED, true));

	// return the loaded User object
	return user;
    }
}
