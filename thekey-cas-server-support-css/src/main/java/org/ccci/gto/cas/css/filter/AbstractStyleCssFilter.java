package org.ccci.gto.cas.css.filter;

import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;

public class AbstractStyleCssFilter extends AbstractRuleCssFilter {
    @Override
    protected final boolean isBlocked(final CSSRule rule) {
	return false;
    }

    @Override
    protected final void filterRule(final CSSRule rule) {
	if (rule instanceof CSSStyleRule) {
	    this.filterStyles(((CSSStyleRule) rule).getStyle());
	} else if (rule instanceof CSSFontFaceRule) {
	    this.filterStyles(((CSSFontFaceRule) rule).getStyle());
	} else if (rule instanceof CSSPageRule) {
	    this.filterStyles(((CSSPageRule) rule).getStyle());
	}
    }

    protected void filterStyles(final CSSStyleDeclaration styles) {
	// do nothing by default
    }
}
