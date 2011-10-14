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
	public static final String ERROR_PASSWORDREQUIRED= "required.password";
	public static final String ERROR_RETYPEREQUIRED = "required.retypePassword";

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
	
	public static final String REQUESTPARAMETER_TEMPLATE = "template";
	public static final String REQUESTPARAMETER_TARGETSERVICE = "targetService";


	public static final String GLOBALMESSAGEPARAMETER="message";
	
	
	//template default location
	public static final String DEFAULTCONVENTIONLOCATION = "/sso/template.css";
	
	//service defaults
	public static final String DEFAULTSERVICEPROTOCOL = "https://";
	
	public static final int DEFAULTPROXY = 8080;
	public static final String CONFIGSERVERELEMENT = "server";
	
	public static final int USERNAME_MAXLENGTH = 80;
	
	//HEALTH
	public static final String SESSION_LANGUAGE_LIST = "languagelist";
	public static final String SESSION_CURRENTLOCALE = "currentlocale";
	
	public static final String REQUESTPARAMETER_GATEWAY = "gateway";
	public static final Object REQUESTPARAMETER_GATEWAY_TRUE = "true";
	public static final String REQUESTPARAMETER_LOGOUTCALLBACK = "logoutCallback";
	public static final String REQUESTPARAMETER_USERNAME = "username";
	public static final String REQUESTPARAMETER_SUBMITFORM = "submit";
	public static final String REQUESTPARAMETER_PASSWORD = "password";
	public static final String DEFAULT_MESSAGES_LOCATION = "messages/idm_languages.properties";
}
