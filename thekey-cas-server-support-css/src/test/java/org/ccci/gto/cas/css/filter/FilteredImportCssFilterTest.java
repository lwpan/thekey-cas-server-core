package org.ccci.gto.cas.css.filter;

import java.io.IOException;

import org.ccci.gto.cas.css.AbstractParserTest;
import org.ccci.gto.cas.css.filter.ReversibleFilter.Type;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

public final class FilteredImportCssFilterTest extends AbstractParserTest {
    private FilteredImportCssFilter getFilter() {
	return new FilteredImportCssFilter();
    }

    public void testFilter() throws IOException {

	// test relative urls
	{
	    final String rawCss = "@import \"1.css\";" + "@import \"2.css\";"
		    + "@import \"http://example.com/1.css\";";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), null);
	    final FilteredImportCssFilter filter = this.getFilter();
	    filter.setFilterType(Type.WHITELIST);
	    filter.setFilterUri("?");
	    filter.addUri("1.css");
	    filter.addUri("http://example.com/2.css");
	    filter.filter(css);

	    final CSSRuleList rules = css.getCssRules();
	    assertEquals(3, rules.getLength());
	    assertEquals("?defaultFallback=false&css=1.css",
		    ((CSSImportRule) rules.item(0)).getHref());
	    assertEquals("?defaultFallback=false&css=2.css",
		    ((CSSImportRule) rules.item(1)).getHref());
	    assertEquals("?defaultFallback=false&css=http://example.com/1.css",
		    ((CSSImportRule) rules.item(2)).getHref());
	}

	// test a single whitelisted url
	{
	    final String rawCss = "@import \"http://example.com/1.css\";"
		    + "@import \"http://example.com/2.css\";"
		    + "@import \"http://example.com/3.css\";";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), null);
	    final FilteredImportCssFilter filter = this.getFilter();
	    filter.setFilterUri("?");
	    filter.setFilterType(Type.WHITELIST);
	    filter.addUri("http://example.com/3.css");
	    filter.filter(css);

	    final CSSRuleList rules = css.getCssRules();
	    assertEquals(3, rules.getLength());
	    assertEquals("?defaultFallback=false&css=http://example.com/1.css",
		    ((CSSImportRule) rules.item(0)).getHref());
	    assertEquals("?defaultFallback=false&css=http://example.com/2.css",
		    ((CSSImportRule) rules.item(1)).getHref());
	    assertEquals("http://example.com/3.css",
		    ((CSSImportRule) rules.item(2)).getHref());
	}

	// test multiple whitelisted urls
	{
	    final String rawCss = "@import \"http://example.com/1.css\";"
		    + "@import \"http://example.com/2.css\";"
		    + "@import \"http://example.com/3.css\";";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), null);
	    final FilteredImportCssFilter filter = this.getFilter();
	    filter.setFilterUri("?");
	    filter.setFilterType(Type.WHITELIST);
	    filter.addUri("http://example.com/2.css");
	    filter.addUri("http://example.com/3.css");
	    filter.filter(css);

	    final CSSRuleList rules = css.getCssRules();
	    assertEquals(3, rules.getLength());
	    assertEquals("?defaultFallback=false&css=http://example.com/1.css",
		    ((CSSImportRule) rules.item(0)).getHref());
	    assertEquals("http://example.com/2.css",
		    ((CSSImportRule) rules.item(1)).getHref());
	    assertEquals("http://example.com/3.css",
		    ((CSSImportRule) rules.item(2)).getHref());
	}

	// test a single blacklisted url
	{
	    final String rawCss = "@import \"http://example.com/1.css\";"
		    + "@import \"http://example.com/2.css\";"
		    + "@import \"http://example.com/3.css\";";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), null);
	    final FilteredImportCssFilter filter = this.getFilter();
	    filter.setFilterUri("?");
	    filter.setFilterType(Type.BLACKLIST);
	    filter.addUri("http://example.com/3.css");
	    filter.filter(css);

	    final CSSRuleList rules = css.getCssRules();
	    assertEquals(3, rules.getLength());
	    assertEquals("http://example.com/1.css",
		    ((CSSImportRule) rules.item(0)).getHref());
	    assertEquals("http://example.com/2.css",
		    ((CSSImportRule) rules.item(1)).getHref());
	    assertEquals("?defaultFallback=false&css=http://example.com/3.css",
		    ((CSSImportRule) rules.item(2)).getHref());
	}

	// test multiple blacklisted urls
	{
	    final String rawCss = "@import \"http://example.com/1.css\";"
		    + "@import \"http://example.com/2.css\";"
		    + "@import \"http://example.com/3.css\";";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), null);
	    final FilteredImportCssFilter filter = this.getFilter();
	    filter.setFilterUri("?");
	    filter.setFilterType(Type.BLACKLIST);
	    filter.addUri("http://example.com/2.css");
	    filter.addUri("http://example.com/3.css");
	    filter.filter(css);

	    final CSSRuleList rules = css.getCssRules();
	    assertEquals(3, rules.getLength());
	    assertEquals("http://example.com/1.css",
		    ((CSSImportRule) rules.item(0)).getHref());
	    assertEquals("?defaultFallback=false&css=http://example.com/2.css",
		    ((CSSImportRule) rules.item(1)).getHref());
	    assertEquals("?defaultFallback=false&css=http://example.com/3.css",
		    ((CSSImportRule) rules.item(2)).getHref());
	}

	// test an undefined filter type
	{
	    final String rawCss = "@import \"http://example.com/1.css\";"
		    + "@import \"http://example.com/2.css\";"
		    + "@import \"http://example.com/3.css\";";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), null);
	    final FilteredImportCssFilter filter = this.getFilter();
	    filter.setFilterUri("?");
	    filter.addUri("http://example.com/3.css");
	    filter.filter(css);

	    final CSSRuleList rules = css.getCssRules();
	    assertEquals(3, rules.getLength());
	    assertEquals("?defaultFallback=false&css=http://example.com/1.css",
		    ((CSSImportRule) rules.item(0)).getHref());
	    assertEquals("?defaultFallback=false&css=http://example.com/2.css",
		    ((CSSImportRule) rules.item(1)).getHref());
	    assertEquals("?defaultFallback=false&css=http://example.com/3.css",
		    ((CSSImportRule) rules.item(2)).getHref());
	}

	// test a different filter uri
	{
	    final String rawCss = "@import \"http://example.com/1.css\";"
		    + "@import \"http://example.com/2.css\";"
		    + "@import \"http://example.com/3.css\";";
	    final CSSStyleSheet css = this.parseCss(
		    this.getStringInputSource(rawCss), null);
	    final FilteredImportCssFilter filter = this.getFilter();
	    filter.setFilterUri("a?b=c");
	    filter.setFilterType(Type.WHITELIST);
	    filter.addUri("http://example.com/3.css");
	    filter.filter(css);

	    final CSSRuleList rules = css.getCssRules();
	    assertEquals(3, rules.getLength());
	    assertEquals(
		    "a?b=c&defaultFallback=false&css=http://example.com/1.css",
		    ((CSSImportRule) rules.item(0)).getHref());
	    assertEquals(
		    "a?b=c&defaultFallback=false&css=http://example.com/2.css",
		    ((CSSImportRule) rules.item(1)).getHref());
	    assertEquals("http://example.com/3.css",
		    ((CSSImportRule) rules.item(2)).getHref());
	}
    }
}
