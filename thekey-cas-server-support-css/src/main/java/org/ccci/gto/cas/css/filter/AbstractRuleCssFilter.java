package org.ccci.gto.cas.css.filter;

import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

public abstract class AbstractRuleCssFilter extends AbstractCssFilter {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.css.filter.AbstractCssFilter#filterInternal(org.w3c.
     * dom.css.CSSStyleSheet, java.net.URI)
     */
    @Override
    protected final CSSStyleSheet filterInternal(final CSSStyleSheet css) {
	// iterate over all the CSS rules
	final CSSRuleList rules = css.getCssRules();
	for (int i = 0; i < rules.getLength(); i++) {
	    final CSSRule rule = rules.item(i);

	    // is this a blocked rule type?
	    if (this.isBlocked(rule)) {
		css.deleteRule(i);
		i--;
		continue;
	    }
	}

	return css;
    }

    protected boolean isBlocked(final CSSRule rule) {
	// default to the rule being allowed
	return false;
    }
}
