package org.ccci.gto.cas.web.servlet;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

/**
 * @author Daniel Frett
 */
public class TheKeyViewInterceptor extends HandlerInterceptorAdapter {
    /** Logger that is available to subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    private static final String DEFAULT_TEMPLATEURL = "/sso/template.css";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle
     * (javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(final HttpServletRequest request,
	    final HttpServletResponse response, final Object handler)
	    throws Exception {
	// extract parameters from request
	final String service = request.getParameter("service");
	final String template = request.getParameter("template");

	// handle any custom templates
	if (StringUtils.isNotBlank(service) || StringUtils.isNotBlank(template)) {
	    log.debug("Checking for a new valid template for the current request");

	    // generate the template URL
	    URL templateUrl = null;
	    try {
		try {
		    // try using the specified template directly
		    templateUrl = new URL(template);
		} catch (MalformedURLException e) {
		    // try generating a default URL based on the specified service
		    final URL serviceUrl = new URL(service);
		    templateUrl = new URL(serviceUrl, DEFAULT_TEMPLATEURL);
		}
	    } catch (MalformedURLException e) {
		templateUrl = null;
	    }

	    // set the template if one exists
	    WebUtils.setSessionAttribute(request, "template",
		    templateUrl.toString());
	}

	// Always continue processing
	return true;
    }

}
