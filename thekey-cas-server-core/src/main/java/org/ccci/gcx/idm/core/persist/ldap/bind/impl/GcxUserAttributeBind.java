package org.ccci.gcx.idm.core.persist.ldap.bind.impl;

import static org.ccci.gto.cas.Constants.*;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.util.LdapUtil;
import org.ccci.gto.cas.persist.ldap.bind.AttributeBind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.util.Assert;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.Date;

/**
 * <b>GcxUserAttributeBind</b> is the concrete implementation of
 * {@link AttributeBind} for converting {@link GcxUser} entities into their LDAP
 * representation.
 *
 * @author Daniel Frett
 */
public class GcxUserAttributeBind extends AbstractAttributeBind<GcxUser> {
    private static final Logger LOG = LoggerFactory.getLogger(GcxUserAttributeBind.class);

    /*
     * (non-Javadoc)
     *
     * @see org.ccci.gcx.idm.core.persist.ldap.bind.impl.AbstractAttributeBind#
     * assertValidObject(java.lang.Object)
     */
    @Override
    protected void assertValidObject(final GcxUser user) {
        super.assertValidObject(user);
        Assert.hasText(user.getEmail(), "E-mail address cannot be blank.");
        Assert.hasText(user.getUserid(), "Userid cannot be blank.");
    }

