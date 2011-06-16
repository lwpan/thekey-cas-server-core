package org.ccci.gto.cas.web.flow;

import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.RequestContext;

public class FacebookAction {
    /** Instance of logging for subclasses. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public FacebookCredentials parse(final RequestContext context,
	    final boolean vivify) throws Exception {
	// retrieve the access token
	final String accessToken = context.getRequestParameters().get("fb_key");

	// generate the facebook credentials for the current request
	final FacebookCredentials credentials = new FacebookCredentials("");
	credentials.setAccessToken(accessToken);
	credentials.setVivify(vivify);

	// return the parsed facebook credentials
	return credentials;
    }
}