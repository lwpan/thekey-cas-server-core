package me.thekey.cas.css.cssparser.scrubber.filter;

import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

public abstract class AbstractRuleCssFilter extends AbstractCssFilter {
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

            // process nested rules in @media rules
            if (rule instanceof CSSMediaRule) {
                this.filterMediaRule((CSSMediaRule) rule);
            }

            // remove this rule if it is not filtered successfully
            if (!this.filterRule(rule)) {
                mediaRule.deleteRule(i);
                i--;
            }
        }
    }

    private void filterStyleSheet(final CSSStyleSheet css) {
        // iterate over all the CSS rules
        final CSSRuleList rules = css.getCssRules();
        for (int i = 0; i < rules.getLength(); i++) {
            final CSSRule rule = rules.item(i);

            // process nested rules in @media rules
            if (rule instanceof CSSMediaRule) {
                this.filterMediaRule((CSSMediaRule) rule);
            }

            // remove this rule if it is not filtered successfully
            if (!this.filterRule(rule)) {
                css.deleteRule(i);
                i--;
            }
        }
    }

    /**
     * @param rule the rule to filter
     * @return a boolean indicating if the rule was filtered successfully, when a rule is unsuccessfully filter,
     * it is removed
     */
    protected boolean filterRule(final CSSRule rule) {
        return true;
    }
}
