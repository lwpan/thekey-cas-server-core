package org.ccci.gto.cas.services.web;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.webflow.execution.RequestContext;

public final class ViewContext {
    private final HttpServletRequest request;
    private final ModelAndView modelAndView;
    private final RequestContext requestContext;
    private final List<ArgumentExtractor> extractors;
    private final ServicesManager servicesManager;

    private boolean loadedService = false;
    private Service service;
    private boolean loadedRegisteredService = false;
    private RegisteredService rService;

    public ViewContext(final RequestContext requestContext,
	    final List<ArgumentExtractor> extractors,
	    final ServicesManager servicesManager) {
	// TODO: there should probably be some better error handling for
	// fetching the native request object
	this.request = (HttpServletRequest) requestContext.getExternalContext()
		.getNativeRequest();
	this.modelAndView = null;
	this.requestContext = requestContext;
	this.extractors = extractors;
	this.servicesManager = servicesManager;
    }

    public ViewContext(final HttpServletRequest request,
	    final ModelAndView modelAndView,
	    final List<ArgumentExtractor> extractors,
	    final ServicesManager servicesManager) {
	this.request = request;
	this.modelAndView = modelAndView;
	this.requestContext = null;
	this.extractors = extractors;
	this.servicesManager = servicesManager;
    }

    public final HttpServletRequest getRequest() {
	return request;
    }

    public final URI getRequestUri() {
        final HttpServletRequest request = this.getRequest();
        final UriBuilder uri = UriBuilder.fromUri(request.getRequestURL().toString());
        uri.replaceQuery(request.getQueryString());
        return uri.build();
    }

    public final Service getService() {
	if (!loadedService) {
	    // try loading the service from the flow scope first
	    if (requestContext != null) {
		try {
		    service = (Service) requestContext.getFlowScope().get(
			    "service");
		} catch (final Exception e) {
		}
	    }

	    // extract the service from the request
	    if (service == null) {
		service = WebUtils.getService(extractors, request);
	    }

	    // mark the service as loaded
	    loadedService = true;
	}

	return service;
    }

    public final RegisteredService getRegisteredService() {
	if (!loadedRegisteredService) {
	    if (servicesManager != null) {
		rService = servicesManager.findServiceBy(this.getService());
	    }

	    loadedRegisteredService = true;
	}
	return rService;
    }

    public final void setAttribute(final String name, final Object value) {
	if (requestContext != null) {
	    requestContext.getRequestScope().put(name, value);
	} else if (modelAndView != null) {
            if (modelAndView.getView() instanceof RedirectView) {
                // #IDM-210 don't put attributes into RedirectView's because
                // they are put into the URL
            } else if ("jsonView".equals(modelAndView.getViewName())) {
                // #IDM-234 CAS 3.5.1 adds a jsonView for svc mgmt svc
                // drag-n-drop. The version of spring-json & sojo used contain a
                // bug when processing an iterable object type. Plus all values
                // stored in the model are serialized to returned JSON, so we
                // probably shouldn't include View attributes.
            } else {
                modelAndView.getModel().put(name, value);
            }
	} else {
	    request.setAttribute(name, value);
	}
    }

    public final Object getAttribute(final String name) {
        if (requestContext != null) {
            return requestContext.getRequestScope().get(name);
        } else if (modelAndView != null) {
            return modelAndView.getModel().get(name);
        } else {
            return request.getAttribute(name);
        }
    }
}
