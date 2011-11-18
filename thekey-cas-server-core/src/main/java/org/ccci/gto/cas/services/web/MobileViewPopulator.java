package org.ccci.gto.cas.services.web;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public final class MobileViewPopulator extends AbstractViewPopulator {
    private final HashMap<Pattern, String> overrides = new HashMap<Pattern, String>();

    @Override
    protected void populateInternal(final ViewContext context) {
	final HttpServletRequest request = context.getRequest();

	// check to see if this is a mobile user agent
	final String userAgent = request.getHeader("User-Agent");
	if (userAgent != null) {
	    for (final Map.Entry<Pattern, String> entry : this.overrides
		    .entrySet()) {
		if (entry.getKey().matcher(userAgent).matches()) {
		    request.setAttribute("isMobile", "true");
		    request.setAttribute("browserType", entry.getValue());
		    break;
		}
	    }
	}
    }

    public void setMobileBrowsers(final Map<String, String> mobileOverrides) {
	overrides.clear();
	for (final Map.Entry<String, String> entry : mobileOverrides.entrySet()) {
	    this.overrides.put(Pattern.compile(entry.getKey()),
		    entry.getValue());
	}
    }
}
