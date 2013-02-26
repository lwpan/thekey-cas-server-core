package org.ccci.gto.cas.federation;

public final class Constants {
    // Authentication attributes
    public static final String AUTH_ATTR_PROXYPROVIDER = "org.ccci.gto.cas.ProxiedCredentialsUri";
    public static final String AUTH_ATTR_REQUIREPROXYVALIDATION = "org.ccci.gto.cas.RequireProxyValidation";

    // Error Codes
    public static final String ERROR_UNKNOWNIDENTITY = "federation.error.unknownidentity";

    // Error Types (used in realSubmit in the login webflow)
    public static final String ERROR_TYPE_UNKNOWNIDENTITY = "unknownIdentity";
}
