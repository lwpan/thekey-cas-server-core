package org.ccci.gto.cas.selfservice;

public class Constants {
    // Message codes
    public static final String MESSAGE_UPDATESUCCESS = "selfserve.complete.notice";
    public static final String MESSAGE_UPDATESUCCESS_RESETPASSWORD = "selfserve.complete.notice";

    // Error codes
    public static final String ERROR_SENDFORGOTFAILED = "selfserve.forgotpassword.message";

    // Audit constants
    public static final String AUDIT_SOURCE_FORCECHANGEPASSWORD = "SelfService:ForcePasswordChange";
    public static final String AUDIT_SOURCE_FORGOTPASSWORD = "SelfService:UserForgotPassword";
    public static final String AUDIT_SOURCE_USERUPDATE = "SelfService:UserAccountUpdate";
}
