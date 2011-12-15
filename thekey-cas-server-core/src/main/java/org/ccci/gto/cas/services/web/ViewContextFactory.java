package org.ccci.gto.cas.services.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.jasig.cas.web.support.ArgumentExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.execution.RequestContext;

public final class ViewContextFactory {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    private List<ArgumentExtractor> argumentExtractors;

    /**
     * @param argumentExtractors
     *            the argumentExtractors to set
     */
    public void setArgumentExtractors(
	    final List<ArgumentExtractor> argumentExtractors) {
	this.argumentExtractors = argumentExtractors;
    }

    public ViewContext getViewContext(final RequestContext context) {
	return new ViewContext(context, argumentExtractors);
    }

    public ViewContext getViewContext(final HttpServletRequest request,
	    final ModelAndView modelAndView) {
	return new ViewContext(request, modelAndView, argumentExtractors);
    }
}
