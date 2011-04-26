package org.ccci.gcx.idm.web;

import java.net.MalformedURLException;
import java.net.URL;
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
     * @param req
     *            = httpservlet request
     * @return
     */
    public static CasAuthenticationRequest buildCasAuthenticationRequest(
	    HttpServletRequest req) {
	log.debug("Building CAS authentication request from HttpServletRequest");
	CasAuthenticationRequest casreq = new CasAuthenticationRequest();
		
	// copy cookies into CasAuthenticationRequest object
	final javax.servlet.http.Cookie[] cookies = req.getCookies();
	if (cookies != null) {
	    for (javax.servlet.http.Cookie cookie : cookies) {
		if (log.isDebugEnabled()) {
		    log.debug("copying cookie: " + cookie.getName() + " - "
			    + cookie.getValue());
		}
		casreq.setCookie(cookie.getName(), cookie.getValue());
	    }
	}

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
     * @param url
     *            the url being visited
     * @param userService
     * @param source
     *            the identifier for the source of this addition
     */
    public static void addDomainVisited(GcxUser user, final String url,
	    GcxUserService userService, String source) {
	// extract the host from the url if possible
	String host = null;
	try {
	    URL u = new URL(url);
	    host = u.getHost();
	} catch (MalformedURLException e) {
	    log.error("Couldn't parse this url: " + url);
	}

	if (StringUtils.isNotBlank(host)
		&& !user.getDomainsVisited().contains(host)) {
	    // Store the host that was visited
	    if (log.isDebugEnabled()) {
		log.debug("Adding domain to list: " + host);
	    }

	    user.addDomainsVisited(host);
	    userService.updateUser(user, false, source, user.getEmail());
	} else if (StringUtils.isBlank(host)) {
	    log.warn("url was malformed: " + url);
	}
    }
}
