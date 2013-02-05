package org.ccci.gto.cas.relay.services.web;

import static org.ccci.gto.cas.Constants.VIEW_ATTR_REQUESTURI;
import static org.ccci.gto.cas.relay.Constants.VIEW_ATTR_RELAYURI;

import java.net.URI;

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
        // set the relay uri used for generating the relay login link
        context.setAttribute(VIEW_ATTR_RELAYURI, this.relayUri);

        // remove any relay login parameter vestiges from the request uri
        final Object requestUri = context.getAttribute(VIEW_ATTR_REQUESTURI);
        final UriBuilder uri;
        if (requestUri instanceof URI) {
            uri = UriBuilder.fromUri((URI) requestUri);
        } else {
            uri = UriBuilder.fromUri(context.getRequestUri());
        }
        uri.replaceQueryParam("lt");
        uri.replaceQueryParam("execution");
        uri.replaceQueryParam("_eventId");
        uri.replaceQueryParam("ticket");

        uri.replaceQueryParam("loginMethod");

        // store the updated url
        context.setAttribute(VIEW_ATTR_REQUESTURI, uri.build());
    }
}
