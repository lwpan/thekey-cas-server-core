package org.ccci.gto.cas.services.web;

import static org.ccci.gto.cas.Constants.VIEW_ATTR_SERVICEDOMAIN;

import java.net.MalformedURLException;
import java.net.URL;

import org.jasig.cas.authentication.principal.Service;

public final class ServiceViewPopulator extends AbstractViewPopulator {
    @Override
    protected void populateInternal(final ViewContext context) {
	final Service service = context.getService();
	if(service != null) {
	    try {
		final URL serviceUrl = new URL(service.getId());
		context.setAttribute(VIEW_ATTR_SERVICEDOMAIN,
			serviceUrl.getHost());
	    } catch (final MalformedURLException e) {
		// suppress the exception since it's not fatal
		log.debug("Error extracting service domain from service", e);
	    }
	}
    }
}
