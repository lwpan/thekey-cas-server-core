package me.thekey.cas.css.cssparser.scrubber.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import me.thekey.cas.css.cssparser.AbstractParserTest;
import me.thekey.cas.css.scrubber.filter.ReversibleFilter.Type;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.IOException;

public class PropertyValueCssFilterTest extends AbstractParserTest {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyValueCssFilterTest.class);

    private PropertyValueCssFilter getFilter() {
        return new PropertyValueCssFilter();
    }

    @Test
    public void testFilter() throws IOException {
        // test basic blacklist filter functionality
        {
            final String baseUri = "https://example.com/base.css";
            final String rawCss = ".rule1 {background: expression(1+2);}.rule2 {background: url(\"express.png\");}";
            final CSSStyleSheet css = this.parseCss(this.getStringInputSource(rawCss), baseUri);
            final PropertyValueCssFilter filter = this.getFilter();
            filter.setFilterType(Type.BLACKLIST);
            filter.addValue("expression");
            filter.filter(css);
            LOG.debug(css.toString());
            final CSSRuleList rules = css.getCssRules();
            assertEquals(2, rules.getLength());
            // rule1
            {
                final CSSRule rule = rules.item(0);
                assertEquals(CSSRule.STYLE_RULE, rule.getType());
                assertTrue(rule instanceof CSSStyleRule);
                assertEquals(0, ((CSSStyleRule) rule).getStyle().getLength());
            }
            // rule2
            {
                final CSSRule rule = rules.item(1);
                assertEquals(CSSRule.STYLE_RULE, rule.getType());
                assertTrue(rule instanceof CSSStyleRule);
                assertEquals(1, ((CSSStyleRule) rule).getStyle().getLength());
            }
        }
    }
}
