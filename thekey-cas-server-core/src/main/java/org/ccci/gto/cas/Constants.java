package org.ccci.gto.cas;

public final class Constants {
    // Request Parameters
    public static final String PARAMETER_ACTIVATION_FLAG = "activate";
    public static final String PARAMETER_ACTIVATION_FLAGVALUE = "true";
    public static final String PARAMETER_ACTIVATION_USERNAME = "u";
    public static final String PARAMETER_ACTIVATION_KEY = "k";

    // Error Codes
    public static final String ERROR_STALEPASSWORD = "error.account.forcechangepassword";

    // LDAP Attributes
    public static final String LDAP_ATTR_EMAIL = "cn";
    public static final String LDAP_ATTR_GUID = "extensionAttribute1";
    public static final String LDAP_ATTR_FIRSTNAME = "givenName";
    public static final String LDAP_ATTR_LASTNAME = "sn";
    public static final String LDAP_ATTR_LOGINTIME = "loginTime";
    public static final String LDAP_ATTR_USERID = "uid";
    public static final String LDAP_ATTR_DOMAINSVISITED = "extensionAttribute2";
    public static final String LDAP_ATTR_ADDITIONALGUIDS = "extensionAttribute3";
    public static final String LDAP_ATTR_ADDITIONALDOMAINSVISITED = "extensionAttribute4";
    public static final String LDAP_ATTR_GROUPS = "groupMembership";
    public static final String LDAP_ATTR_ALLOWPASSWORDCHANGE = "passwordAllowChange";
    public static final String LDAP_ATTR_LOGINDISABLED = "loginDisabled";
    public static final String LDAP_ATTR_LOCKED = "lockedByIntruder";
    public static final String LDAP_ATTR_STALEPASSWORD = "extensionAttribute5";
}
