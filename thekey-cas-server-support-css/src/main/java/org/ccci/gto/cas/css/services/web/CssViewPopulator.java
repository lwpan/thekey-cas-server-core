package org.ccci.gto.cas.css.services.web;

import static org.ccci.gto.cas.css.Constants.PARAMETER_TEMPLATEURL;
import static org.ccci.gto.cas.css.Constants.VIEW_ATTR_TEMPLATEURL;
import static org.ccci.gto.cas.oauth.Constants.FLOW_ATTR_CLIENT;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.ccci.gto.cas.services.web.AbstractViewPopulator;
import org.ccci.gto.cas.services.web.ViewContext;
import org.jasig.cas.services.RegisteredService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CssViewPopulator extends AbstractViewPopulator {
    private static final Logger LOG = LoggerFactory.getLogger(CssViewPopulator.class);

    @Override
    protected void populateInternal(final ViewContext context) {
	final HttpServletRequest request = context.getRequest();

	// set the template to use for this request
	URI templateUri = null;

	// check for a specified template first
	if (templateUri == null) {
	    final String template = request.getParameter(PARAMETER_TEMPLATEURL);
	    if (StringUtils.isNotBlank(template)) {
		try {
		    templateUri = new URI(template);
		} catch (final URISyntaxException e) {
		    log.debug("Malformed template parameter", e);
		    templateUri = null;
		}
	    }
	}

        // check for a template defined for an OAuth client
        if (templateUri == null) {
            // we load the oauth client from the flow scope
            final Object client = context.getAttribute(FLOW_ATTR_CLIENT);
            if (client instanceof Client) {
                final String template = ((Client) client).getTemplateCssUrl();
                if (StringUtils.isNotBlank(template)) {
                    try {
                        templateUri = new URI(template);
                    } catch (final URISyntaxException e) {
                        LOG.error("Invalid templateCssUrl in OAuth Client", e);
                        templateUri = null;
                    }
                }
            }
        }

	// check for the template URL in the RegisteredService
	if (templateUri == null) {
	    final RegisteredService rService = context.getRegisteredService();
	    if (rService instanceof TheKeyRegisteredService) {
		final String template = ((TheKeyRegisteredService) rService)
			.getTemplateCssUrl();
		if (StringUtils.isNotBlank(template)) {
		    try {
			templateUri = new URI(template);
		    } catch (final URISyntaxException e) {
			log.debug(
				"Invalid templateCssUrl in RegisteredService",
				e);
			templateUri = null;
		    }
		}
	    }
	}

	// set the template url in the view context
	context.setAttribute(VIEW_ATTR_TEMPLATEURL, templateUri);
    }
}
