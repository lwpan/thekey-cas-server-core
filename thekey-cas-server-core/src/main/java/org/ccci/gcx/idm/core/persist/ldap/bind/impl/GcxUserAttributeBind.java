package org.ccci.gcx.idm.core.persist.ldap.bind.impl;

import static org.ccci.gto.cas.Constants.LDAP_ATTR_ADDITIONALDOMAINSVISITED;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_DOMAINSVISITED;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FACEBOOKIDSTRENGTH;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_GUID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_LASTNAME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_LOGINTIME;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_OBJECTCLASS;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_PASSWORD;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_RELAYGUID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_RELAYGUIDSTRENGTH;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_USERID;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_ALLOWPASSWORDCHANGE;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_LOGINDISABLED;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_STALEPASSWORD;
import static org.ccci.gto.cas.Constants.LDAP_FLAG_VERIFIED;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_INETORGPERSON;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_NDSLOGIN;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_ORGANIZATIONALPERSON;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_PERSON;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_THEKEYATTRIBUTES;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_TOP;

import java.util.Date;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.util.LdapUtil;
import org.ccci.gto.cas.persist.ldap.bind.AttributeBind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.util.Assert;

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

    /**
     * @param object
     * @return
     * @see AttributeBind#build(Object)
     */
    public Attributes build(final GcxUser user) {
	// make sure a valid GcxUser is provided
	this.assertValidObject(user);

	/*
	 * The attribute for locking out a user is read-only, and shouldn't be
	 * set here.
	 */

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
            attrs.put(LDAP_ATTR_FACEBOOKIDSTRENGTH,
                    encodeStrength(facebookId, user.getFacebookIdStrengthFor(facebookId)));
        }
        final String relayGuid = user.getRelayGuid();
        if (relayGuid != null) {
            attrs.put(LDAP_ATTR_RELAYGUID, relayGuid);
            attrs.put(LDAP_ATTR_RELAYGUIDSTRENGTH, encodeStrength(relayGuid, user.getRelayGuidStrengthFor(relayGuid)));
        }

	// Dump the generated attributes if debug mode is enabled
        if (LOG.isDebugEnabled()) {
            LOG.debug("***** GcxUser LDAP: {}", LdapUtil.attributesToString(attrs));
	}

	// return the generated attributes
	return attrs;
    }

    /**
     * @param object
     * @param context
     * @see AttributeBind#mapToContext(Object, DirContextOperations)
     */
    public void mapToContext(final GcxUser user,
	    final DirContextOperations context) {
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
        context.setAttributeValue(LDAP_FLAG_STALEPASSWORD, Boolean.toString(user.isForcePasswordChange()).toUpperCase());
        context.setAttributeValue(LDAP_FLAG_VERIFIED, Boolean.toString(user.isVerified()).toUpperCase());
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
        context.setAttributeValue(LDAP_ATTR_FACEBOOKIDSTRENGTH,
                facebookId != null ? encodeStrength(facebookId, user.getFacebookIdStrengthFor(facebookId)) : null);
        final String relayGuid = user.getRelayGuid();
        context.setAttributeValue(LDAP_ATTR_RELAYGUID, relayGuid);
        context.setAttributeValue(LDAP_ATTR_RELAYGUIDSTRENGTH,
                relayGuid != null ? encodeStrength(relayGuid, user.getRelayGuidStrengthFor(relayGuid)) : null);
    }
}