    public Attributes build(final GcxUser user) {
        // make sure a valid GcxUser is provided
        this.assertValidObject(user);

        // The attribute for locking out a user is read-only, and shouldn't be set here.

        // build the Attributes object
        Attributes attrs = new BasicAttributes(true);

        // set the object class for this GcxUser
        final Attribute objectClass = new BasicAttribute(LDAP_ATTR_OBJECTCLASS);
        objectClass.add(LDAP_OBJECTCLASS_TOP);
        objectClass.add(LDAP_OBJECTCLASS_PERSON);
        objectClass.add(LDAP_OBJECTCLASS_NDSLOGIN);
        objectClass.add(LDAP_OBJECTCLASS_ORGANIZATIONALPERSON);
        objectClass.add(LDAP_OBJECTCLASS_INETORGPERSON);
        objectClass.add(LDAP_OBJECTCLASS_THEKEYATTRIBUTES);
        if (hasCruAttributes(user)) {
            objectClass.add(LDAP_OBJECTCLASS_CRU_PERSON);
        }
        attrs.put(objectClass);

        // set the attributes for this user
        attrs.put(LDAP_ATTR_EMAIL, user.getEmail());
        attrs.put(LDAP_ATTR_GUID, user.getGUID());
        attrs.put(LDAP_ATTR_FIRSTNAME, user.getFirstName());
        attrs.put(LDAP_ATTR_LASTNAME, user.getLastName());
        attrs.put(LDAP_ATTR_USERID, user.getUserid());

        attrs.put(LDAP_FLAG_ALLOWPASSWORDCHANGE, Boolean.toString(user.isPasswordAllowChange()).toUpperCase());
        attrs.put(LDAP_FLAG_LOGINDISABLED, Boolean.toString(user.isLoginDisabled()).toUpperCase());
        attrs.put(LDAP_FLAG_STALEPASSWORD, Boolean.toString(user.isForcePasswordChange()).toUpperCase());
        attrs.put(LDAP_FLAG_VERIFIED, Boolean.toString(user.isVerified()).toUpperCase());

        // store any self-service keys
        final String signupKey = user.getSignupKey();
        if (StringUtils.isNotBlank(signupKey)) {
            attrs.put(LDAP_ATTR_SIGNUPKEY, signupKey);
        }
        final String changeEmailKey = user.getChangeEmailKey();
        if (StringUtils.isNotBlank(changeEmailKey)) {
            attrs.put(LDAP_ATTR_CHANGEEMAILKEY, changeEmailKey);
        }
        final String proposedEmail = user.getProposedEmail();
        if (StringUtils.isNotBlank(proposedEmail)) {
            attrs.put(LDAP_ATTR_PROPOSEDEMAIL, proposedEmail);
        }
        final String resetPasswordKey = user.getResetPasswordKey();
        if (StringUtils.isNotBlank(resetPasswordKey)) {
            attrs.put(LDAP_ATTR_RESETPASSWORDKEY, resetPasswordKey);
        }

        final String password = user.getPassword();
        if (StringUtils.isNotBlank(password)) {
            attrs.put(LDAP_ATTR_PASSWORD, password);
        }
        final Date loginTime = user.getLoginTime();
        if (loginTime != null) {
            attrs.put(LDAP_ATTR_LOGINTIME, this.convertToGeneralizedTime(loginTime));
        }

        // set the multi-valued attributes
        this.addAttributeList(attrs, LDAP_ATTR_DOMAINSVISITED, user.getDomainsVisited(), false);
        this.addAttributeList(attrs, LDAP_ATTR_ADDITIONALGUIDS, user.getGUIDAdditional(), false);
        this.addAttributeList(attrs, LDAP_ATTR_ADDITIONALDOMAINSVISITED, user.getDomainsVisitedAdditional(), false);

        // set any federated identities
        final String facebookId = user.getFacebookId();
        if (facebookId != null) {
            attrs.put(LDAP_ATTR_FACEBOOKID, facebookId);
            attrs.put(LDAP_ATTR_FACEBOOKIDSTRENGTH, encodeStrength(facebookId,
                    user.getFacebookIdStrengthFor(facebookId)));
        }

        // Relay required/defined attributes
        if(hasCruAttributes(user))
        {
            log.debug("has attributes");
            if (!Strings.isNullOrEmpty(user.getRelayGuid()))
            {
                attrs.put(LDAP_ATTR_CRU_RELAY_GUID, user.getRelayGuid());
            }
            if (!Strings.isNullOrEmpty(user.getEmployeeId()))
            {
                attrs.put(LDAP_ATTR_CRU_EMPLOYEE_ID, user.getEmployeeId());
            }
            if (!Strings.isNullOrEmpty(user.getDepartmentNumber()))
            {
                attrs.put(LDAP_ATTR_CRU_DEPARTMENT_NUMBER, user.getDepartmentNumber());
            }
            if (!Strings.isNullOrEmpty(user.getCruDesignation()))
            {
                attrs.put(LDAP_ATTR_CRU_DESIGNATION, user.getCruDesignation());
            }
            if (!Strings.isNullOrEmpty(user.getCruEmployeeStatus()))
            {
                attrs.put(LDAP_ATTR_CRU_EMPLOYEE_STATUS, user.getCruEmployeeStatus());
            }
            if (!Strings.isNullOrEmpty(user.getCruGender()))
            {
                attrs.put(LDAP_ATTR_CRU_GENDER, user.getCruGender());
            }
            if (!Strings.isNullOrEmpty(user.getCruHrStatusCode()))
            {
                attrs.put(LDAP_ATTR_CRU_HR_STATUS_CODE, user.getCruHrStatusCode());
            }
            if (!Strings.isNullOrEmpty(user.getCruJobCode()))
            {
                attrs.put(LDAP_ATTR_CRU_JOB_CODE, user.getCruJobCode());
            }
            if (!Strings.isNullOrEmpty(user.getCruManagerID()))
            {
                attrs.put(LDAP_ATTR_CRU_MANAGER_ID, user.getCruManagerID());
            }
            if (!Strings.isNullOrEmpty(user.getCruMinistryCode()))
            {
                attrs.put(LDAP_ATTR_CRU_MINISTRY_CODE, user.getCruMinistryCode());
            }
            if (!Strings.isNullOrEmpty(user.getCruPayGroup()))
            {
                attrs.put(LDAP_ATTR_CRU_PAY_GROUP, user.getCruPayGroup());
            }
            if (!Strings.isNullOrEmpty(user.getCruPreferredName()))
            {
                attrs.put(LDAP_ATTR_CRU_PREFERRED_NAME, user.getCruPreferredName());
            }
            if (!Strings.isNullOrEmpty(user.getCruSubMinistryCode()))
            {
                attrs.put(LDAP_ATTR_CRU_SUB_MINISTRY_CODE, user.getCruSubMinistryCode());
            }
			if (!Strings.isNullOrEmpty(user.getCity()))
			{
				attrs.put(LDAP_ATTR_CITY, user.getCity());
			}
			if (!Strings.isNullOrEmpty(user.getState()))
			{
				attrs.put(LDAP_ATTR_STATE, user.getState());
			}
			if (!Strings.isNullOrEmpty(user.getPostal()))
			{
				attrs.put(LDAP_ATTR_POSTAL_CODE, user.getPostal());
			}
			if (!Strings.isNullOrEmpty(user.getCountry()))
			{
				attrs.put(LDAP_ATTR_COUNTRY, user.getCountry());
			}
			if (!Strings.isNullOrEmpty(user.getWorkPhone()))
			{
				attrs.put(LDAP_ATTR_TELEPHONE, user.getWorkPhone());
			}
			if (!Strings.isNullOrEmpty(user.getWorkPhoneExtension()))
			{
				attrs.put(LDAP_ATTR_TELEX, user.getWorkPhoneExtension());
			}
			if(!user.getProxyAddresses().isEmpty())
			{
				for(String proxyAddress : user.getProxyAddresses())
				{
					attrs.put(LDAP_ATTR_CRU_PROXY_ADDRESSES, proxyAddress);
				}
			}
		}

        // Dump the generated attributes if debug mode is enabled
        if (LOG.isDebugEnabled()) {
            LOG.debug("***** GcxUser LDAP: {}", LdapUtil.attributesToString(attrs));
        }

        // return the generated attributes
        return attrs;
    }

