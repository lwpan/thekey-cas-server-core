package org.ccci.gto.cas.services.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.jasig.cas.web.support.ArgumentExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public final class TheKeyViewInterceptor extends HandlerInterceptorAdapter {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    private List<ArgumentExtractor> argumentExtractors;

    @NotNull
    private List<ViewPopulator> populators;

    public void setArgumentExtractors(
	    final List<ArgumentExtractor> argumentExtractors) {
	this.argumentExtractors = argumentExtractors;
    }

    /**
     * @param populators
     *            the ViewPopulators to use for this ThemeResolver
     */
    public void setPopulators(final List<ViewPopulator> populators) {
	this.populators = populators;
    }

    @Override
    public void postHandle(final HttpServletRequest request,
	    final HttpServletResponse response, final Object handler,
	    final ModelAndView modelAndView) {
	// create a view context for this request
	final ViewContext context = new ViewContext(request, modelAndView,
		argumentExtractors);

	// process all the ViewPopulators
	if (populators != null) {
	    for (final ViewPopulator populator : populators) {
		populator.populate(context);
	    }
	}
    }
}
