package org.ccci.gcx.idm.core.model.impl;

import static org.ccci.gcx.idm.core.Constants.DEFAULT_COUNTRY_CODE;
import static org.ccci.gto.cas.Constants.ACCOUNT_DEACTIVATEDPREFIX;
import static org.ccci.gto.cas.Constants.STRENGTH_FULL;
import static org.ccci.gto.cas.Constants.STRENGTH_NONE;
import static org.ccci.gto.cas.Constants.VALIDGUIDREGEX;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.ccci.gto.cas.model.Auditable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <b>GcxUser</b> defines the basic GCX user and his attributes.
 */
public class GcxUser implements Auditable, Serializable {
    private static final long serialVersionUID = 4531983367394137418L;
    private static final Logger LOG = LoggerFactory.getLogger(GcxUser.class);

    private static final String[] AuditProperties = new String[]{"email", "GUID", "firstName", "lastName",
            "domainsVisited", "GUIDAdditional", "domainsVisitedAdditional", "passwordAllowChange", "loginDisabled",
            "locked", "forcePasswordChange", "verified", "loginTime", "userid", "facebookId"};

    public static final String FIELD_GUID = "GUID";
    public static final String FIELD_PASSWORD = "password";


    /**
     * LDAP eDirectory fields:
     * <p/>
     * givenName  = first name
     * sn  = last name
     * cn (set to same value as uid) = email address
     * uid  (set to same value as cn)  = email address
     * extensionAttribute1  = SSO_GUID
     * extensionAttribute2  = list of domains visited with SSO_GUID
     * extensionAttribute3  = Additional SSO_GUID(s)
     * extensionAttribute4  = list of domains visited with Additional SSO_GUID(s)
     * loginTime  = timestamp set internally by eDirectory
     * passwordAllowChange  = TRUE/FALSE
     * loginDisabled  = TRUE/FALSE
     * extensionAttribute5 = force password change
     */

    // Attributes
    private String email = null;
    private String password = null;
    private String guid = null;
    private String firstName = null;
    private String lastName = null;

    // Multi-value attributes
    private final Collection<String> groupMembership = new HashSet<>();
    private final Collection<String> domainsVisited = new HashSet<>();
    private final Collection<String> additionalGuids = new HashSet<>();
    private final Collection<String> additionalDomainsVisited = new HashSet<>();

    // flags
    private boolean passwordAllowChange = false;
    private boolean loginDisabled = false;
    private boolean locked = false;
    private boolean forcePasswordChange = false;
    private boolean verified = false;

    // self-service verification keys
    private String signupKey = null;
    private String changeEmailKey = null;
    private String resetPasswordKey = null;
    private String proposedEmail = null;

    // Meta-data
    private String countryCode = DEFAULT_COUNTRY_CODE;
    private Date loginTime = null;
    private String userId = null;

    // Federated identities
    private String facebookId = null;
    private double facebookIdStrength = STRENGTH_NONE;

