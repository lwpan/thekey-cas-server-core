package org.ccci.gto.cas.services.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public final class TheKeyViewInterceptor extends HandlerInterceptorAdapter {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    private ViewContextFactory viewFactory;

    @NotNull
    private List<ViewPopulator> populators;

    /**
     * @param populators
     *            the ViewPopulators to use for this ThemeResolver
     */
    public void setPopulators(final List<ViewPopulator> populators) {
	this.populators = populators;
    }

    /**
     * @param factory
     *            the ViewContextFactory to use
     */
    public void setViewContextFactory(final ViewContextFactory factory) {
	this.viewFactory = factory;
    }

    @Override
    public void postHandle(final HttpServletRequest request,
	    final HttpServletResponse response, final Object handler,
	    final ModelAndView modelAndView) {
	// create a view context for this request
	final ViewContext context = viewFactory.getViewContext(request,
		modelAndView);

	// process all the ViewPopulators
	if (populators != null) {
	    for (final ViewPopulator populator : populators) {
		populator.populate(context);
	    }
	}
    }
}
