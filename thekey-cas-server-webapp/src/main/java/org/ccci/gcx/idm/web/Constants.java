package org.ccci.gcx.idm.web;

/**
 * Constants used throughout the sso web application
 * @author ken burcham
 *
 */

public interface Constants 
{
	
	//source identifiers (used to indicate "source" of gcxuser interactions for auditing)
	public static final String SOURCEIDENTIFIER_ACTIVATION = "SelfService:Activation";
	public static final String SOURCEIDENTIFIER_FORCECHANGEPASSWORD = "SelfService:ForcePasswordChange";
	public static final String SOURCEIDENTIFIERUSERUPDATE = "SelfService:UserAccountUpdate";
	public static final String SOURCEIDENTIFIER_FORGOTPASSWORD = "SelfService:UserForgotPassword";
	public static final String SOURCEIDENTIFIER_SERVICEVALIDATOR = "ServiceValidator";
	public static final String SOURCEIDENTIFIER_LOGIN = "Login";



	
	//Viewnames
	public static final String VIEW_LOGIN = "login";
	public static final String VIEW_MESSAGE = "message";
	public static final String VIEW_FORCECHANGEPASSWORD = "forcePasswordChange";
	public static final String VIEW_PASSWORDJAVASCRIPT = "includePasswordJavascript";


	
	//Error messages
	public static final String ERROR_ACTIVATIONFAILED = "error.account.activationfailed";
	public static final String ERROR_FORCECHANGEPASSWORD = "error.account.forcechangepassword";
	public static final String ERROR_ACCOUNTDEACTIVATED = "error.account.deactivated";
	public static final String ERROR_LOGINDISABLED    = "error.login.disabled";
	public static final String ERROR_ACCOUNTLOCKED    = "error.account.locked";
	public static final String ERROR_AUTHENTICATIONFAILED = "error.account.authenticationfailed";
	public static final String ERROR_ACCOUNTNOTACTIVATED  = "error.account.notactivated";
	public static final String ERROR_CHANGEPASSWORDFAILED = "error.account.changepasswordfailed";
	public static final String ERROR_VALIDATIONWITHOUTSERVICE = "error.servicevalidation.noserviceprovided";
	public static final String ERROR_UPDATEFAILED = "error.account.updatefailed.nouser";
	public static final String ERROR_UPDATEFAILED_EMAILEXISTS = "error.account.updatefailed.emailexists";
	public static final String ERROR_PASSWORDREQUIRED= "required.password";
	public static final String ERROR_RETYPEREQUIRED = "required.retypePassword";
	public static final String ERROR_MISMATCHRETYPE = "mismatch.retypePassword";
	public static final String ERROR_PASSWORD_MINLENGTH = "error.password.minlength"; 
	public static final String ERROR_PASSWORD_MAXLENGTH = "error.password.maxlength";
	public static final String ERROR_UPPERREQUIRED = "error.password.upperrequired";
	public static final String ERROR_LOWERREQUIRED = "error.password.lowerrequired";
	public static final String ERROR_SYMBOLREQUIRED = "error.password.symbolrequired";
	public static final String ERROR_NUMBERREQUIRED = "error.password.numberrequired";
	public static final String ERROR_SENDFORGOTFAILED = "selfserve.forgotpassword.message";

	public static final String MESSAGE_VIEW_TITLE = "messagetitle";
	public static final String MESSAGE_VIEW_NOTICE = "messagenotice";
	public static final String MESSAGE_VIEW_MESSAGE = "messagemessage";
	public static final String MESSAGE_VIEW_TITLE_CODE = "activation.message.title";
	public static final String MESSAGE_VIEW_NOTICE_CODE = "activation.message.notice";
	
	
	//parameter names
	public static final String ACTIVATIONPARAMETER_USERNAME = "u";
	public static final String ACTIVATIONPARAMETER_PIN = "k";
	public static final String SESSIONATTRIBUTE_LOCATION = "location";
	public static final String SESSIONATTRIBUTE_LOGIN = "signup_username";
	public static final String SESSIONATTRIBUTE_LOGINUSER = "signup_user";
	public static final String SESSIONATTRIBUTE_SERVICE = "service";
	public static final String SESSIONATTRIBUTE_CLIENTJAVASCRIPT = "clientjavascript";
	
