package org.ccci.gto.cas.relay.util;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

public final class RelayUtil {
    public static final String extractService(final RequestContext context) {
        // retrieve the request url which is required for the CasCredentials
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
        final StringBuffer service = request.getRequestURL().append("?").append(request.getQueryString());
        final int ticketIndex = service.lastIndexOf("ticket=");
        if (ticketIndex > 0) {
            service.delete(ticketIndex - 1, service.length());
        }

        // return the service as a String
        return service.toString();
    }
}
