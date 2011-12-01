package org.ccci.gto.cas.selfservice;

public class Constants {
    // WebFlow related constants
    public static final String FLOW_MODEL_SELFSERVICEUSER = "user";

    // View attribute keys
    public static final String VIEW_ATTR_PASSWORDRULES = "jsonPasswordRules";

    // Message codes
    public static final String MESSAGE_UPDATESUCCESS = "selfserve.complete.notice";
    public static final String MESSAGE_UPDATESUCCESS_RESETPASSWORD = "selfserve.complete.notice";

    // Error codes
    public static final String ERROR_SENDFORGOTFAILED = "selfserve.forgotpassword.message";
    public static final String ERROR_PASSWORD_MAXLENGTH = "error.password.maxlength";
    public static final String ERROR_PASSWORD_MINLENGTH = "error.password.minlength";
    public static final String ERROR_PASSWORD_MISMATCHRETYPE = "mismatch.retypePassword";
    public static final String ERROR_PASSWORD_UPPERREQUIRED = "error.password.upperrequired";
    public static final String ERROR_PASSWORD_LOWERREQUIRED = "error.password.lowerrequired";
    public static final String ERROR_PASSWORD_SYMBOLREQUIRED = "error.password.symbolrequired";
    public static final String ERROR_PASSWORD_NUMBERREQUIRED = "error.password.numberrequired";

    // Audit constants
    public static final String AUDIT_SOURCE_FORCECHANGEPASSWORD = "SelfService:ForcePasswordChange";
    public static final String AUDIT_SOURCE_FORGOTPASSWORD = "SelfService:UserForgotPassword";
    public static final String AUDIT_SOURCE_SIGNUP = "SelfService:Signup";
    public static final String AUDIT_SOURCE_USERUPDATE = "SelfService:UserAccountUpdate";
}