    /**
     * Return auditable property names.
     *
     * @return {@link String} array of auditable property names.
     * @see org.ccci.gto.cas.model.Auditable#getAuditProperties()
     */
    public String[] getAuditProperties() {
        return GcxUser.AuditProperties;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * @param a_firstName the firstName to set
     */
    public void setFirstName(String a_firstName) {
        this.firstName = a_firstName;
    }


    /**
     * @return the lastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * @param a_lastName the lastName to set
     */
    public void setLastName(String a_lastName) {
        this.lastName = a_lastName;
    }


    /**
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @param a_email the email to set
     */
    public void setEmail(String a_email) {
        this.email = a_email;
    }


    public boolean isDeactivated() {
        return StringUtils.isNotBlank(this.email) && this.email.startsWith(ACCOUNT_DEACTIVATEDPREFIX);
    }


    /**
     * @return the gUID
     */
    public String getGUID() {
        return this.guid;
    }

    /**
     * @param guid the guid for this user
     */
    public void setGUID(final String guid) {
        // throw an exception if this guid isn't valid
        if (!VALIDGUIDREGEX.matcher(guid).matches()) {
            LOG.error("Invalid GUID: {}", guid);
            // temporarily ignore this error until all broken accounts are
            // corrected
            // throw new IllegalArgumentException("Invalid GUID: " + guid);
        }

        this.guid = guid.toUpperCase();
    }

    /**
     * @return the domainsVisited
     */
    public Collection<String> getDomainsVisited() {
        return Collections.unmodifiableCollection(this.domainsVisited);
    }

    /**
     * @param domains the domainsVisited to set
     */
    public void setDomainsVisited(final Collection<String> domains) {
        this.domainsVisited.clear();
        if (domains != null) {
            this.domainsVisited.addAll(domains);
        }
    }

    public void addDomainsVisited(String a_domainsVisited) {
        if (StringUtils.isNotBlank(a_domainsVisited)) {
            this.domainsVisited.add(a_domainsVisited);
        }
    }

    /**
     * @param guids a list of additional guids to set for the current user
     */
    public void setGUIDAdditional(final Collection<String> guids) {
        this.additionalGuids.clear();
        this.addGUIDAdditional(guids);
    }

    public void addGUIDAdditional(final String guid) {
        if (StringUtils.isNotBlank(guid)) {
            this.additionalGuids.add(guid);
        }
    }

    public void addGUIDAdditional(final Collection<String> guids) {
        if (guids != null) {
            this.additionalGuids.addAll(guids);
        }
    }

    /**
     * @return an immutable list of additional guids
     */
    public Collection<String> getGUIDAdditional() {
        return Collections.unmodifiableCollection(this.additionalGuids);
    }

    /**
     * @param domains the domainsVisitedAdditional to set
     */
    public void setDomainsVisitedAdditional(final Collection<String> domains) {
        this.additionalDomainsVisited.clear();
        this.addDomainsVisitedAdditional(domains);
    }

    public void addDomainsVisitedAdditional(final String domain) {
        if (StringUtils.isNotBlank(domain)) {
            this.additionalDomainsVisited.add(domain);
        }
    }

    public void addDomainsVisitedAdditional(final Collection<String> domains) {
        if (domains != null) {
            this.additionalDomainsVisited.addAll(domains);
        }
    }

    /**
     * @return the domainsVisitedAdditional
     */
    public Collection<String> getDomainsVisitedAdditional() {
        return Collections.unmodifiableCollection(this.additionalDomainsVisited);
    }

    /**
     * @return the loginTime
     */
    public Date getLoginTime() {
        return this.loginTime;
    }

    /**
     * @param a_loginTime the loginTime to set
     */
    public void setLoginTime(Date a_loginTime) {
        this.loginTime = a_loginTime;
    }


    /**
     * @return the passwordAllowChange
     */
    public boolean isPasswordAllowChange() {
        return this.passwordAllowChange;
    }

    /**
     * @param a_passwordAllowChange the passwordAllowChange to set
     */
    public void setPasswordAllowChange(boolean a_passwordAllowChange) {
        this.passwordAllowChange = a_passwordAllowChange;
    }


    /**
     * @return the loginDisabled
     */
    public boolean isLoginDisabled() {
        return this.loginDisabled;
    }

    /**
     * @param a_loginDisabled the loginDisabled to set
     */
    public void setLoginDisabled(boolean a_loginDisabled) {
        this.loginDisabled = a_loginDisabled;
    }


    /**
     * @return the locked
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * @param a_locked the locked to set
     */
    public void setLocked(boolean a_locked) {
        this.locked = a_locked;
    }


    /**
     * @return the forcePasswordChange
     */
    public boolean isForcePasswordChange() {
        return this.forcePasswordChange;
    }

    /**
     * @param a_forcePasswordChange the forcePasswordChange to set
     */
    public void setForcePasswordChange(boolean a_forcePasswordChange) {
        this.forcePasswordChange = a_forcePasswordChange;
    }

    /**
     * @param verified a flag indicating if the current user has been verified or not
     */
    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    /**
     * @return whether or not the current user is verified
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param a_password the password to set
     */
    public void setPassword(String a_password) {
        this.password = a_password;
    }


    /**
     * @return the userid
     */
    public String getUserid() {
        return this.userId;
    }

    /**
     * @param a_userid the userid to set
     */
    public void setUserid(String a_userid) {
        this.userId = a_userid;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroupMembership(final Collection<String> groups) {
        this.groupMembership.clear();
        this.groupMembership.addAll(groups);
    }

    /**
     * @return the groupMembership
     */
    public Collection<String> getGroupMembership() {
        return Collections.unmodifiableCollection(this.groupMembership);
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return this.countryCode;
    }


    /**
     * Determine if this object is equal to the specified one. The comparison is done
     * based on GUID which should be unique to all {@link GcxUser} objects. If this
     * object and the specified one have <tt>null</tt> for the GUID, the result of
     * this method is <tt>false</tt>.
     *
     * @param a_Object Other object to be compared with this one.
     * @return <tt>True</tt> if both are equal by virtue of the GUID value, except
     * where they are both <tt>null</tt>
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object a_Object) {
        boolean result = false;

        if ((a_Object != null) && (GcxUser.class.isAssignableFrom(a_Object.getClass()))) {
            GcxUser other = (GcxUser) a_Object;
            if (((this.getGUID() != null) && (other.getGUID() != null)) && ((this == a_Object) || (this.getGUID()
                    .equals(other.getGUID())))) {
                result = true;
            }
        }

        return result;
    }

    public final String getSignupKey() {
        return this.signupKey;
    }

    public final String getChangeEmailKey() {
        return this.changeEmailKey;
    }

    public final String getResetPasswordKey() {
        return this.resetPasswordKey;
    }

    public final String getProposedEmail() {
        return this.proposedEmail;
    }

    public final void setSignupKey(final String key) {
        this.signupKey = key;
    }

    public final void setChangeEmailKey(final String key) {
        this.changeEmailKey = key;
    }

    public final void setResetPasswordKey(final String key) {
        this.resetPasswordKey = key;
    }

    public final void setProposedEmail(final String email) {
        this.proposedEmail = email;
    }

    /**
     * @param id the facebook id to set
     */
    public void setFacebookId(final String id, final Number strength) {
        this.facebookId = id;
        this.facebookIdStrength = (strength != null ? strength.doubleValue() : STRENGTH_NONE);
        if (this.facebookIdStrength < STRENGTH_NONE) {
            this.facebookIdStrength = STRENGTH_NONE;
        } else if (this.facebookIdStrength > STRENGTH_FULL) {
            this.facebookIdStrength = STRENGTH_FULL;
        }
    }

    /**
     * @return the facebook id
     */
    public String getFacebookId() {
        return facebookId;
    }

    public Double getFacebookIdStrengthFor(final String id) {
        if (this.facebookId != null && this.facebookId.equals(id)) {
            return this.facebookIdStrength;
        }
        return STRENGTH_NONE;
    }

    public void removeFacebookId(final String id) {
        if (id != null && id.equals(this.facebookId)) {
            this.facebookId = null;
            this.facebookIdStrength = STRENGTH_NONE;
        }
    }

    /**
     * copy this user object
     *
     * @return copy of the original object
     */
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
    public GcxUser clone() {
        final GcxUser user = new GcxUser();
        user.email = this.email;
        user.password = this.password;
        user.guid = this.guid;
        user.firstName = this.firstName;
        user.lastName = this.lastName;
        user.groupMembership.addAll(this.groupMembership);
        user.domainsVisited.addAll(this.domainsVisited);
        user.additionalGuids.addAll(this.additionalGuids);
        user.additionalDomainsVisited.addAll(this.additionalDomainsVisited);
        user.passwordAllowChange = this.passwordAllowChange;
        user.loginDisabled = this.loginDisabled;
        user.locked = this.locked;
        user.forcePasswordChange = this.forcePasswordChange;
        user.verified = this.verified;
        user.countryCode = this.countryCode;
        user.loginTime = this.loginTime;
        user.userId = this.userId;
        user.facebookId = this.facebookId;
        user.facebookIdStrength = this.facebookIdStrength;
        user.signupKey = this.signupKey;
        user.changeEmailKey = this.changeEmailKey;
        user.proposedEmail = this.proposedEmail;
        user.resetPasswordKey = this.resetPasswordKey;
        return user;
    }

    /**
     * Create display ready version of this object.
     *
     * @return String
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        final String name = this.getClass().getName();
        final int pos = (name.lastIndexOf(".") < 0) ? 0 : (name.lastIndexOf(".") + 1);

        result.append("<<").append(name.substring(pos)).append(">>::[");
        PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(this);
        for (int i = 0; i < desc.length; i++) {
            if ((desc[i].getPropertyType() != null) && (!List.class.isAssignableFrom(desc[i].getPropertyType())) &&
                    (!Set.class.isAssignableFrom(desc[i].getPropertyType()))) {
                if ((!desc[i].getName().equals("class")) && (!desc[i].getName().equals("createTime")) && (!desc[i]
                        .getName().equals("auditProperties"))) {
                    Object value = "?";
                    if (desc[i].getName().toLowerCase().matches("password")) {
                        value = "**redacted**";
                    } else {
                        try {
                            value = BeanUtils.getProperty(this, desc[i].getName());
                        } catch (Exception ignored) {
                        }
                    }
                    if (!result.substring(result.length() - 1).equals("[")) {
                        result.append(",");
                    }
                    result.append(desc[i].getName().toLowerCase()).append("=").append(value);
                }
            }
        }
        result.append("]");

        return result.toString();
    }
}
