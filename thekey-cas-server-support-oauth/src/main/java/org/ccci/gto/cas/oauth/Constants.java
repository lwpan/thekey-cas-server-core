package org.ccci.gto.cas.oauth;

public final class Constants {
    // OAuth request/response params
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_CLIENT_ID = "client_id";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_EXPIRES_IN = "expires_in";
    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String PARAM_THEKEY_GUID = "thekey_guid";
    public static final String PARAM_REDIRECT_URI = "redirect_uri";
    public static final String PARAM_REFRESH_TOKEN = "refresh_token";
    public static final String PARAM_RESPONSE_TYPE = "response_type";
    public static final String PARAM_SCOPE = "scope";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_TOKEN_TYPE = "token_type";

    // OAuth grant types
    public static final String GRANT_TYPE_CODE = "authorization_code";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    // OAuth response types
    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String RESPONSE_TYPE_TOKEN = "token";

    // OAuth token types
    public static final String TOKEN_TYPE_BEARER = "bearer";

    // OAuth error's
    public static final String ERROR_INVALID_GRANT = "invalid_grant";
    public static final String ERROR_INVALID_SCOPE = "invalid_scope";
    public static final String ERROR_SERVER_ERROR = "server_error";
    public static final String ERROR_UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    public static final String ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";

    // OAuth access_token supported scope values
    public static final String SCOPE_FULLTICKET = "fullticket";

    // FlowScope attributes
    public static final String FLOW_ATTR_CLIENT = "oauthClient";
    public static final String FLOW_ATTR_PARAMS = "oauthParams";
    public static final String FLOW_ATTR_REDIRECT_URI = "oauthRedirectUri";
    public static final String FLOW_ATTR_SCOPE = "oauthScope";
}
