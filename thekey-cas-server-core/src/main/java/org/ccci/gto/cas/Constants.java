package org.ccci.gto.cas;

public final class Constants {
    // Audit Source identifiers
    public static final String AUDIT_SOURCE_SERVICEVALIDATOR = "ServiceValidator";

    // Request Parameters
    public static final String PARAMETER_ACTIVATION_FLAG = "activate";
    public static final String PARAMETER_ACTIVATION_FLAGVALUE = "true";
    public static final String PARAMETER_ACTIVATION_USERNAME = "u";
    public static final String PARAMETER_ACTIVATION_KEY = "k";
    public static final String PARAMETER_LOGINTICKET = "lt";

    // Error Codes
    public static final String ERROR_STALEPASSWORD = "error.account.forcechangepassword";
    public static final String ERROR_FACEBOOKIDALREADYLINKED = "error.account.facebook.idalreadylinked";
    public static final String ERROR_UPDATEFAILED_NOUSER = "error.account.updatefailed.nouser";
    public static final String ERROR_UPDATEFAILED_EMAILEXISTS = "duplicate.username";
    public static final String ERROR_EMAILREQUIRED = "required.username";
    public static final String ERROR_PASSWORDREQUIRED = "required.password";
    public static final String ERROR_FIRSTNAMEREQUIRED = "required.firstName";
    public static final String ERROR_LASTNAMEREQUIRED = "required.lastName";
    public static final String ERROR_INVALIDEMAIL = "invalid.username";

    // Authentication/Principal attribute keys
    public static final String AUTH_ATTR_KEYUSER = "org.ccci.gto.cas.KeyUser";
    public static final String AUTH_ATTR_PROXYPROVIDER = "org.ccci.gto.cas.ProxiedCredentialsUri";
    public static final String PRINCIPAL_ATTR_GUID = "guid";
    public static final String PRINCIPAL_ATTR_ADDITIONALGUIDS = "additionalGuid";
    public static final String PRINCIPAL_ATTR_EMAIL = "email";
    public static final String PRINCIPAL_ATTR_FIRSTNAME = "firstName";
    public static final String PRINCIPAL_ATTR_LASTNAME = "lastName";

    // View attribute keys
    public static final String VIEW_ATTR_SERVICEDOMAIN = "serviceDomain";

    // Miscellaneous constants
    public static final String ACCOUNT_DEACTIVATEDPREFIX = "$GUID$-";

    // LDAP constants
    public static final int LDAP_NOSEARCHLIMIT = 0;

    // LDAP Attributes
    public static final String LDAP_ATTR_EMAIL = "cn";
    public static final String LDAP_ATTR_GUID = "extensionAttribute1";
    public static final String LDAP_ATTR_PASSWORD = "userPassword";
    public static final String LDAP_ATTR_FIRSTNAME = "givenName";
    public static final String LDAP_ATTR_LASTNAME = "sn";
    public static final String LDAP_ATTR_LOGINTIME = "loginTime";
    public static final String LDAP_ATTR_FACEBOOKID = "thekeyFacebookId";
    public static final String LDAP_ATTR_USERID = "uid";
    public static final String LDAP_ATTR_DOMAINSVISITED = "extensionAttribute2";
    public static final String LDAP_ATTR_ADDITIONALGUIDS = "extensionAttribute3";
    public static final String LDAP_ATTR_ADDITIONALDOMAINSVISITED = "extensionAttribute4";
    public static final String LDAP_ATTR_GROUPS = "groupMembership";
    public static final String LDAP_ATTR_ALLOWPASSWORDCHANGE = "passwordAllowChange";
    public static final String LDAP_ATTR_LOGINDISABLED = "loginDisabled";
    public static final String LDAP_ATTR_LOCKED = "lockedByIntruder";
    public static final String LDAP_ATTR_STALEPASSWORD = "extensionAttribute5";
    public static final String LDAP_ATTR_VERIFIED = "thekeyAccountVerified";
    public static final String LDAP_ATTR_OBJECTCLASS = "objectClass";

    // LDAP objectClass values
    public static final String LDAP_OBJECTCLASS_TOP = "Top";
    public static final String LDAP_OBJECTCLASS_PERSON = "Person";
    public static final String LDAP_OBJECTCLASS_NDSLOGIN = "ndsLoginProperties";
    public static final String LDAP_OBJECTCLASS_ORGANIZATIONALPERSON = "organizationalPerson";
    public static final String LDAP_OBJECTCLASS_INETORGPERSON = "inetOrgPerson";
}
