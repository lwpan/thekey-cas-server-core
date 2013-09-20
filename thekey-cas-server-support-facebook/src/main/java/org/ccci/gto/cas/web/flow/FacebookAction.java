package org.ccci.gto.cas.web.flow;

import static org.ccci.gto.cas.facebook.Constants.PARAMETER_SIGNED_REQUEST;

import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.springframework.webflow.execution.RequestContext;

public class FacebookAction {
    public FacebookCredentials parse(final RequestContext context) throws Exception {
	// generate the facebook credentials for the current request
	final FacebookCredentials credentials = new FacebookCredentials();
	credentials.setSignedRequest(context.getRequestParameters().get(
		PARAMETER_SIGNED_REQUEST));

	// return the parsed facebook credentials
	return credentials;
    }
}
