package org.ccci.gto.cas;

import java.util.regex.Pattern;

public final class Constants {
    // Audit Source identifiers
    public static final String AUDIT_SOURCE_SERVICEVALIDATOR = "ServiceValidator";

    // Request Parameters
    public static final String PARAMETER_ACTIVATION_FLAG = "activate";
    public static final String PARAMETER_ACTIVATION_FLAGVALUE = "true";
    public static final String PARAMETER_ACTIVATION_USERNAME = "u";
    public static final String PARAMETER_ACTIVATION_KEY = "k";
    public static final String PARAMETER_LOGINTICKET = "lt";
    public static final String PARAM_SERVICE = "service";

    // Error Codes
    public static final String ERROR_UNVERIFIED = "error.account.unverified";
    public static final String ERROR_STALEPASSWORD = "error.account.forcechangepassword";
    public static final String ERROR_FACEBOOKIDALREADYLINKED = "error.account.facebook.idalreadylinked";
    public static final String ERROR_UPDATEFAILED_NOUSER = "error.account.updatefailed.nouser";
    public static final String ERROR_UPDATEFAILED_EMAILEXISTS = "duplicate.username";
    public static final String ERROR_EMAILREQUIRED = "required.username";
    public static final String ERROR_PASSWORDREQUIRED = "required.password";
    public static final String ERROR_FIRSTNAMEREQUIRED = "required.firstName";
    public static final String ERROR_LASTNAMEREQUIRED = "required.lastName";
    public static final String ERROR_INVALIDEMAIL = "invalid.username";

    // Error Types (used in realSubmit in the login webflow)
    public static final String ERROR_TYPE_STALEPASSWORD = "stalePassword";

    // Authentication/Credentials/Principal attribute keys
    public static final String AUTH_ATTR_KEYUSER = "me.thekey.cas.KeyUser";
    public static final String PRINCIPAL_ATTR_GUID = "guid";
    public static final String PRINCIPAL_ATTR_ADDITIONALGUIDS = "additionalGuid";
    public static final String PRINCIPAL_ATTR_FACEBOOKID = "facebookId";
    public static final String PRINCIPAL_ATTR_EMAIL = "email";
    public static final String PRINCIPAL_ATTR_FIRSTNAME = "firstName";
    public static final String PRINCIPAL_ATTR_LASTNAME = "lastName";

    // Supported view basenames
    public static final String VIEW_BASENAME_THEKEY = "thekey_views";
    public static final String VIEW_BASENAME_THEKEY_V2 = "thekey_v2_views";
    public static final String VIEW_BASENAME_THEKEY_V4 = "thekey_v4_views";

    // base strength values
    public static final double STRENGTH_NONE = 0.0;
    public static final double STRENGTH_FULL = 1.0;
    @Deprecated
    public static final double STRENGTH_LEGACYFACEBOOK = 0.25;

    // View attribute keys
    public static final String VIEW_ATTR_COMMONURIPARAMS = "commonUriParams";
    public static final String VIEW_ATTR_REQUESTURI = "requestUri";
    public static final String VIEW_ATTR_SERVICEDOMAIN = "serviceDomain";
    public static final String VIEW_ATTR_LOCALE = "locale";

    // Miscellaneous constants
    public static final Pattern VALIDGUIDREGEX = Pattern
	    .compile("^[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}$");
    public static final String ACCOUNT_DEACTIVATEDPREFIX = "$GUID$-";

    // LDAP constants
    public static final int LDAP_NOSEARCHLIMIT = 0;

    // LDAP Attributes
    public static final String LDAP_ATTR_OBJECTCLASS = "objectClass";
    public static final String LDAP_ATTR_EMAIL = "cn";
    public static final String LDAP_ATTR_GUID = "extensionAttribute1";
    public static final String LDAP_ATTR_PASSWORD = "userPassword";
    public static final String LDAP_ATTR_FIRSTNAME = "givenName";
    public static final String LDAP_ATTR_LASTNAME = "sn";
    public static final String LDAP_ATTR_LOGINTIME = "loginTime";
    public static final String LDAP_ATTR_FACEBOOKID = "thekeyFacebookId";
    public static final String LDAP_ATTR_FACEBOOKIDSTRENGTH = "thekeyFacebookIdStrength";
    public static final String LDAP_ATTR_USERID = "uid";
    public static final String LDAP_ATTR_DOMAINSVISITED = "extensionAttribute2";
    public static final String LDAP_ATTR_ADDITIONALGUIDS = "extensionAttribute3";
    public static final String LDAP_ATTR_ADDITIONALDOMAINSVISITED = "extensionAttribute4";
    public static final String LDAP_ATTR_GROUPS = "groupMembership";

    public static final String LDAP_ATTR_RESETPASSWORDKEY = "thekeyResetPasswordKey";
    public static final String LDAP_ATTR_SIGNUPKEY = "thekeySignupKey";
    public static final String LDAP_ATTR_CHANGEEMAILKEY = "thekeyChangeEmailKey";
    public static final String LDAP_ATTR_PROPOSEDEMAIL = "thekeyProposedEmail";

    public static final String LDAP_FLAG_ALLOWPASSWORDCHANGE = "passwordAllowChange";
    public static final String LDAP_FLAG_LOGINDISABLED = "loginDisabled";
    public static final String LDAP_FLAG_LOCKED = "lockedByIntruder";
    public static final String LDAP_FLAG_STALEPASSWORD = "extensionAttribute5";
    public static final String LDAP_FLAG_VERIFIED = "thekeyAccountVerified";

    // LDAP objectClass values
    public static final String LDAP_OBJECTCLASS_TOP = "Top";
    public static final String LDAP_OBJECTCLASS_PERSON = "Person";
    public static final String LDAP_OBJECTCLASS_NDSLOGIN = "ndsLoginProperties";
    public static final String LDAP_OBJECTCLASS_ORGANIZATIONALPERSON = "organizationalPerson";
    public static final String LDAP_OBJECTCLASS_INETORGPERSON = "inetOrgPerson";
    public static final String LDAP_OBJECTCLASS_THEKEYATTRIBUTES = "thekeyAttributes";
}
