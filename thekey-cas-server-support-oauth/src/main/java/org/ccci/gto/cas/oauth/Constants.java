package org.ccci.gto.cas.oauth;

public final class Constants {
    // OAuth request params
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_AUTHORIZATION_CODE = "code";
    public static final String PARAM_CLIENT_ID = "client_id";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_REDIRECT_URI = "redirect_uri";
    public static final String PARAM_RESPONSE_TYPE = "response_type";
    public static final String PARAM_STATE = "state";

    // OAuth response types
    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String RESPONSE_TYPE_TOKEN = "token";

    // OAuth error's
    public static final String ERROR_SERVER_ERROR = "server_error";
    public static final String ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";

    // OAuth access_token supported scope values
    public static final String SCOPE_TICKET = "ticket";

    // Authentication/Principal attribute keys
    public static final String AUTH_ATTR_GRANT = "org.ccci.gto.cas.oauth.Grant";

    // FlowScope attributes
    public static final String FLOW_ATTR_CLIENT = "oauthClient";
    public static final String FLOW_ATTR_PARAMS = "oauthParams";
    public static final String FLOW_ATTR_REDIRECT_URI = "oauthRedirectUri";
}
