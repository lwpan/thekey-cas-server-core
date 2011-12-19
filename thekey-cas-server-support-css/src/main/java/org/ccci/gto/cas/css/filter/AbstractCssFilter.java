package org.ccci.gto.cas.css.filter;

import java.net.URI;

import org.w3c.dom.css.CSSStyleSheet;

public abstract class AbstractCssFilter implements CssFilter {
    @Override
    public final CSSStyleSheet filter(final CSSStyleSheet css, final URI uri) {
	return filterInternal(css, uri);
    }

    protected CSSStyleSheet filterInternal(final CSSStyleSheet css,
	    final URI uri) {
	// default to returning the unmodified input stylesheet
	return css;
    }
}
