package me.thekey.cas.css.cssparser.scrubber.filter;

import org.w3c.dom.css.CSSStyleSheet;

public abstract class AbstractCssFilter implements CssFilter {
    @Override
    public final void filter(final CSSStyleSheet css) {
        this.filterInternal(css);
    }

    protected void filterInternal(final CSSStyleSheet css) {
    }
}
