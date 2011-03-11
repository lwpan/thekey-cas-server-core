package org.ccci.gcx.idm.web;


/*
import javax.naming.directory.DirContext;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.support.LdapUtils;
*/

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.validation.BindException;
import java.net.URL;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationResponse;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.core.service.AuthenticationService;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.core.util.URLUtil;
import org.ccci.gcx.idm.web.brand.BrandLocator;
import org.ccci.gcx.idm.web.flow.CookieRetrievingCookieGenerator;


/**
 * LoginHomeController - Provides the homepage for our idm login service.
 * It references a BrandExtractor which looks up the location of our template branding.
 * 
 * @author ken
 *
 */

public class LoginFormController extends SimpleFormController 
{
			
	private BrandLocator extractor;
	private GcxUserService gcxuserservice;
	private AuthenticationService authservice;
	private CookieRetrievingCookieGenerator cg;
	private LanguageListBean langbean;
	private RedListBean redlistbean;
	private static String defaultservice;
	private boolean allowGETcredentials;
	
	private int hits = 0;
	private int successfulLogins = 0;
	private int failedLogins = 0;
	//private ContextSource contextSource;
	
	protected static final Log log = LogFactory.getLog(LoginFormController.class);
	
	/**
	 * setup command object for this formcontroller to be our loginuser.
	  NOTE: this simpleloginuser is used instead of gcxuser in order to have a
	        light model/command object that we can do our spring forms with.  When necessary
	        we can use a gcxuser directly (for search, etc), 
	        but for the most part we'll favor this lighter object.
	
	 */
	public LoginFormController()
	{
		setCommandClass(SimpleLoginUser.class);
		setCommandName("loginUser");
	}

	/**
	 * returns a prepared (if necessary) command object.
	 * 
	 * TEMPLATE FEATURE
	 *  We put the "location" attribute into the session. This location attribute
	 *    is referenced in the jsp pages in a script tag to provide a custom look and feel for the login pages.
	 *    1) If a "template" attribute is provided, that attribute is passed along as the location. No checking.
	 *    2) If a "service" attribute is provided, we use a BrandLocator class to determine the location based on the service.
	 *    3) If neither of the above are found, the script tag will be empty which will simply display our default look.
	 *    
	 * Note: If there is a session parameter LOGINSESSIONATTRIBUTE set then we'll use that as our "current" username.
	 *   This makes it possible to the login page to be pre-populated with the username when coming from other 
	 *   controllers or the self-service webflow.
	 */
	protected Object formBackingObject (HttpServletRequest request) throws Exception {
		SimpleLoginUser loginForm = (SimpleLoginUser) super.formBackingObject(request);
		if(log.isDebugEnabled()) log.debug("Building FBO for login.");
		
		//check for "signup" username stored in the session to auto-add to the username field on the form
		String signupUsername = (String) WebUtils.getSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOGIN);
		if(signupUsername != null)
		{
			if(log.isDebugEnabled()) log.debug("Found a signupUsername: "+signupUsername+". adding it to the model.");
			loginForm.setUsername(signupUsername);
			request.getSession().removeAttribute(Constants.SESSIONATTRIBUTE_LOGIN); // remove so we don't accidentally force on subsequent requests.
		}
		
