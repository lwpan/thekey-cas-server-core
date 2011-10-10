package org.ccci.gto.cas.css.scrubber;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

public class ParsingCssScrubberTest extends TestCase {
    private ParsingCssScrubber getCssScrubber() {
	final ParsingCssScrubber scrubber = new ParsingCssScrubber();
	scrubber.setBlockedProperties(null);
	scrubber.setBlockedTypes(null);
	scrubber.setBlockedPropertyValues(null);
	return scrubber;
    }

    private InputSource getFileInputSource(final String fileName) {
	final InputStream is = getClass().getClassLoader().getResourceAsStream(
		fileName);
	assertNotNull(is);
	return new InputSource(new InputStreamReader(is));
    }

    private InputSource getStringInputSource(final String css) {
	return new InputSource(new StringReader(css));
    }

    public void testParser() throws IOException {
	final ParsingCssScrubber scrubber = this.getCssScrubber();

	{
	    final CSSStyleSheet css = scrubber
		    .parse(getStringInputSource(".foo { font-weight: normal !/* comment */important; }"));
	    assertEquals(1, css.getCssRules().getLength());
	    final CSSRule rule = css.getCssRules().item(0);
	    assertEquals(CSSRule.STYLE_RULE, rule.getType());
	    assertEquals(1, ((CSSStyleRule) rule).getStyle().getLength());
	}
    }

    public void testBlockRules() throws IOException {
	final ParsingCssScrubber scrubber = this.getCssScrubber();
	final String RULES = "@import url('a'); .foo { behavior: test2; -mm-behavior: test2; font-weight: normal !/* comment */important; behavior: test; font-weight: expression(a); }";
	final int RULES_TOTAL = 2;
	final int RULES_IMPORT = 1;
	final int ATTRS_TOTAL = 5;
	final int ATTRS_BEHAVIOR = 2;
	final int ATTRS_EXPRESSION = 1;

	/* nothing blocked */
	{
	    final CSSStyleSheet css = scrubber
		    .scrub(getStringInputSource(RULES));
	    final CSSRuleList rules = css.getCssRules();
	    final CSSRule rule = rules.item(rules.getLength() - 1);
	    assertEquals(RULES_TOTAL, rules.getLength());
	    assertEquals(CSSRule.STYLE_RULE, rule.getType());
	    assertEquals(ATTRS_TOTAL, ((CSSStyleRule) rule).getStyle()
		    .getLength());
	}

	/* block import rules */
	{
	    scrubber.addBlockedType(CSSRule.IMPORT_RULE);
	    final CSSStyleSheet css = scrubber
		    .scrub(getStringInputSource(RULES));
	    final CSSRuleList rules = css.getCssRules();
	    final CSSRule rule = rules.item(rules.getLength() - 1);
	    assertEquals(RULES_TOTAL - RULES_IMPORT, rules.getLength());
	    assertEquals(CSSRule.STYLE_RULE, rule.getType());
	    assertEquals(ATTRS_TOTAL, ((CSSStyleRule) rule).getStyle()
		    .getLength());
	    scrubber.setBlockedTypes(null);
	}

	/* block behavior properties */
	{
	    scrubber.addBlockedProperty("behavior");
	    final CSSStyleSheet css = scrubber
		    .scrub(getStringInputSource(RULES));
	    final CSSRuleList rules = css.getCssRules();
	    final CSSRule rule = rules.item(rules.getLength() - 1);
	    assertEquals(RULES_TOTAL, rules.getLength());
	    assertEquals(CSSRule.STYLE_RULE, rule.getType());
	    assertEquals(ATTRS_TOTAL - ATTRS_BEHAVIOR, ((CSSStyleRule) rule)
		    .getStyle().getLength());
	    scrubber.setBlockedProperties(null);
	}

	/* block import rules and behavior properties */
	{
	    scrubber.addBlockedType(CSSRule.IMPORT_RULE);
	    scrubber.addBlockedProperty("behavior");
	    final CSSStyleSheet css = scrubber
		    .scrub(getStringInputSource(RULES));
	    final CSSRuleList rules = css.getCssRules();
	    final CSSRule rule = rules.item(rules.getLength() - 1);
	    assertEquals(RULES_TOTAL - RULES_IMPORT, rules.getLength());
	    assertEquals(CSSRule.STYLE_RULE, rule.getType());
	    assertEquals(ATTRS_TOTAL - ATTRS_BEHAVIOR, ((CSSStyleRule) rule)
		    .getStyle().getLength());
	    scrubber.setBlockedProperties(null);
	    scrubber.setBlockedTypes(null);
	}

	/* block properties containing expressions */
	{
	    scrubber.addBlockedPropertyValue("expression");
	    final CSSStyleSheet css = scrubber
		    .scrub(getStringInputSource(RULES));
	    final CSSRuleList rules = css.getCssRules();
	    final CSSRule rule = rules.item(rules.getLength() - 1);
	    assertEquals(RULES_TOTAL, rules.getLength());
	    assertEquals(CSSRule.STYLE_RULE, rule.getType());
	    assertEquals(ATTRS_TOTAL - ATTRS_EXPRESSION, ((CSSStyleRule) rule)
		    .getStyle().getLength());
	    scrubber.setBlockedPropertyValues(null);
	}

	/*
	 * block import rules, behavior properties, and properties containing
	 * expressions
	 */
	{
	    scrubber.addBlockedType(CSSRule.IMPORT_RULE);
	    scrubber.addBlockedProperty("behavior");
	    scrubber.addBlockedPropertyValue("expression");
	    final CSSStyleSheet css = scrubber
		    .scrub(getStringInputSource(RULES));
	    final CSSRuleList rules = css.getCssRules();
	    final CSSRule rule = rules.item(rules.getLength() - 1);
	    assertEquals(RULES_TOTAL - RULES_IMPORT, rules.getLength());
	    assertEquals(CSSRule.STYLE_RULE, rule.getType());
	    assertEquals(ATTRS_TOTAL - ATTRS_BEHAVIOR - ATTRS_EXPRESSION,
		    ((CSSStyleRule) rule).getStyle().getLength());
	    scrubber.setBlockedPropertyValues(null);
	    scrubber.setBlockedProperties(null);
	    scrubber.setBlockedTypes(null);
	}
    }
}
