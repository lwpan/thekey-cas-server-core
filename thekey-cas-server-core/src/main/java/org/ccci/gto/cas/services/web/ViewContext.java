package org.ccci.gto.cas.services.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

public final class ViewContext {
    private final HttpServletRequest request;
    private final List<ArgumentExtractor> extractors;
    private final RequestContext requestContext;

    private boolean loadedService = false;
    private Service service;

    public ViewContext(final HttpServletRequest request,
	    final List<ArgumentExtractor> extractors) {
	this.request = request;
	this.extractors = extractors;
	this.requestContext = null;
    }

    public ViewContext(final RequestContext requestContext,
	    final List<ArgumentExtractor> extractors) {
	this.requestContext = requestContext;
	this.extractors = extractors;
	// TODO: there should probably be some better error handling for
	// fetching the native request object
	this.request = (HttpServletRequest) requestContext.getExternalContext()
		.getNativeRequest();
    }

    public final HttpServletRequest getRequest() {
	return request;
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

    public final void setAttribute(final String name, final Object value) {
	if (requestContext != null) {
	    requestContext.getRequestScope().put(name, value);
	} else {
	    request.setAttribute(name, value);
	}
    }
}