		//if there is an incoming service or template OR there is no saved service, determine our service+location
		if(
			(request.getParameter(Constants.REQUESTPARAMETER_SERVICE) != null || 
		     request.getParameter(Constants.REQUESTPARAMETER_TEMPLATE) != null  ) ||
			 WebUtils.getSessionAttribute(request, Constants.SESSIONATTRIBUTE_SERVICE) == null)
		   {				
				//re-determine the service and location.
				if(log.isDebugEnabled()) log.debug("service or template present... reacquire.");
				
				String service = determineService(request);
				String location = determineLocation(request, service);
				WebUtils.setSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOCATION, location);
				WebUtils.setSessionAttribute(request, Constants.SESSIONATTRIBUTE_SERVICE, service);
		   }

		WebUtils.setSessionAttribute(request, Constants.SESSION_CURRENTLOCALE, RequestContextUtils.getLocale(request).getLanguage());
		WebUtils.setSessionAttribute(request, Constants.SESSION_LANGUAGE_LIST, langbean.getLanguageList());
		
		hits++;
		
		return loginForm;
	}
	
	/**
	 * Here we make it possible to override the default "isFormSubmission" implementation to indicate 
	 * that ALL requests made to us are submissions from our form.  Result is based upon the 
	 * DI value allowGETcredentials. If true, then we allow username/password to be sent via GET.
	 * 
	 * 
	 * Why? Troy Wolbrink (possibly others?) want to be able to send http GET (not just POST) 
	 *      requests with username/password. 
	 * 
	 * SECURITY NOTE: Sending credentials in a GET request means they
	 *      won't be encrypted as they're simply part of the url. This is probably very bad in that
	 *      anyone listening between the requesting server and us will be able to observe
	 *      the credentials in transit.
	 */
	@Override
	protected boolean isFormSubmission(HttpServletRequest request)
	{
		//if we have a submit button pushed then we're TRUE for sure 
		if("POST".equals(request.getMethod()))
			return true;
		
		//now return results of isallowgetcredentials configuration option.
		if (isAllowGETcredentials() 
			&& StringUtils.isNotEmpty(request.getParameter(Constants.REQUESTPARAMETER_USERNAME))
			&& StringUtils.isNotEmpty(request.getParameter(Constants.REQUESTPARAMETER_PASSWORD)))
			return true;
		
		return false;
			
	}
	
	/**
	 * Don't bother validating if it is a GET request (see note on isFormSubmission override above).
	 * This keeps us from getting validation error messages when simply displaying the form with a GET.
	 */
	@Override
	protected boolean suppressValidation(HttpServletRequest request, Object command)
	{
		//suppress validation on GET requests
		if("GET".equals(request.getMethod()))
			return true;
		else
			return false;
	}
	
	/**
	 * Handles the login procedure (when they click submit)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors)
     throws Exception
	{
		//our form values bound to our simple user model object
		SimpleLoginUser form = (SimpleLoginUser) command;
		        
		GcxUser user=null;

		//First, we need to fetch our GcxUser and verify they exist, are allowed to login, etc.
		user = gcxuserservice.findUserByEmail(form.getUsername()) ;
        
		if (user == null) //they aren't a valid edir user
		{
    		//see if they are transitional, activate them if they are and their password is valid
        	GcxUser tsUser = gcxuserservice.findTransitionalUserByEmail(form.getUsername());
        	if(tsUser!=null)
        	{
        		if(log.isDebugEnabled())log.debug("Transitional user located, so they haven't activated yet.");
        		
        		//does the entered password match the temporary password issued?
        		if(tsUser.getPassword().equals(form.getPassword()))
        		{
        			//activate them
        			log.info("Activating transitional user:"+form.getUsername());
        			gcxuserservice.activateTransitionalUser(tsUser, Constants.SOURCEIDENTIFIER_ACTIVATION, tsUser.getEmail());
        			//Now look up their new user object and force them to change their password.
        			user = gcxuserservice.findUserByEmail(form.getUsername());
            		log.info("Activation SUCCESS now FORCE PASSWORD CHANGE : "+form.getUsername());
            		//prepare form object for change password.
                	form.setAuthenticated(true);
            		form.setFirstName(user.getFirstName());
            		form.setLastName(user.getLastName());
            		WebUtils.setSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOGINUSER , form);
        			return new ModelAndView(Constants.VIEW_FORCECHANGEPASSWORD,Constants.MODEL_LOGINUSER,form);
        		}
        	}
        	
        	//report this as a credential failure (same as if it was a wrong password)
        	log.info("User authentication failed because USER NOT FOUND!" + form.getUsername());
        	errors.rejectValue(null, Constants.ERROR_AUTHENTICATIONFAILED);
        	failedLogins++;
        	return this.showForm(request,errors,Constants.VIEW_LOGIN);
		}	
		
		if(log.isDebugEnabled()) log.debug("Valid user found for "+form.getUsername()+ " now login via CAS");
		
		//OK so we have a valid user from LDAP. Check authentication via CAS.
   
		//build up our casrequest object        
        CasAuthenticationRequest casrequest = IdmUtil.buildCasAuthenticationRequest(request);
        casrequest.setPrincipal(form.getUsername());
        casrequest.setCredential(form.getPassword());
        
        //authentication request
        CasAuthenticationResponse casresponse = 
        	(CasAuthenticationResponse) authservice.handleLoginRequest(casrequest);
	
        if(casresponse.isAuthenticated())
        {
    		log.info("CAS authentication SUCCESSFUL for "+form.getUsername()+" now verify administrative settings.");
        	form.setAuthenticated(true);
    		form.setFirstName(user.getFirstName());
    		form.setLastName(user.getLastName());
    		WebUtils.setSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOGINUSER , form);
        }
        else
        {
        	log.info("Login FAILURE: Credentials invalid for "+form.getUsername());
        	errors.rejectValue(null, Constants.ERROR_AUTHENTICATIONFAILED);
        	failedLogins++;
        	return this.showForm(request,errors,Constants.VIEW_LOGIN);
        }
        
        //VERIFY administrative parameters
        if(user.isLocked())
        {
        	log.info("Login FAILURE: Account is locked on "+form.getUsername());
    		errors.rejectValue(null, Constants.ERROR_ACCOUNTLOCKED);
    		return this.showForm(request,errors,Constants.VIEW_LOGIN);
        }
		
    	if(user.isDeactivated()) 
    	{
    		log.info("Login FAILURE: Account is deactivated on"+form.getUsername());
    		errors.rejectValue(null, Constants.ERROR_ACCOUNTDEACTIVATED);
        	failedLogins++;
    		return this.showForm(request, errors, Constants.VIEW_LOGIN);
    	}
    	
    	if(user.isForcePasswordChange())
    	{
    		log.info("Login SUCCESS, however Account FORCE PASSWORD CHANGE is in effect on: "+form.getUsername());
    		return new ModelAndView(Constants.VIEW_FORCECHANGEPASSWORD,Constants.MODEL_LOGINUSER,form);
    	}

    	if(user.isLoginDisabled())
    	{
    		log.info("Login FAILURE: Account is disabled on"+form.getUsername());
    		errors.rejectValue(null, Constants.ERROR_LOGINDISABLED);
    		failedLogins++;
    		return this.showForm(request, errors, Constants.VIEW_LOGIN);
    	}
    	
    	//authentication was successful and user has no administrative holds.
    	// so we can set our cookie and clear the session and off we go.
    	addCookieToResponse(response,casresponse.getCASTGCValue());
    	
    	request.getSession().invalidate();
    	
    	//if user is in admin group, make a note in the clean session.
    	if(gcxuserservice.isUserInAdminGroup(user))
    	{
    		log.info(user.getUserid()+" is in the admin group.");
    		request.getSession().setAttribute(Constants.SESSIONATTRIBUTE_ADMIN, user.getUserid());
    	}
    	else
    	{
    		if(log.isDebugEnabled()) log.info(user.getUserid()+" is in the admin group.");

    	}
        
        IdmUtil.addDomainVisited(user, casrequest, gcxuserservice, Constants.SOURCEIDENTIFIER_LOGIN);
        
        if(log.isDebugEnabled()) log.debug("Your currently visited domains: "+user.getDomainsVisitedString());
	        
    	if(log.isDebugEnabled()) log.debug("redirecting to service url back from CAS: "+casresponse.getLocation());
    	if(redlistbean.isListedService(casrequest.getService()))
    	{
    		response.setHeader(Constants.RESPONSEHEADER_TICKET,URLUtil.extractParm(casresponse.getLocation(),Constants.REQUESTPARAMETER_TICKET));
    		response.setHeader(Constants.RESPONSEHEADER_SERVICE,casrequest.getService());
    	}
        response.sendRedirect(casresponse.getLocation());
        
        successfulLogins++;
        
        return null;
        
	}
	 
	/**
	 * Determines the service location from an httprequest. 
	 * FIRST: "service" parameter from request
	 * SECOND: if not provided, then we use a defaultserviceurl as configured in Constants.
	 * always returns uri with protocol included (http:// or https://)
	 * @param request
	 * @return
	 */
	public static String determineService(HttpServletRequest request )
	{
		if(log.isDebugEnabled())  log.debug("determining service");
		String service = request.getParameter(Constants.REQUESTPARAMETER_SERVICE);
		
		//if service is provided in the parameters, use it.
		if(StringUtils.isNotBlank(service))
		{
			//make sure it is a valid uri
			try
			{
		new URL(service);
			}catch(Exception e)
			{
				log.error("Couldn't make this service into an url:"+service);
				service="";
			}
		}
		
		//use session if there wasn't one in the request parameters
		else
		{
			if(log.isDebugEnabled())  log.debug("didn't find a service url parameter, so trying the session");
			if(request.getSession() != null)
				service = (String) request.getSession().getAttribute(Constants.REQUESTPARAMETER_SERVICE);
		}
					
		//use the default service if we didn't get one from the request or session
		if(StringUtils.isBlank(service))
		{
			if(log.isDebugEnabled())  log.debug("didn't find a service url parameter or session variable so setting to default");

			service = LoginFormController.getDefaultService();
			
		}
		
		//ensure we have a protocol.
		if(!service.startsWith("http://") && !service.startsWith("https://") )
		{
			service = Constants.DEFAULTSERVICEPROTOCOL +service;
		}
	
		
		if(log.isDebugEnabled()) log.debug("DETERMINED OUR SERVICE TO BE ===== "+service);
		
		return service;
	}
	
	
	private String determineLocation(HttpServletRequest a_req, String a_service)
	{
		//is there an incoming template (that trumps everything)
		String template = a_req.getParameter(Constants.REQUESTPARAMETER_TEMPLATE);
		
		//if it is not blank, use it.
		if(StringUtils.isNotBlank(template))
		{
			try
			{
				//make sure it is a valid url
		new URL(template);
				if(log.isDebugEnabled()) log.debug("Yes: I could make "+template+" into an url, so we'll use it.");
				
			}catch(Exception e)
			{
				log.error("Couldn't make this template into an url:"+template+" so keep looking.");
				template="";
			}
		}
		
		else //means template was not provided so lets see if 
			//  1) incoming service? if yes then use it to derive a location (incoming service trumps a stored location)
			//  2) if no incoming service, is there a cached location in our session
			//  3) otherwise return default location
		{
			if(log.isDebugEnabled()) log.debug("No TEMPLATE parameter supplied, checking for service");
			
			//is there an incoming service parameter? (not a cached one, but an incoming one)
			String serviceParm = (String) a_req.getSession().getAttribute(Constants.REQUESTPARAMETER_SERVICE);
			
			//is there an incoming service parameter?
			if(StringUtils.isBlank(serviceParm))
			{
				//no, so then use our cached location. (if exists)
				template = (String) a_req.getSession().getAttribute(Constants.SESSIONATTRIBUTE_LOCATION);
			}
			else
			{
				//yes, we had an incoming service, so derive location from the service (unless it is our default service then skip it)
				if(!StringUtils.equals(a_service,Constants.DEFAULTSERVICEURL))
					template = extractor.getBrandLocation(a_service);
			}
			
		}
		
		//make sure null or anything blank or whitespace always gets returned as ""
		if(StringUtils.isBlank(template))
			template = "";

		//wrap all targets with our css processor
		if(!template.startsWith(Constants.CSSSERVICEURL))
			template = Constants.CSSSERVICEURL+template; 
		
		if(log.isDebugEnabled()) log.debug("Determined our TEMPLATE to be = "+template);
		
		//additionally... check to see if a reloadcss parameter has been passed along...
		String st_reload = a_req.getParameter(Constants.REQUESTPARAMETER_CSSRELOAD);
		if(StringUtils.isNotBlank(st_reload))
		{
			if(log.isDebugEnabled()) log.debug("reload css detected... adding reloadcss parm to css request.");
			template = template+"&"+Constants.REQUESTPARAMETER_CSSRELOAD+"=true";
		}
		
		return template;
		
	}
	
	
	/**
	 * adds the CAS TGC cookie to the response  
	 * @param a_response
	 */
	private void addCookieToResponse(HttpServletResponse a_response, String a_cookieValue)
	{
    	if(log.isDebugEnabled()) log.debug("adding cookievalue to response: "+a_cookieValue);
		cg.addCookie(a_response, a_cookieValue);
	}
	
	private void removeCookieFromResponse(HttpServletResponse a_response)
	{
    	if(log.isDebugEnabled()) log.debug("removing cookie from response");
		cg.removeCookie(a_response);
	}
	
	// DI
	public void setBrandLocator(BrandLocator a_be)
	{
		extractor = a_be;
	}
	
	public void setGcxUserService(GcxUserService a_svc)
	{
		gcxuserservice = a_svc;
	}
	
	public void setAuthenticationService(AuthenticationService a_svc)
	{
		authservice = a_svc;
	}

	public void setCookieGenerator(CookieRetrievingCookieGenerator a_cg)
	{
		cg=a_cg;
	}
	public void setLanguageListBean(LanguageListBean a_bean)
	{
		langbean = a_bean;
	}
	
	public void setDefaultService(String a_svc)
	{
		defaultservice = a_svc;
	}
	
	public static String getDefaultService()
	{
		String service = defaultservice;
		if(StringUtils.isBlank(service))
			service = Constants.DEFAULTSERVICEURL;		
		return defaultservice;
	}
	
	/*
	public void setContextSource(ContextSource src)
	{
		contextSource = src;
	}
	*/
	
	/**
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * @return the successfulLogins
	 */
	public int getSuccessfulLogins() {
		return successfulLogins;
	}

	/**
	 * @return the failedLogins
	 */
	public int getFailedLogins() {
		return failedLogins;
	}

	public void setRedListBean(RedListBean a_redlistbean)
	{
		redlistbean = a_redlistbean;
	}

	public void setAllowGETcredentials(boolean a_allowGETcredentials) {
		allowGETcredentials = a_allowGETcredentials;
	}

	public boolean isAllowGETcredentials() {
		return allowGETcredentials;
	}
	
	
}
