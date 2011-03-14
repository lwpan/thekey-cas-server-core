package org.ccci.gcx.idm.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.core.util.AuthenticationRequestBuilder;
import org.springframework.web.util.HtmlUtils;

/**
 * provides various utility methods used by idmweb.
 * @author ken
 *
 */
public final class IdmUtil implements AuthenticationRequestBuilder {

	protected static final Log log = LogFactory.getLog(IdmUtil.class);

	
	/**
	 * returns a generic CasAuthenticationClientRequest
	 */
	public AuthenticationClientRequest buildAuthenticationClientRequest() {

		return new CasAuthenticationRequest();
		
	}
	
	
	
	
	/**
	 * Builds a CasAuthenticationRequest based on an httpservletrequest
	 * 
	 * @param req = httpservlet request
	 * @return
	 */
	public static CasAuthenticationRequest buildCasAuthenticationRequest(HttpServletRequest req)
	{
		CasAuthenticationRequest casreq = new CasAuthenticationRequest();
		
		if(log.isDebugEnabled()) log.debug("Building CAS authentication request from httpservletrequest");
		
		HashMap<String,org.apache.commons.httpclient.Cookie> 
			apacheCookies = new HashMap<String,org.apache.commons.httpclient.Cookie>();
		
		if(req.getCookies()!=null)  //this is unlikely but we were getting a NPE below on rare occasion.
		{
			for (int a = 0; a<req.getCookies().length; a++)
			{
				javax.servlet.http.Cookie servletcookie = req.getCookies()[a];
				if(log.isDebugEnabled()) log.debug("copying cookie: "+servletcookie.getName() + " - "+servletcookie.getValue());
				apacheCookies.put(servletcookie.getName(), transformServletCookie(servletcookie));
			}
		}
		casreq.setCookies(apacheCookies);
		
		//set service to either url parameter OR session value if urlparm doesn't exist
		casreq.setService( LoginFormController.determineService(req));
				
		if(log.isDebugEnabled()) log.debug("Set service to: "+casreq.getService());
		casreq.setTicket(req.getParameter(Constants.REQUESTPARAMETER_TICKET));
		if(log.isDebugEnabled()) log.debug("Set ticket to: "+casreq.getTicket());
		
		//set pgtUrl if present.
		casreq.setPgtUrl(req.getParameter(Constants.REQUESTPARAMETER_PGTURL));

		//set logoutcallback if present
		casreq.setLogoutCallback(req.getParameter(Constants.REQUESTPARAMETER_LOGOUTCALLBACK));
		
		//set proxy parms if present (we'll use ticket and service for pgt and targetService)
		if(StringUtils.isNotEmpty(req.getParameter(Constants.REQUESTPARAMETER_PGT)))
		{
			if(log.isDebugEnabled()) log.debug("Looks like we must have a proxy request.  setting proxy request parameters.");
			casreq.setTicket(req.getParameter(Constants.REQUESTPARAMETER_PGT));
			casreq.setService(req.getParameter(Constants.REQUESTPARAMETER_TARGETSERVICE));
		}
		
		return casreq;
		
	}
	
	//from googlecode
    public static org.apache.commons.httpclient.Cookie transformServletCookie(javax.servlet.http.Cookie servletCookie) {

        org.apache.commons.httpclient.Cookie newCookie = null;

        if (servletCookie != null) {
            newCookie = 
                    new org.apache.commons.httpclient.Cookie(HtmlUtils.htmlEscape(servletCookie.getDomain()), 
                    		HtmlUtils.htmlEscape(servletCookie.getName()), 
                    				HtmlUtils.htmlEscape(servletCookie.getValue()), 
                    						HtmlUtils.htmlEscape(servletCookie.getPath()) != 
                                                             null ? 
                                            HtmlUtils.htmlEscape(servletCookie.getPath()) : 
                                                             "/", 
                                                             servletCookie.getMaxAge(), 
                                                             false);
            //TODO :::  last field should be: servletCookie.getSecure()
        }
        return newCookie;
    }

    /**
     * Transforms Apache cookies into Servlet Cookies
     * 
     * @param apacheCookie apache cookie 
     * 
     * @return servlet cookie
     */
    public static javax.servlet.http.Cookie transformApacheCookie(org.apache.commons.httpclient.Cookie apacheCookie) {

        javax.servlet.http.Cookie newCookie = null;

        if (apacheCookie != null) {
            Date expire = apacheCookie.getExpiryDate();
            int maxAge = -1;

            if (expire == null) {
                maxAge = -1;
            } else {
                Date now = Calendar.getInstance().getTime();
                // Convert milli-second to second
                Long second = 
                    new Long((expire.getTime() - now.getTime()) / 1000);
                maxAge = second.intValue();
            }

            newCookie = 
                    new javax.servlet.http.Cookie(HtmlUtils.htmlEscape(apacheCookie.getName()), 
                    		HtmlUtils.htmlEscape(apacheCookie.getValue()));

            newCookie.setDomain(HtmlUtils.htmlEscape(apacheCookie.getDomain())!=null ? HtmlUtils.htmlEscape(apacheCookie.getDomain()):"");
            newCookie.setPath(HtmlUtils.htmlEscape(apacheCookie.getPath()));
            newCookie.setMaxAge(maxAge);
            newCookie.setSecure(false);    //TODO:::   (apacheCookie.getSecure());
            if(log.isDebugEnabled()) log.debug("NEW COOKIE ---");
            if(log.isDebugEnabled()) log.debug("Name:"+newCookie.getName());
            if(log.isDebugEnabled()) log.debug("Val:"+newCookie.getValue());
            if(log.isDebugEnabled()) log.debug("Domain:"+newCookie.getDomain());
            if(log.isDebugEnabled()) log.debug("Path:"+newCookie.getPath());
            if(log.isDebugEnabled()) log.debug("MaxAge:"+newCookie.getMaxAge());
    

        
        }
        return newCookie;
    }



    /**
     * Returns the email address in the message field. or returns null.
     * @param message
     * @return email address, or null if not found
     */
	public static String extractEmail(String message) {
		if(log.isDebugEnabled()) log.debug("Looking for an email in: "+message);
		Matcher m = Pattern.compile("<cas:user>(.*)</cas:user>").matcher(message);
	        
	    if(m.find())
	    {
	    	if(log.isDebugEnabled()) log.debug("Found an email address: "+m.group(1));
	        return m.group(1);
	    }
	    else
	    {
	    	if(log.isDebugEnabled()) log.debug("Didn't find an email in that mess.");
	    	return null;
	    }
		
	}

    /**
     * adds a domain visited to a particular user's additional domain list.
     * 
     * @param user
     * @param request
     * @param userService
     * @param source
     *            the identifier for the source of this addition
     */
    public static void addDomainVisited(GcxUser user,
	    CasAuthenticationRequest request, GcxUserService userService,
	    String source) {
	String service = request.getService();
	if (StringUtils.isBlank(user.getDomainsVisitedString())
		|| !user.getDomainsVisited().contains(service)) {
	    // extract host from the service if possible
	    String host = null;
	    try {
		URL u = new URL(service);
		host = u.getHost();
	    } catch (MalformedURLException e) {
		log.error("Couldn't parse this service as an url: " + service);
	    }

	    // Store the host that was visited
	    if (StringUtils.isNotBlank(host)) {
		if (log.isDebugEnabled()) {
		    log.debug("Adding domain to list: " + host);
		}

		user.addDomainsVisited(host);
		userService.updateUser(user, false, source, user.getEmail());
	    } else {
		log.warn("service wasn't wellformed: " + service);
	    }
	}
    }
}
