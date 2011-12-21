package org.ccci.gto.cas.css.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.css.CSSStyleSheet;

public abstract class AbstractCssFilter implements CssFilter {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public final CSSStyleSheet filter(final CSSStyleSheet css) {
	return filterInternal(css);
    }

    protected CSSStyleSheet filterInternal(final CSSStyleSheet css) {
	// default to returning the unmodified input stylesheet
	return css;
    }
}
