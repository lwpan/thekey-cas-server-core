package me.thekey.cas.css.cssparser.scrubber.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import me.thekey.cas.css.cssparser.AbstractParserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

import java.io.IOException;

public class AbsoluteUriCssFilterTest extends AbstractParserTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbsoluteUriCssFilterTest.class);

    private AbsoluteUriCssFilter getFilter() {
        return new AbsoluteUriCssFilter();
    }

    @Test
    public void testFilter() throws IOException {
        final String rawCss = "@import \"import.css\";body {background: url(\"../body.png\");}div {background: " +
                "transparent url(images/div.png) no-repeat right top;}span {background: url(https://absolute.example" +
                ".com/span.png);}";

        for (final String[] uris : new String[][]{{null, "import.css", "../body.png", "images/div.png"},
                {"https://example.com/base.css", "https://example.com/import.css", "https://example.com/body.png",
                        "https://example.com/images/div.png"}, {"https://www2.example.com/path/base.css",
                "https://www2.example.com/path/import.css", "https://www2.example.com/body.png",
                "https://www2.example.com/path/images/div.png"}}) {
            // parse with no base uri
            {
                final CSSStyleSheet css = this.parseCss(this.getStringInputSource(rawCss), uris[0]);
                this.getFilter().filter(css);
                LOG.debug(css.toString());
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
                    final CSSValue value = ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("background");
                    assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
                    assertTrue(value instanceof CSSPrimitiveValue);
                    final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
                    assertEquals(CSSPrimitiveValue.CSS_URI, pValue.getPrimitiveType());
                    assertEquals(uris[2], pValue.getStringValue());
                }
                // div style rule
                {
                    final CSSRule rule = rules.item(2);
                    assertEquals(CSSRule.STYLE_RULE, rule.getType());
                    assertTrue(rule instanceof CSSStyleRule);
                    final CSSValue value = ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("background");
                    assertEquals(CSSValue.CSS_VALUE_LIST, value.getCssValueType());
                    assertTrue(value instanceof CSSValueList);
                    final CSSValueList lValue = (CSSValueList) value;
                    assertEquals(5, lValue.getLength());
                    final CSSValue value2 = lValue.item(1);
                    assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value2.getCssValueType());
                    assertTrue(value2 instanceof CSSPrimitiveValue);
                    final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value2;
                    assertEquals(CSSPrimitiveValue.CSS_URI, pValue.getPrimitiveType());
                    assertEquals(uris[3], pValue.getStringValue());
                }
                // span style rule
                {
                    final CSSRule rule = rules.item(3);
                    assertEquals(CSSRule.STYLE_RULE, rule.getType());
                    assertTrue(rule instanceof CSSStyleRule);
                    final CSSValue value = ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("background");
                    assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
                    assertTrue(value instanceof CSSPrimitiveValue);
                    final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
                    assertEquals(CSSPrimitiveValue.CSS_URI, pValue.getPrimitiveType());
                    assertEquals("https://absolute.example.com/span.png", pValue.getStringValue());
                }
            }
        }

        // test #IDM-192: @import url("\""); and @import url(); crash css filter
        {
            final String baseUri = "https://example.com/base.css";
            final String invalidCss = "@import \"import1.css\";@import url(\"\\\"\");@import \"import3.css\";@import " +
                    "url();";
            final CSSStyleSheet css = this.parseCss(this.getStringInputSource(invalidCss), baseUri);
            this.getFilter().filter(css);
            LOG.debug(css.toString());
            final CSSRuleList rules = css.getCssRules();
            assertEquals(3, rules.getLength());
            // import rule 1
            {
                final CSSRule rule = rules.item(0);
                assertEquals(CSSRule.IMPORT_RULE, rule.getType());
                assertTrue(rule instanceof CSSImportRule);
                assertEquals("https://example.com/import1.css", ((CSSImportRule) rule).getHref());
            }
            // import rule 3
            {
                final CSSRule rule = rules.item(1);
                assertEquals(CSSRule.IMPORT_RULE, rule.getType());
                assertTrue(rule instanceof CSSImportRule);
                assertEquals("https://example.com/import3.css", ((CSSImportRule) rule).getHref());
            }
            // empty import rule
            {
                final CSSRule rule = rules.item(2);
                assertEquals(CSSRule.IMPORT_RULE, rule.getType());
                assertTrue(rule instanceof CSSImportRule);
                assertEquals("https://example.com/", ((CSSImportRule) rule).getHref());
            }
        }

        // test #IDM-193: background: url("\""); and background: url(); crash css filter
        {
            final String baseUri = "https://example.com/base.css";
            final String invalidCss = ".rule1 {background: url(\"img1.png\");}.rule2 {background: url(\"\\\"\");}" +
                    ".rule3 {background: url();}.rule4 {background: url(\"img4.png\");}";
            final CSSStyleSheet css = this.parseCss(this.getStringInputSource(invalidCss), baseUri);
            this.getFilter().filter(css);
            LOG.debug(css.toString());
            final CSSRuleList rules = css.getCssRules();
            assertEquals(4, rules.getLength());
            // rule1
            {
                final CSSRule rule = rules.item(0);
                assertEquals(CSSRule.STYLE_RULE, rule.getType());
                assertTrue(rule instanceof CSSStyleRule);
                final CSSStyleDeclaration styles = ((CSSStyleRule) rule).getStyle();
                assertEquals(1, styles.getLength());
                final CSSValue value = styles.getPropertyCSSValue("background");
                assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
                assertTrue(value instanceof CSSPrimitiveValue);
                final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
                assertEquals(CSSPrimitiveValue.CSS_URI, pValue.getPrimitiveType());
                assertEquals("https://example.com/img1.png", pValue.getStringValue());
            }
            // rule2
            {
                final CSSRule rule = rules.item(1);
                assertEquals(CSSRule.STYLE_RULE, rule.getType());
                assertTrue(rule instanceof CSSStyleRule);
                final CSSStyleDeclaration styles = ((CSSStyleRule) rule).getStyle();
                assertEquals(0, styles.getLength());
            }
            // rule3
            {
                final CSSRule rule = rules.item(2);
                assertEquals(CSSRule.STYLE_RULE, rule.getType());
                assertTrue(rule instanceof CSSStyleRule);
                final CSSStyleDeclaration styles = ((CSSStyleRule) rule).getStyle();
                assertEquals(1, styles.getLength());
                final CSSValue value = styles.getPropertyCSSValue("background");
                assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
                assertTrue(value instanceof CSSPrimitiveValue);
                final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
                assertEquals(CSSPrimitiveValue.CSS_URI, pValue.getPrimitiveType());
                assertEquals("https://example.com/", pValue.getStringValue());
            }
            // rule4
            {
                final CSSRule rule = rules.item(3);
                assertEquals(CSSRule.STYLE_RULE, rule.getType());
                assertTrue(rule instanceof CSSStyleRule);
                final CSSStyleDeclaration styles = ((CSSStyleRule) rule).getStyle();
                assertEquals(1, styles.getLength());
                final CSSValue value = styles.getPropertyCSSValue("background");
                assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
                assertTrue(value instanceof CSSPrimitiveValue);
                final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
                assertEquals(CSSPrimitiveValue.CSS_URI, pValue.getPrimitiveType());
                assertEquals("https://example.com/img4.png", pValue.getStringValue());
            }
        }
    }
}
