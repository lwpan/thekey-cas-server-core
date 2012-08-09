package org.ccci.gto.cas.relay.services.web;

import static org.ccci.gto.cas.relay.Constants.VIEW_ATTR_RELAYURI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;

import org.ccci.gto.cas.services.web.AbstractViewPopulator;
import org.ccci.gto.cas.services.web.ViewContext;

public final class RelayViewPopulator extends AbstractViewPopulator {
    @NotNull
    private String relayUri;

    public void setRelayUri(final String relayUri) {
        this.relayUri = relayUri + (relayUri.endsWith("/") ? "" : "/");
    }

    @Override
    protected void populateInternal(final ViewContext context) {
        context.setAttribute(VIEW_ATTR_RELAYURI, this.relayUri);

        final HttpServletRequest request = context.getRequest();
        final UriBuilder uri = UriBuilder.fromUri(request.getRequestURL().toString());
        uri.replaceQuery(request.getQueryString());
        context.setAttribute("requestUri", uri.build().toString());
    }
}