    private boolean hasCruAttributes(GcxUser gcxUser)
    {
        return !Strings.isNullOrEmpty(gcxUser.getEmployeeId()) ||
                !Strings.isNullOrEmpty(gcxUser.getDepartmentNumber()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruDesignation()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruEmployeeStatus()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruGender()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruHrStatusCode()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruJobCode()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruManagerID()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruMinistryCode()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruPayGroup()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruPreferredName()) ||
                !Strings.isNullOrEmpty(gcxUser.getRelayGuid()) ||
                !Strings.isNullOrEmpty(gcxUser.getCruSubMinistryCode()) ||
				!Strings.isNullOrEmpty(gcxUser.getCity()) ||
				!Strings.isNullOrEmpty(gcxUser.getState()) ||
				!Strings.isNullOrEmpty(gcxUser.getPostal()) ||
				!Strings.isNullOrEmpty(gcxUser.getCountry()) ||
				!Strings.isNullOrEmpty(gcxUser.getWorkPhone()) ||
				!Strings.isNullOrEmpty(gcxUser.getWorkPhoneExtension()) ||
				!gcxUser.getProxyAddresses().isEmpty() ||
				!Strings.isNullOrEmpty(gcxUser.getRelayGuid());
	}

    /**
     * @param user
     * @param context
     * @see AttributeBind#mapToContext(Object, DirContextOperations)
     */
    public void mapToContext(final GcxUser user, final DirContextOperations context) {
        // make sure a valid GcxUser is provided
        this.assertValidObject(user);

	/*
	 * The attribute for locking out a user is read-only, and shouldn't be
	 * set here.
	 */

        // add thekeyAttributes objectClass to this object
        context.addAttributeValue(LDAP_ATTR_OBJECTCLASS, LDAP_OBJECTCLASS_THEKEYATTRIBUTES);

        // set the attributes for this user
        context.setAttributeValue(LDAP_ATTR_FIRSTNAME, user.getFirstName());
        context.setAttributeValue(LDAP_ATTR_LASTNAME, user.getLastName());
        context.setAttributeValue(LDAP_ATTR_USERID, user.getUserid());

        context.setAttributeValue(LDAP_FLAG_ALLOWPASSWORDCHANGE, Boolean.toString(user.isPasswordAllowChange())
                .toUpperCase());
        context.setAttributeValue(LDAP_FLAG_LOGINDISABLED, Boolean.toString(user.isLoginDisabled()).toUpperCase());
        context.setAttributeValue(LDAP_FLAG_STALEPASSWORD, Boolean.toString(user.isForcePasswordChange()).toUpperCase
                ());
        context.setAttributeValue(LDAP_FLAG_VERIFIED, Boolean.toString(user.isVerified()).toUpperCase());

        // set any self-service keys
        context.setAttributeValue(LDAP_ATTR_SIGNUPKEY, user.getSignupKey());
        context.setAttributeValue(LDAP_ATTR_CHANGEEMAILKEY, user.getChangeEmailKey());
        context.setAttributeValue(LDAP_ATTR_PROPOSEDEMAIL, user.getProposedEmail());
        context.setAttributeValue(LDAP_ATTR_RESETPASSWORDKEY, user.getResetPasswordKey());

        final String password = user.getPassword();
        if (StringUtils.isNotBlank(password)) {
            context.setAttributeValue(LDAP_ATTR_PASSWORD, password);
        }

        final Date loginTime = user.getLoginTime();
        if (loginTime != null) {
            context.setAttributeValue(LDAP_ATTR_LOGINTIME, this.convertToGeneralizedTime(loginTime));
        }

        // set the multi-valued attributes
        context.setAttributeValues(LDAP_ATTR_DOMAINSVISITED, user.getDomainsVisited().toArray());
        context.setAttributeValues(LDAP_ATTR_ADDITIONALGUIDS, user.getGUIDAdditional().toArray());
        context.setAttributeValues(LDAP_ATTR_ADDITIONALDOMAINSVISITED, user.getDomainsVisitedAdditional().toArray());

        // set any federated identities
        final String facebookId = user.getFacebookId();
        context.setAttributeValue(LDAP_ATTR_FACEBOOKID, facebookId);
        context.setAttributeValue(LDAP_ATTR_FACEBOOKIDSTRENGTH, facebookId != null ? encodeStrength(facebookId,
                user.getFacebookIdStrengthFor(facebookId)) : null);
    }
}
