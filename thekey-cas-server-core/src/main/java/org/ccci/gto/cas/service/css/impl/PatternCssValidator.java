package org.ccci.gto.cas.service.css.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class PatternCssValidator extends AbstractCssValidator {
    private final ArrayList<Pattern> patterns = new ArrayList<Pattern>();

    @Override
    public boolean isValid(final String css) {
	// strip whitespace before processing
	final String css2 = css.replaceAll("\\s", "");

	// check to see if the css matches any of the configured patterns
	for (final Pattern pattern : patterns) {
	    if (pattern.matcher(css2).find()) {
		return false;
	    }
	}

	// no patterns match, so the css is valid
	return true;
    }

    /**
     * this method sets the patterns to be blocked by the PatternCssValidator
     * 
     * @param patterns
     *            the invalid patterns
     */
    public void setInvalidPatterns(final Collection<? extends String> patterns) {
	this.patterns.clear();
	if (patterns != null) {
	    for (final String rule : patterns) {
		this.patterns.add(Pattern.compile(rule));
	    }
	}
    }
}
