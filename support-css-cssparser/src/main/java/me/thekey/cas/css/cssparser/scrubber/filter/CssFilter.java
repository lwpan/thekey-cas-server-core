package me.thekey.cas.css.cssparser.scrubber.filter;

import org.w3c.dom.css.CSSStyleSheet;

public interface CssFilter {
    /**
     * filter the specified stylesheet
     *
     * @param css the stylesheet to filter
     * @return the filtered stylesheet
     */
    CSSStyleSheet filter(CSSStyleSheet css);
}
