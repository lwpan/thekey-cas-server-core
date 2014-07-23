package me.thekey.cas.css.cssparser.scrubber.filter;

import org.w3c.dom.css.CSSStyleSheet;

public abstract class AbstractCssFilter implements CssFilter {
    @Override
    public final CSSStyleSheet filter(final CSSStyleSheet css) {
        return filterInternal(css);
    }

    protected CSSStyleSheet filterInternal(final CSSStyleSheet css) {
        // default to returning the unmodified input stylesheet
        return css;
    }
}
