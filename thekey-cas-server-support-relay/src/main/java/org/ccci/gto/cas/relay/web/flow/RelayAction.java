package org.ccci.gto.cas.relay.web.flow;

import javax.servlet.http.HttpServletRequest;

import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

public class RelayAction {
    public CasCredentials parse(final RequestContext context) throws Exception {
        // retrieve the request url which is required for the CasCredentials
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
        final StringBuffer service = request.getRequestURL().append("?").append(request.getQueryString());
        final int ticketIndex = service.lastIndexOf("ticket=");
        if (ticketIndex > 0) {
            service.delete(ticketIndex - 1, service.length());
        }

        // generate CasCredentials for the current request
        final CasCredentials credentials = new CasCredentials();
        credentials.setService(service.toString());
        credentials.setTicket(context.getRequestParameters().get("ticket"));

        // return the parsed CAS credentials
        return credentials;
    }
}