	public static final String MODEL_LOGINUSER = "user";
	
	public static final String CSSSERVICEURL = "css.htm?css=";
	
	public static final String REQUESTPARAMETER_SERVICE="service";
	public static final String REQUESTPARAMETER_TICKET="ticket";
	public static final String REQUESTPARAMETER_CANCEL = "cancel";
	public static final String REQUESTPARAMETER_PGTURL = "pgtUrl";
	public static final String REQUESTPARAMETER_TEMPLATE = "template";
	public static final String REQUESTPARAMETER_PGT = "pgt";
	public static final String REQUESTPARAMETER_TARGETSERVICE = "targetService";
	public static final String REQUESTPARAMETER_CSS = "css";
	public static final String REQUESTPARAMETER_SIGNIN = "signin";
	public static final String REQUESTPARAMETER_CSSRELOAD = "reloadcss";


	public static final String GLOBALMESSAGEPARAMETER="message";
	
	
	//template default location
	public static final String DEFAULTCONVENTIONLOCATION = "/sso/template.css";
	
	//service defaults
	public static final String DEFAULTSERVICEURL = "https://www.mygcx.org/system/screen/dashboard";
	public static final String DEFAULTSERVICEPROTOCOL = "https://";
	
	public static final String CAS_ATTRIBUTE = "cas:attributes";
	public static final String CAS_ATTRIBUTE_NAME = "name";
	public static final String CAS_ATTRIBUTE_VALUE = "value";
	public static final String CAS_ATTRIBUTE_USER_CLOSE = "</cas:user>";
	
	public static final String ACCOUNT_UPDATESUCCESS = "selfserve.complete.notice";
	public static final String ACCOUNT_UPDATESUCCESS_RESETPASSWORD = "selfserve.complete.notice";
	public static final int DEFAULTPROXY = 8080;
	public static final String CONFIGSERVERELEMENT = "server";
	public static final String CONFIGPASSWORD_MINLENGTH = "minLength";
	public static final String CONFIGPASSWORD_MAXLENGTH = "maxLength";
	public static final String CONFIGPASSWORD_HAVEUPPERCASE = "haveUpperCase";
	public static final String CONFIGPASSWORD_HAVELOWERCASE = "haveLowerCase";
	public static final String CONFIGPASSWORD_HAVESYMBOL = "haveSymbol";
	public static final String CONFIGPASSWORD_HAVENUMBER = "haveNumber";
	public static final String CONFIGPASSWORD_HAVEMINMIX = "haveMinMix";
	
	public static final int USERNAME_MAXLENGTH = 80;
	public static final int PIN_MAXLENGTH = 10;
	
	//HEALTH
	public static final String HEALTH_USERSVC_TESTUSER = "kenburcham@gmail.com";
	public static final String HEALTH_USERSVC_TESTTRANSITIONALUSER = "donotdelete@gcxhealthtest.com";
	public static final String VIEW_HEALTH = "HealthViewer";
	public static final String MODEL_HEALTHSTATUSLIST = "statuslist";
	public static final String SESSION_LANGUAGE_LIST = "languagelist";
	public static final String SESSION_CURRENTLOCALE = "currentlocale";
	public static final String SESSIONATTRIBUTE_ADMIN = "adminusername";
	
	public static final String BEAN_LOGINFORMCONTROLLER = "loginFormController";
	public static final String CONFIGPASSWORD_BLACKLIST = "blacklist";
	public static final String REQUESTPARAMETER_GATEWAY = "gateway";
	public static final Object REQUESTPARAMETER_GATEWAY_TRUE = "true";
	public static final String REQUESTPARAMETER_LOGOUTCALLBACK = "logoutCallback";
	public static final String SESSIONATTRIBUTE_TICKET = "ticket";
	public static final String REQUESTPARAMETER_USERNAME = "username";
	public static final String RESPONSEHEADER_TICKET = "CAS-Ticket";
	public static final String RESPONSEHEADER_SERVICE = "CAS-Service";
	public static final String REQUESTPARAMETER_SUBMITFORM = "submit";
	public static final String REQUESTPARAMETER_PASSWORD = "password";
	public static final String DEFAULT_MESSAGES_LOCATION = "messages/idm_languages.properties";
	
}
