package org.ccci.gto.cas.services.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.WebUtils;

public final class ViewContext {
    private final HttpServletRequest request;

    private final List<ArgumentExtractor> extractors;

    private boolean loadedService = false;
    private Service service;

    ViewContext(final HttpServletRequest request,
	    final List<ArgumentExtractor> extractors) {
	this.request = request;
	this.extractors = extractors;
    }

    public final HttpServletRequest getRequest() {
	return request;
    }

    public final Service getService() {
	if (!loadedService) {
	    service = WebUtils.getService(extractors, request);
	    loadedService = true;
	}

	return service;
    }
}
