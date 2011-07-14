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
	public static final int    DEFAULTPROXY = 8080;
	
	public static final String ERROR_NOCREDENTIALORPRINCIPAL = "error.nocredentialorprincipal";
	public static final String ERROR_SERVICEEMPTY = "error.servicevalidate.serviceempty";
	public static final String ERROR_TICKETEMPTY = "error.servicevalidate.ticketempty";
	public static final String ERROR_VALIDATIONFAILED = "error.servicevalidate.failed";
	public static final String ERROR_NOCOOKIEAFTERAUTH = "error.login.nocookieafterauth";
}
