package org.ccci.gcx.idm.core.persist.ldap.bind.impl;

import static org.ccci.gto.cas.Constants.LDAP_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.LDAP_ATTR_OBJECTCLASS;
import static org.ccci.gto.cas.Constants.LDAP_OBJECTCLASS_THEKEYATTRIBUTES;

import java.util.Date;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.util.LdapUtil;
import org.ccci.gto.cas.Constants;
import org.ccci.gto.cas.persist.ldap.bind.AttributeBind;
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
    // LDAP Attributes in use
    private static final String ATTR_OBJECTCLASS = Constants.LDAP_ATTR_OBJECTCLASS;
    private static final String ATTR_EMAIL = Constants.LDAP_ATTR_EMAIL;
    private static final String ATTR_GUID = Constants.LDAP_ATTR_GUID;
    private static final String ATTR_PASSWORD = Constants.LDAP_ATTR_PASSWORD;
    private static final String ATTR_FIRSTNAME = Constants.LDAP_ATTR_FIRSTNAME;
    private static final String ATTR_LASTNAME = Constants.LDAP_ATTR_LASTNAME;
    private static final String ATTR_LOGINTIME = Constants.LDAP_ATTR_LOGINTIME;
    private static final String ATTR_USERID = Constants.LDAP_ATTR_USERID;
    private static final String ATTR_DOMAINSVISITED = Constants.LDAP_ATTR_DOMAINSVISITED;
    private static final String ATTR_ADDITIONALGUIDS = Constants.LDAP_ATTR_ADDITIONALGUIDS;
    private static final String ATTR_ADDITIONALDOMAINSVISITED = Constants.LDAP_ATTR_ADDITIONALDOMAINSVISITED;
    private static final String FLAG_ALLOWPASSWORDCHANGE = Constants.LDAP_ATTR_ALLOWPASSWORDCHANGE;
    private static final String FLAG_LOGINDISABLED = Constants.LDAP_ATTR_LOGINDISABLED;
    private static final String FLAG_STALEPASSWORD = Constants.LDAP_ATTR_STALEPASSWORD;
    private static final String FLAG_VERIFIED = Constants.LDAP_ATTR_VERIFIED;

    // LDAP objectClass values
    private static final String OBJECTCLASS_TOP = Constants.LDAP_OBJECTCLASS_TOP;
    private static final String OBJECTCLASS_PERSON = Constants.LDAP_OBJECTCLASS_PERSON;
    private static final String OBJECTCLASS_NDSLOGIN = Constants.LDAP_OBJECTCLASS_NDSLOGIN;
    private static final String OBJECTCLASS_ORGANIZATIONALPERSON = Constants.LDAP_OBJECTCLASS_ORGANIZATIONALPERSON;
    private static final String OBJECTCLASS_INETORGPERSON = Constants.LDAP_OBJECTCLASS_INETORGPERSON;

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
	final Attribute objectClass = new BasicAttribute(ATTR_OBJECTCLASS);
	objectClass.add(OBJECTCLASS_TOP);
	objectClass.add(OBJECTCLASS_PERSON);
	objectClass.add(OBJECTCLASS_NDSLOGIN);
	objectClass.add(OBJECTCLASS_ORGANIZATIONALPERSON);
	objectClass.add(OBJECTCLASS_INETORGPERSON);
	objectClass.add(LDAP_OBJECTCLASS_THEKEYATTRIBUTES);
	attrs.put(objectClass);

	// set the attributes for this user
	attrs.put(ATTR_EMAIL, user.getEmail());
	attrs.put(ATTR_GUID, user.getGUID());
	attrs.put(ATTR_FIRSTNAME, user.getFirstName());
	attrs.put(ATTR_LASTNAME, user.getLastName());
	attrs.put(ATTR_USERID, user.getUserid());

	attrs.put(FLAG_ALLOWPASSWORDCHANGE,
		Boolean.toString(user.isPasswordAllowChange()).toUpperCase());
	attrs.put(FLAG_LOGINDISABLED, Boolean.toString(user.isLoginDisabled())
		.toUpperCase());
	attrs.put(FLAG_STALEPASSWORD,
		Boolean.toString(user.isForcePasswordChange()).toUpperCase());
	attrs.put(FLAG_VERIFIED, Boolean.toString(user.isVerified())
		.toUpperCase());

	final String password = user.getPassword();
	if (StringUtils.isNotBlank(password)) {
	    attrs.put(ATTR_PASSWORD, password);
	}
	final Date loginTime = user.getLoginTime();
	if (loginTime != null) {
	    attrs.put(ATTR_LOGINTIME, this.convertToGeneralizedTime(loginTime));
	}

	// set the multi-valued attributes
	this.addAttributeList(attrs, ATTR_DOMAINSVISITED,
		user.getDomainsVisited(), false);
	this.addAttributeList(attrs, ATTR_ADDITIONALGUIDS,
		user.getGUIDAdditional(), false);
	this.addAttributeList(attrs, ATTR_ADDITIONALDOMAINSVISITED,
		user.getDomainsVisitedAdditional(), false);

        // set any federated identities
        if (user.getFacebookId() != null) {
            attrs.put(LDAP_ATTR_FACEBOOKID, user.getFacebookId());
        }

	// Dump the generated attributes if debug mode is enabled
	if (log.isDebugEnabled()) {
	    log.debug("***** GcxUser LDAP: "
		    + LdapUtil.attributesToString(attrs));
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
	context.setAttributeValue(ATTR_FIRSTNAME, user.getFirstName());
	context.setAttributeValue(ATTR_LASTNAME, user.getLastName());
	context.setAttributeValue(ATTR_USERID, user.getUserid());

	context.setAttributeValue(FLAG_ALLOWPASSWORDCHANGE,
		Boolean.toString(user.isPasswordAllowChange()).toUpperCase());
	context.setAttributeValue(FLAG_LOGINDISABLED,
		Boolean.toString(user.isLoginDisabled()).toUpperCase());
	context.setAttributeValue(FLAG_STALEPASSWORD,
		Boolean.toString(user.isForcePasswordChange()).toUpperCase());
	context.setAttributeValue(FLAG_VERIFIED,
		Boolean.toString(user.isVerified()).toUpperCase());
	final String password = user.getPassword();
	if (StringUtils.isNotBlank(password)) {
	    context.setAttributeValue(ATTR_PASSWORD, password);
	}

	// set the multi-valued attributes
	context.setAttributeValues(ATTR_DOMAINSVISITED, user
		.getDomainsVisited().toArray());
	context.setAttributeValues(ATTR_ADDITIONALGUIDS, user
		.getGUIDAdditional().toArray());
	context.setAttributeValues(ATTR_ADDITIONALDOMAINSVISITED, user
		.getDomainsVisitedAdditional().toArray());

        // set any federated identities
        context.setAttributeValue(LDAP_ATTR_FACEBOOKID, user.getFacebookId());
    }
}
