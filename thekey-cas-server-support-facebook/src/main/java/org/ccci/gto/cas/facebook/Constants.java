package org.ccci.gto.cas.facebook;

public final class Constants {
    public static final String PRINCIPAL_ATTR_ACCESSTOKEN = "facebook.accessToken";

    public static final String PARAMETER_SIGNED_REQUEST = "fb_signed_request";

    // Error Codes
    public static final String ERROR_ACCOUNTALREADYLINKED = "facebook.error.accountalreadylinked";
    public static final String ERROR_FACEBOOKVIVIFYERROR = "facebook.error.vivifyfailed";

    // Link strength values
    public static final Double STRENGTH_MATCHINGEMAIL = 0.25;
    public static final Double STRENGTH_AUTOCREATE = 0.5;
}
