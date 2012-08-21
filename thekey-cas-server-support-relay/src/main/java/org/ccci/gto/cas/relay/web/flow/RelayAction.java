package org.ccci.gto.cas.relay.web.flow;

import org.ccci.gto.cas.relay.authentication.principal.CasCredentials;
import org.ccci.gto.cas.relay.util.RelayUtil;
import org.springframework.webflow.execution.RequestContext;

public class RelayAction {
    public CasCredentials parse(final RequestContext context) throws Exception {
        // generate CasCredentials for the current request
        final CasCredentials credentials = new CasCredentials();
        credentials.setService(RelayUtil.extractService(context));
        credentials.setTicket(context.getRequestParameters().get("ticket"));

        // return the parsed CAS credentials
        return credentials;
    }
}
