package org.ccci.gto.cas.services.web;

import static org.ccci.gto.cas.Constants.VIEW_ATTR_REQUESTURI;

import java.net.URI;

public final class RequestUriViewPopulator extends AbstractViewPopulator {
    @Override
    protected void populateInternal(final ViewContext context) {
        // initialize the Request Uri if it hasn't already been set
        if (!(context.getAttribute(VIEW_ATTR_REQUESTURI) instanceof URI)) {
            context.setAttribute(VIEW_ATTR_REQUESTURI, context.getRequestUri());
        }
    }
}
