package org.ccci.gto.cas.css.filter;

import org.w3c.dom.css.CSSStyleSheet;

public interface CssFilter {
    /**
     * filter the specified stylesheet
     * 
     * @param css
     *            the stylesheet to filter
     * @param uri
     *            the url this stylesheet was loaded from, can be null if there
     *            is no url
     * @return the filtered stylesheet
     */
    public CSSStyleSheet filter(final CSSStyleSheet css);
}
