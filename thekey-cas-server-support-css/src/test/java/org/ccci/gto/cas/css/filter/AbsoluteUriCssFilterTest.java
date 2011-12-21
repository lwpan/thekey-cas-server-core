package org.ccci.gto.cas.css.filter;

import java.io.IOException;

import org.ccci.gto.cas.css.AbstractParserTest;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

public class AbsoluteUriCssFilterTest extends AbstractParserTest {
    private AbsoluteUriCssFilter getFilter() {
	return new AbsoluteUriCssFilter();
    }

    public void testFilter() throws IOException {
	final String rawCss = "@import \"import.css\";"
		+ "body {background: url(\"../body.png\");}"
		+ "div {background: transparent url(images/div.png) no-repeat right top;}"
		+ "span {background: url(https://absolute.example.com/span.png);}";

	for (final String[] uris : new String[][] {
		{ null, "import.css", "../body.png", "images/div.png" },
		{ "https://example.com/base.css",
			"https://example.com/import.css",
			"https://example.com/body.png",
			"https://example.com/images/div.png" },
		{ "https://www2.example.com/path/base.css",
			"https://www2.example.com/path/import.css",
			"https://www2.example.com/body.png",
			"https://www2.example.com/path/images/div.png" }
	}) {
	    // parse with no base uri
	    {
		final CSSStyleSheet css = this.parseCss(
			this.getStringInputSource(rawCss), uris[0]);
		this.getFilter().filter(css);
		log.debug(css.toString());
		final CSSRuleList rules = css.getCssRules();
		// import rule
		{
		    final CSSRule rule = rules.item(0);
		    assertEquals(CSSRule.IMPORT_RULE, rule.getType());
		    assertTrue(rule instanceof CSSImportRule);
		    assertEquals(uris[1], ((CSSImportRule) rule).getHref());
		}
		// body style rule
		{
		    final CSSRule rule = rules.item(1);
		    assertEquals(CSSRule.STYLE_RULE, rule.getType());
		    assertTrue(rule instanceof CSSStyleRule);
		    final CSSValue value = ((CSSStyleRule) rule).getStyle()
			    .getPropertyCSSValue("background");
		    assertEquals(CSSValue.CSS_PRIMITIVE_VALUE,
			    value.getCssValueType());
		    assertTrue(value instanceof CSSPrimitiveValue);
		    final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
		    assertEquals(CSSPrimitiveValue.CSS_URI,
			    pValue.getPrimitiveType());
		    assertEquals(uris[2], pValue.getStringValue());
		}
		// div style rule
		{
		    final CSSRule rule = rules.item(2);
		    assertEquals(CSSRule.STYLE_RULE, rule.getType());
		    assertTrue(rule instanceof CSSStyleRule);
		    final CSSValue value = ((CSSStyleRule) rule).getStyle()
			    .getPropertyCSSValue("background");
		    assertEquals(CSSValue.CSS_VALUE_LIST,
			    value.getCssValueType());
		    assertTrue(value instanceof CSSValueList);
		    final CSSValueList lValue = (CSSValueList) value;
		    assertEquals(5, lValue.getLength());
		    final CSSValue value2 = lValue.item(1);
		    assertEquals(CSSValue.CSS_PRIMITIVE_VALUE,
			    value2.getCssValueType());
		    assertTrue(value2 instanceof CSSPrimitiveValue);
		    final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value2;
		    assertEquals(CSSPrimitiveValue.CSS_URI,
			    pValue.getPrimitiveType());
		    assertEquals(uris[3], pValue.getStringValue());
		}
		// span style rule
		{
		    final CSSRule rule = rules.item(3);
		    assertEquals(CSSRule.STYLE_RULE, rule.getType());
		    assertTrue(rule instanceof CSSStyleRule);
		    final CSSValue value = ((CSSStyleRule) rule).getStyle()
			    .getPropertyCSSValue("background");
		    assertEquals(CSSValue.CSS_PRIMITIVE_VALUE,
			    value.getCssValueType());
		    assertTrue(value instanceof CSSPrimitiveValue);
		    final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
		    assertEquals(CSSPrimitiveValue.CSS_URI,
			    pValue.getPrimitiveType());
		    assertEquals("https://absolute.example.com/span.png",
			    pValue.getStringValue());
		}
	    }
	}
    }
}
