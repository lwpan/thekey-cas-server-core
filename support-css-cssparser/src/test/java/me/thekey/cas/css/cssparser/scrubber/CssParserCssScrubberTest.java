package me.thekey.cas.css.cssparser.scrubber;

import static org.junit.Assert.assertEquals;

import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.Property;
import me.thekey.cas.css.cssparser.AbstractParserTest;
import me.thekey.cas.css.cssparser.scrubber.filter.CssFilter;
import me.thekey.cas.css.cssparser.scrubber.filter.PropertyNameCssFilter;
import me.thekey.cas.css.cssparser.scrubber.filter.PropertyValueCssFilter;
import me.thekey.cas.css.scrubber.filter.ReversibleFilter.Type;
import me.thekey.cas.css.cssparser.scrubber.filter.RuleTypeCssFilter;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CssParserCssScrubberTest extends AbstractParserTest {
    private final static String FILTER_IMPORT = "blockImport";
    private final static String FILTER_BEHAVIOR = "blockBehavior";
    private final static String FILTER_EXPRESSION = "blockExpression";
    private final static String FILTER_MOZ_BINDING = "blockMozBinding";

    private CssParserCssScrubber getCssScrubber() {
        final CssParserCssScrubber scrubber = new CssParserCssScrubber();
        scrubber.setFilters(null);
        return scrubber;
    }

    @Test
    public void testParser() throws IOException {
        final CssParserCssScrubber scrubber = this.getCssScrubber();

        {
            final CSSStyleSheet css = scrubber.parse(getStringInputSource(".foo { font-weight: normal !/* comment " +
                    "*/important; }"), null);
            assertEquals(1, css.getCssRules().getLength());
            final CSSRule rule = css.getCssRules().item(0);
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(1, ((CSSStyleRule) rule).getStyle().getLength());
        }
    }

    @Test
    public void testBlockRules() throws IOException {
        final CssParserCssScrubber scrubber = this.getCssScrubber();
        final String RULES = "@import url('a'); .foo { behavior: test2; -mm-behavior: test2; font-weight: normal !/* comment */important; behavior: test; font-weight: expression(a); -moz-binding: url(script.xml#mycode); }";
        final int RULES_TOTAL = 2;
        final int RULES_IMPORT = 1;
        final int ATTRS_TOTAL = 6;
        final int ATTRS_BEHAVIOR = 2;
        final int ATTRS_EXPRESSION = 1;
        final int ATTRS_MOZ_BINDING = 1;

        // generate CssFilters
        final HashMap<String, CssFilter> filters = new HashMap<String, CssFilter>();
        {
            final RuleTypeCssFilter filter = new RuleTypeCssFilter();
            filter.setFilterType(Type.BLACKLIST);
            filter.addType(CSSRule.IMPORT_RULE);
            filters.put(FILTER_IMPORT, filter);
        }
        {
            final PropertyNameCssFilter filter = new PropertyNameCssFilter();
            filter.setFilterType(Type.BLACKLIST);
            filter.addName("behavior");
            filters.put(FILTER_BEHAVIOR, filter);
        }
        {
            final PropertyNameCssFilter filter = new PropertyNameCssFilter();
            filter.setFilterType(Type.BLACKLIST);
            filter.addName("-moz-binding");
            filters.put(FILTER_MOZ_BINDING, filter);
        }
        {
            final PropertyValueCssFilter filter = new PropertyValueCssFilter();
            filter.setFilterType(Type.BLACKLIST);
            filter.addValue("expression");
            filters.put(FILTER_EXPRESSION, filter);
        }

        // nothing blocked
        {
            final CSSStyleSheet css = scrubber.scrub(getStringInputSource(RULES), null);
            final CSSRuleList rules = css.getCssRules();
            final CSSRule rule = rules.item(rules.getLength() - 1);
            assertEquals(RULES_TOTAL, rules.getLength());
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(ATTRS_TOTAL, ((CSSStyleRule) rule).getStyle().getLength());
        }

        // block import rules
        {
            scrubber.addFilter(filters.get(FILTER_IMPORT));
            final CSSStyleSheet css = scrubber.scrub(getStringInputSource(RULES), null);
            final CSSRuleList rules = css.getCssRules();
            final CSSRule rule = rules.item(rules.getLength() - 1);
            assertEquals(RULES_TOTAL - RULES_IMPORT, rules.getLength());
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(ATTRS_TOTAL, ((CSSStyleRule) rule).getStyle().getLength());
            scrubber.setFilters(null);
        }

        // block behavior properties
        {
            scrubber.addFilter(filters.get(FILTER_BEHAVIOR));
            final CSSStyleSheet css = scrubber.scrub(getStringInputSource(RULES), null);
            final CSSRuleList rules = css.getCssRules();
            final CSSRule rule = rules.item(rules.getLength() - 1);
            assertEquals(RULES_TOTAL, rules.getLength());
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(ATTRS_TOTAL - ATTRS_BEHAVIOR, ((CSSStyleRule) rule).getStyle().getLength());
            scrubber.setFilters(null);
        }

        // block import rules and behavior properties
        {
            scrubber.addFilter(filters.get(FILTER_IMPORT));
            scrubber.addFilter(filters.get(FILTER_BEHAVIOR));
            final CSSStyleSheet css = scrubber.scrub(getStringInputSource(RULES), null);
            final CSSRuleList rules = css.getCssRules();
            final CSSRule rule = rules.item(rules.getLength() - 1);
            assertEquals(RULES_TOTAL - RULES_IMPORT, rules.getLength());
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(ATTRS_TOTAL - ATTRS_BEHAVIOR, ((CSSStyleRule) rule).getStyle().getLength());
            scrubber.setFilters(null);
        }

        // block properties containing expressions
        {
            scrubber.addFilter(filters.get(FILTER_EXPRESSION));
            final CSSStyleSheet css = scrubber.scrub(getStringInputSource(RULES), null);
            final CSSRuleList rules = css.getCssRules();
            final CSSRule rule = rules.item(rules.getLength() - 1);
            assertEquals(RULES_TOTAL, rules.getLength());
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(ATTRS_TOTAL - ATTRS_EXPRESSION, ((CSSStyleRule) rule).getStyle().getLength());
            scrubber.setFilters(null);
        }

        // block import rules, behavior properties, and properties containing expressions
        {
            scrubber.addFilter(filters.get(FILTER_IMPORT));
            scrubber.addFilter(filters.get(FILTER_BEHAVIOR));
            scrubber.addFilter(filters.get(FILTER_EXPRESSION));
            final CSSStyleSheet css = scrubber.scrub(getStringInputSource(RULES), null);
            final CSSRuleList rules = css.getCssRules();
            final CSSRule rule = rules.item(rules.getLength() - 1);
            assertEquals(RULES_TOTAL - RULES_IMPORT, rules.getLength());
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(ATTRS_TOTAL - ATTRS_BEHAVIOR - ATTRS_EXPRESSION, ((CSSStyleRule) rule).getStyle().getLength());
            scrubber.setFilters(null);
        }

        // block -moz-binding properties
        {
            scrubber.addFilter(filters.get(FILTER_MOZ_BINDING));
            final CSSStyleSheet css = scrubber.scrub(getStringInputSource(RULES), null);
            final CSSRuleList rules = css.getCssRules();
            final CSSRule rule = rules.item(rules.getLength() - 1);
            assertEquals(RULES_TOTAL, rules.getLength());
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(ATTRS_TOTAL - ATTRS_MOZ_BINDING, ((CSSStyleRule) rule).getStyle().getLength());
            scrubber.setFilters(null);
        }
    }

    @Test
    public void testParserFontFace() throws IOException {
        final CssParserCssScrubber scrubber = this.getCssScrubber();
        final String RULES = "@font-face { font-family: 'Roboto'; font-style: normal; font-weight: 400; src: local('Roboto Regular'), local('Roboto-Regular'), url(http://themes.googleusercontent.com/static/fonts/roboto/v8/CrYjSnGjrRCn0pd9VQsnFOvvDin1pK8aKteLpeZ5c0A.woff) format('woff');}";

        {
            final CSSStyleSheet css = scrubber.parse(getStringInputSource(RULES), null);
            assertEquals(1, css.getCssRules().getLength());
            final CSSRule rule = css.getCssRules().item(0);
            assertEquals(CSSRule.FONT_FACE_RULE, rule.getType());
            assertEquals(4, ((CSSFontFaceRule) rule).getStyle().getLength());
        }
    }

    @Test
    public void testParserRgba() throws IOException {
        final CssParserCssScrubber scrubber = this.getCssScrubber();
        final String RULES = "input {color: rgba(0, 0, 0, 0.1);}";

        {
            final CSSStyleSheet css = scrubber.parse(getStringInputSource(RULES), null);
            assertEquals(1, css.getCssRules().getLength());
            final CSSRule rule = css.getCssRules().item(0);
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(1, ((CSSStyleRule) rule).getStyle().getLength());
            final CSSValue value = ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("color");
            assertEquals(CSSValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
            assertEquals("rgba(0, 0, 0, 0.1)", value.getCssText());
        }
    }

    @Test
    @Ignore("Not supported yet")
    public void testParserMsGradient() throws Exception {
        final CssParserCssScrubber scrubber = this.getCssScrubber();
        final String RULES = ".btn-default { filter: progid:DXImageTransform.Microsoft.gradient" + "" +
                "(startColorstr='#ffffffff', endColorstr='#ffe0e0e0', GradientType=0);}";

        {
            final CSSStyleSheet css = scrubber.parse(getStringInputSource(RULES), null);
            assertEquals(1, css.getCssRules().getLength());
            final CSSRule rule = css.getCssRules().item(0);
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            assertEquals(1, ((CSSStyleRule) rule).getStyle().getLength());
            //TODO: finish test
        }
    }

    @Test
    public void testVendorProperties() throws Exception {
        final CssParserCssScrubber scrubber = this.getCssScrubber();
        final String RULES = "*.btn-default {background-image: -webkit-linear-gradient(top, #fff 0%, " +
                "#e0e0e0 100%);background-image: -o-linear-gradient(top, #fff 0%, " +
                "#e0e0e0 100%);background-image: -webkit-gradient(linear, left top, left bottom, from(#fff), " +
                "to(#e0e0e0));background-image: linear-gradient(to bottom, #fff 0%, #e0e0e0 100%);}";

        {
            final CSSStyleSheet css = scrubber.parse(getStringInputSource(RULES), null);
            assertEquals(1, css.getCssRules().getLength());
            final CSSRule rule = css.getCssRules().item(0);
            assertEquals(CSSRule.STYLE_RULE, rule.getType());
            final CSSStyleDeclaration style = ((CSSStyleRule) rule).getStyle();
            assertEquals(4, style.getLength());
            if (style instanceof CSSStyleDeclarationImpl) {
                final List<Property> properties = ((CSSStyleDeclarationImpl) style).getProperties();
                assertEquals(4, properties.size());
                for (final Property property : properties) {
                    assertEquals("background-image", property.getName());
                }
            }
        }
    }
}
