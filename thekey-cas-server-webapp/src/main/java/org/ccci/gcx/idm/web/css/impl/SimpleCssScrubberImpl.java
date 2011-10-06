package org.ccci.gcx.idm.web.css.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.ccci.gcx.idm.web.css.AbstractCssScrubber;
import org.ccci.gto.cas.service.css.CssValidator;

public class SimpleCssScrubberImpl extends AbstractCssScrubber {
    private final ArrayList<CssValidator> validators = new ArrayList<CssValidator>();

    /**
     * Retrieves css from cssUrl and checks for rule violations. If any of the
     * regex's are found it fails the css. It does not actually "scrub" or
     * remove the offending sections but rather simply returns an empty string
     * if it fails.
     * 
     * No caching. It fetches the css *every* time. Hey, its a place to start.
     */
    @Override
    public String scrub(final URI uri) {
	final String css = fetchCssContent(uri);

	for (final CssValidator validator : validators) {
	    if (!validator.isValid(css)) {
		return "";
	    }
	}

	return css;
    }

    /**
     * @param validators
     *            the css validators to use when validating css
     */
    public void setValidators(
	    final Collection<? extends CssValidator> validators) {
	this.validators.clear();
	if (validators != null) {
	    this.validators.addAll(validators);
	}
    }
}
