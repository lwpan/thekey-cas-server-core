package org.ccci.gto.cas.css.scrubber;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.ccci.gto.cas.css.filter.CssFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.CSSOMParser;

public class ParsingCssScrubber implements CssScrubber {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final ArrayList<CssFilter> filters = new ArrayList<CssFilter>();

    private final HashSet<String> blockedProperties = new HashSet<String>();
    private final ArrayList<Pattern> blockedPropertyValues = new ArrayList<Pattern>();

    public void addBlockedProperty(final String name) {
	if (name != null) {
	    this.blockedProperties.add(name);
	}
    }

    public void setBlockedProperties(final Collection<? extends String> names) {
	this.blockedProperties.clear();
	if (names != null) {
	    this.blockedProperties.addAll(names);
	}
    }

    public void addBlockedPropertyValue(final String pattern) {
	if (pattern != null) {
	    this.blockedPropertyValues.add(Pattern.compile(pattern));
	}
    }

    public void setBlockedPropertyValues(
	    final Collection<? extends String> patterns) {
	this.blockedPropertyValues.clear();
	if (patterns != null) {
	    for (final String pattern : patterns) {
		if (pattern != null) {
		    this.blockedPropertyValues.add(Pattern.compile(pattern));
		}
	    }
	}
    }

    public void addFilter(final CssFilter filter) {
	this.filters.add(filter);
    }

    public void setFilters(final Collection<? extends CssFilter> filters) {
	this.filters.clear();
	if (filters != null) {
	    this.filters.addAll(filters);
	}
    }

    protected CSSStyleSheet parse(final InputSource source) throws IOException {
	try {
	    final CSSOMParser parser = new CSSOMParser();
	    return parser.parseStyleSheet(source, null, null);
	} catch (final IOException e) {
	    log.debug("error parsing CSS", e);
	    throw e;
	}
    }

    public String scrub(final URI uri) {
	try {
	    final CSSStyleSheet css = this.scrub(
		    new InputSource(uri.toString()), uri);
	    return css.toString();
	} catch (final Exception e) {
	    log.debug("error scrubbing CSS, returning empty CSS");
	    return "";
	}
    }

    protected CSSStyleSheet scrub(final InputSource source, final URI uri)
	    throws IOException {
	CSSStyleSheet css = this.parse(source);

	// process any defined css filters
	if (!filters.isEmpty()) {
	    for (final CssFilter filter : filters) {
		css = filter.filter(css, uri);
	    }
	}

	// iterate over all the CSS rules
	final CSSRuleList rules = css.getCssRules();
	for (int i = 0; i < rules.getLength(); i++) {
	    final CSSRule rule = rules.item(i);

	    switch (rule.getType()) {
	    case CSSRule.STYLE_RULE:
		if (this.blockedProperties.size() > 0
			|| this.blockedPropertyValues.size() > 0) {
		    this.scrubStyleRule((CSSStyleRule) rule);
		}
		break;
	    }
	}

	return css;
    }

    private void scrubStyleRule(final CSSStyleRule rule) {
	final CSSStyleDeclaration styles = rule.getStyle();

	// the set of properties to remove from this rule
	final HashSet<String> propertiesToRemove = new HashSet<String>();

	// remove any blocked properties
	propertiesToRemove.addAll(blockedProperties);

	/* only process the properties if there are blocked values */
	if (this.blockedPropertyValues.size() > 0) {
	    /*
	     * for {@link CSSStyleDeclarationImpl} we can directly manipulate
	     * the Property objects to get around the standard API limitations
	     */
	    if (styles instanceof CSSStyleDeclarationImpl) {
		final List<Property> properties = ((CSSStyleDeclarationImpl) styles)
			.getProperties();
		for (int j = 0; j < properties.size(); j++) {
		    final Property property = properties.get(j);
		    final String value = property.getValue().toString();
		    for (final Pattern pattern : this.blockedPropertyValues) {
			if (pattern.matcher(value).find()) {
			    properties.remove(j);
			    j--;
			    break;
			}
		    }
		}
	    } else {
		final HashSet<String> seen = new HashSet<String>();
		for (int j = 0; j < styles.getLength(); j++) {
		    final String name = styles.item(j);
		    final String value = styles.getPropertyValue(name);

		    /*
		     * remove any duplicate properties because the standard API
		     * doesn't provide a way to manipulate properties with the
		     * same name that are not the first property
		     */
		    if (seen.contains(name.toLowerCase())) {
			propertiesToRemove.add(name);
		    }

		    /*
		     * check to see if the value matches a blocked pattern
		     */
		    for (final Pattern pattern : this.blockedPropertyValues) {
			if (pattern.matcher(value).find()) {
			    propertiesToRemove.add(name);
			    break;
			}
		    }

		    /*
		     * add this property to the list of seen properties
		     */
		    seen.add(name.toLowerCase());
		}
	    }
	}

	/* remove all properties that need to be removed */
	for (final String property : propertiesToRemove) {
	    /*
	     * removeProperty only removes the first property with the specified
	     * name, loop until all properties using the specified name are
	     * removed
	     */
	    while (true) {
		final int properties = styles.getLength();
		styles.removeProperty(property);
		if (properties == styles.getLength()) {
		    break;
		}
	    }
	}
    }
}
