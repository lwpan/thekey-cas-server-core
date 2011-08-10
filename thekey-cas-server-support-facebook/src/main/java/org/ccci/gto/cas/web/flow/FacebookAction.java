package org.ccci.gto.cas.web.flow;

import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.RequestContext;

public class FacebookAction {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public FacebookCredentials parse(final RequestContext context,
	    final boolean vivify) throws Exception {
	// generate the facebook credentials for the current request
	final FacebookCredentials credentials = new FacebookCredentials();
	credentials.setSignedRequest(context.getRequestParameters().get(
		"fb_signed_request"));
	credentials.setVivify(vivify);

	// return the parsed facebook credentials
	return credentials;
    }
}
