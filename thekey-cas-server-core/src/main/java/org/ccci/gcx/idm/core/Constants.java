package org.ccci.gcx.idm.core;

/**
 * <b>Constants</b> contains the common constants used throughout the IDM application.
 *
 * @author Greg Crider  Oct 17, 2008  6:29:26 PM
 */
public interface Constants
{

    // BEAN OBJECTS
    
    public static final String BEAN_AUDIT_DAO = "persist.auditDao" ;
    
    public static final String BEAN_AUDIT_SERVICE = "service.auditService" ;
    
    public static final String BEAN_TRANS_GCXUSER_DAO = "persist.gcxUserDao" ;
    
    public static final String BEAN_GCXUSER_DAO = "ldap.gcxUserDao" ;
    
    public static final String BEAN_GCXUSER_SERVICE = "service.gcxUserService" ;
    
    public static final String BEAN_AUTHENTICATION_SERVICE = "service.authenticationService";

    public static final String BEAN_RANDOM_PASSWORD = "util.randomPassword" ;
    
    public static final String BEAN_MAIL_SENDER = "mail.mailSender" ;
    
    public static final String BEAN_MAIL_SERVICE = "service.mailService" ;
    
    public static final String BEAN_MESSAGE_SOURCE = "util.messageSource" ;
    
    public static final String BEAN_LOCALE = "util.Locale" ;
    
    // HIBERNATE QUERY NAMES
    
    public static final String QUERY_AUDIT_FINDALLBYUSERID = "org.ccci.gcx.idm.core.model.impl.Audit.findAllByUserid" ;
    
    public static final String QUERY_GCXUSER_FINDBYGUID = "org.ccci.gcx.idm.core.model.impl.GcxUser.findByGuid" ;
    
    public static final String QUERY_GCXUSER_FINDBYEMAIL = "org.ccci.gcx.idm.core.model.impl.GcxUser.findByEmail" ;
    
    // LDAP
    public static final String LDAP_KEY_PASSWORD = "userPassword" ;
    
    // SEARCH
    
    public static final int SEARCH_NO_LIMIT = 0 ;
    
    // MISC
    
    public static final String PREFIX_DEACTIVATED = "$GUID$-" ;
    
    public static final String DEFAULT_COUNTRY_CODE = "EN" ;
    
    public static final String INTERNAL_SOURCE = "INTERNAL" ;
    
    public static final String INTERNAL_CREATEDBY = "INTERNAL" ;
    
    //Authentication
	public static final String LOGIN_URL = "/login";
	public static final String LOGOUT_URL = "/logout";
	
	public static final String TICKET_BEGIN = "ticket=";
	public static final String URL_BEGIN = "service=";
	public static final String LT_REGEX = ".*name=\"lt\".value=\"(.*?)\"";
	public static final String DEFAULTSERVICEPROTOCOL ="http://";
	public static final String DEFAULTCASSERVICEPROTOCOL = "https://";
	
	public static final String SERVICE_VALIDATE_URL = "/serviceValidate";
	public static final String PROXY_VALIDATE_URL = "/proxyValidate";
	public static final String PROXY_TICKET_URL = "/proxy";
	public static final String SYMBOL_AMP = "&";
	public static final String PGTURL_BEGIN = "pgtUrl=";

	public static final String CAS_SERVICE = "service";
	public static final String CAS_TICKET  = "ticket";
	public static final String CAS_PGT     = "pgtUrl";
	public static final String CAS_EVENTID = "_eventId";
	public static final String CAS_USERNAME = "username";
	public static final String CAS_PASSWORD = "password";
	public static final String CAS_LOGINTICKET = "lt";
	public static final String CAS_GATEWAY    = "gateway";
	public static final String CAS_LOCATIONHEADER = "location";
	public static final String CAS_PROXY_TARGETSERVICE = "targetService=";
	public static final String CAS_PROXY_TARGETSERVICE_PARAM = "targetService";
	public static final String CAS_PROXY_PGT_PARAM = "pgt";
	public static final String CAS_PROXY_PGT = "pgt=";
	
	public static final String CAS_TGC	= "CASTGC";
	public static final String CAS_COOKIEPATH = "/internal";
	public static final String SERVICE_PROCESSTGC = "processTGCRequest";
	
	public static final int    DEFAULTPROXY = 8080;
	public static final String CAS_GATEWAYPARM = "&gateway=true";
	
	public static final String ERROR_NOCREDENTIALORPRINCIPAL = "error.nocredentialorprincipal";
	public static final String ERROR_SERVICEEMPTY = "error.servicevalidate.serviceempty";
	public static final String ERROR_TICKETEMPTY = "error.servicevalidate.ticketempty";
	public static final String ERROR_VALIDATIONFAILED = "error.servicevalidate.failed";
	public static final String ERROR_NOCOOKIEAFTERAUTH = "error.login.nocookieafterauth";

	//public static final String CAS_COOKIEDOMAIN = ".mygcx.org";
	public static final String CAS_DEFAULTCOOKIEDOMAIN = "localhost";

	public static final String CAS_LOGOUTCALLBACK = "logoutCallback";

	public static final int DEFAULTCOOKIEEXPIRY = 14400;

	

}
