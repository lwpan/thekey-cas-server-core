package org.ccci.gto.cas.css.filter;

import org.w3c.dom.css.CSSMediaRule;
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
	this.filterStyleSheet(css);
	return css;
    }

    private void filterMediaRule(final CSSMediaRule mediaRule) {
	// iterate over all the CSS rules
	final CSSRuleList rules = mediaRule.getCssRules();
	for (int i = 0; i < rules.getLength(); i++) {
	    final CSSRule rule = rules.item(i);

	    // is this a blocked rule type?
	    if (this.isBlocked(rule)) {
		mediaRule.deleteRule(i);
		i--;
		continue;
	    }

	    // process nested rules in @media rules
	    if (rule instanceof CSSMediaRule) {
		this.filterMediaRule((CSSMediaRule) rule);
	    }

	    // apply any filters to the current CSSRule
	    this.filterRule(rule);
	}
    }

    private void filterStyleSheet(final CSSStyleSheet css) {
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

	    // process nested rules in @media rules
	    if (rule instanceof CSSMediaRule) {
		this.filterMediaRule((CSSMediaRule) rule);
	    }

	    // apply any filters to the current CSSRule
	    this.filterRule(rule);
	}
    }

    protected void filterRule(final CSSRule rule) {
	// do nothing by default
    }

    protected boolean isBlocked(final CSSRule rule) {
	// default to the rule being allowed
	return false;
    }
}
