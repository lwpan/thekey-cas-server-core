package org.ccci.gto.cas.services.web;

import static org.ccci.gto.cas.Constants.VIEW_ATTR_COMMONURIPARAMS;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

public final class CommonUriParamsViewPopulator extends AbstractViewPopulator {
    private Collection<String> params = Collections.emptyList();

    public void setParams(final Collection<String> params) {
        if (params != null) {
            this.params = params;
        } else {
            this.params = Collections.emptyList();
        }
    }

    @Override
    protected void populateInternal(final ViewContext context) {
        final HttpServletRequest request = context.getRequest();

        final UriBuilder uri = UriBuilder.fromPath("/");
        for (final String param : this.params) {
            final String[] values = request.getParameterValues(param);
            if (values != null) {
                uri.queryParam(param, (Object[]) values);
            }
        }

        // put the common URI params in the ViewContext
        context.setAttribute(VIEW_ATTR_COMMONURIPARAMS, uri.build().getRawQuery());
    }
}
