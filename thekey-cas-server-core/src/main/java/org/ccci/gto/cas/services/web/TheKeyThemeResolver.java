package org.ccci.gto.cas.services.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.web.support.ArgumentExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.theme.AbstractThemeResolver;

public class TheKeyThemeResolver extends AbstractThemeResolver {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private List<ArgumentExtractor> argumentExtractors;

    private List<ViewPopulator> populators;

    public void setThemeName(final HttpServletRequest request,
	    final HttpServletResponse response, final String themeName) {
	// do nothing
    }

    public String resolveThemeName(final HttpServletRequest request) {
	// create a theme context for this request
	final ViewContext context = new ViewContext(request, argumentExtractors);

	// process all the ViewPopulators
	if (populators != null) {
	    for (final ViewPopulator populator : populators) {
		populator.populate(context);
	    }
	}

	// return the default theme name
	return getDefaultThemeName();
    }

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
}
