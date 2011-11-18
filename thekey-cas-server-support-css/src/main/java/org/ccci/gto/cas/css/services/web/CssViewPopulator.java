package org.ccci.gto.cas.css.services.web;

import static org.ccci.gto.cas.css.Constants.PARAMETER_TEMPLATEURL;
import static org.ccci.gto.cas.css.Constants.SESSION_TEMPLATEURL;
import static org.ccci.gto.cas.css.Constants.URL_DEFAULT_TEMPLATE;
import static org.ccci.gto.cas.css.Constants.VIEW_ATTR_TEMPLATEURL;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.ccci.gto.cas.services.web.AbstractViewPopulator;
import org.ccci.gto.cas.services.web.ViewContext;
import org.jasig.cas.authentication.principal.Service;

public final class CssViewPopulator extends AbstractViewPopulator {
    @Override
    protected void populateInternal(final ViewContext context) {
	final HttpServletRequest request = context.getRequest();
	final Service service = context.getService();

	// set the template to use for this request
	URL templateUrl = null;

	// check for a specified template first
	final String template = request.getParameter(PARAMETER_TEMPLATEURL);
	if (templateUrl == null && StringUtils.isNotBlank(template)) {
	    try {
		templateUrl = new URL(template);
	    } catch (final MalformedURLException e) {
		log.debug("Malformed template parameter", e);
		templateUrl = null;
	    }
	}

	// TODO: IDM-134 check for a template configured in the services manager

	// set a default template url based on the specified service
	if (templateUrl == null && service != null) {
	    try {
		final URL serviceUrl = new URL(service.getId());
		templateUrl = new URL(serviceUrl, URL_DEFAULT_TEMPLATE);
	    } catch (final MalformedURLException e) {
		log.debug("Error generating default template url from service",
			e);
		templateUrl = null;
	    }
	}

	// load the template url from the session
	if (templateUrl == null) {
	    request.getSession().getAttribute(SESSION_TEMPLATEURL);
	}
	// otherwise store the found templateUrl in the session
	else {
	    request.getSession().setAttribute(SESSION_TEMPLATEURL, templateUrl);
	}

	// set the template url in the request context
	request.setAttribute(VIEW_ATTR_TEMPLATEURL, templateUrl);
    }
}
