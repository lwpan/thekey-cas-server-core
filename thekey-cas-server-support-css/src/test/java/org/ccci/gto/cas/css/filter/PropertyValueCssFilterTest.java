package org.ccci.gto.cas.css.filter;

import java.io.IOException;

import org.ccci.gto.cas.css.AbstractParserTest;
import org.ccci.gto.cas.css.filter.ReversibleFilter.Type;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

public class PropertyValueCssFilterTest extends AbstractParserTest {
    private PropertyValueCssFilter getFilter() {
	return new PropertyValueCssFilter();
    }

    public void testFilter() throws IOException {
	// test basic blacklist filter functionality
	{
	    final String baseUri = "https://example.com/base.css";
	    final String rawCss = ".rule1 {background: expression(1+2);}"
		    + ".rule2 {background: url(\"express.png\");}";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), baseUri);
	    final PropertyValueCssFilter filter = this.getFilter();
	    filter.setFilterType(Type.BLACKLIST);
	    filter.addValue("expression");
	    filter.filter(css);
	    log.debug(css.toString());
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
