package me.thekey.cas.css.cssparser.scrubber.filter;

import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;

import java.util.Collection;

public class AbstractStyleCssFilter extends AbstractRuleCssFilter {
    @Override
    protected final boolean filterRule(final CSSRule rule) {
        if (rule instanceof CSSStyleRule) {
            this.filterStyles(((CSSStyleRule) rule).getStyle());
        } else if (rule instanceof CSSFontFaceRule) {
            this.filterStyles(((CSSFontFaceRule) rule).getStyle());
        } else if (rule instanceof CSSPageRule) {
            this.filterStyles(((CSSPageRule) rule).getStyle());
        }

        // run any additional filtering needed for this rule
        return this.filterRuleInternal(rule);
    }

    protected boolean filterRuleInternal(final CSSRule rule) {
        return true;
    }

    protected void filterStyles(final CSSStyleDeclaration styles) {
        // do nothing by default
    }

    protected final void removeProperties(final CSSStyleDeclaration styles, final Collection<? extends String>
            properties) {
        // remove all properties that need to be removed
        for (final String property : properties) {
            // removeProperty only removes the first property with the specified name,
            // loop until all properties using the specified name are removed
            while (true) {
                final int size = styles.getLength();
                styles.removeProperty(property);
                if (styles.getLength() == size) {
                    break;
                }
            }
        }
    }
}
