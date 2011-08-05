package org.ccci.gto.cas.authentication.principal;

import static org.ccci.gto.cas.facebook.Constants.PRINCIPAL_ATTR_ACCESSTOKEN;

import java.util.HashMap;

import org.ccci.gto.cas.facebook.util.FacebookUtils;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;

import com.restfb.json.JsonObject;

public class FacebookCredentialsToPrincipalResolver extends
	OAuth2CredentialsToPrincipalResolver {

    public FacebookCredentialsToPrincipalResolver() {
	super(FacebookCredentials.class, false);
    }

    public FacebookCredentialsToPrincipalResolver(
	    final Class<? extends FacebookCredentials> classToSupport,
	    final boolean supportSubClasses) {
	super(classToSupport, supportSubClasses);
    }

    @Override
    protected Principal resolveOAuth2Principal(final OAuth2Credentials rawCreds) {
	final FacebookCredentials credentials = (FacebookCredentials) rawCreds;
	final JsonObject data = FacebookUtils.getSignedData(credentials
		.getSignedRequest());

	// store the access token that may be required to create a new account
	final HashMap<String, Object> attrs = new HashMap<String, Object>();
	attrs.put(PRINCIPAL_ATTR_ACCESSTOKEN, credentials.getAccessToken());

	// return a new Principal object
	return new SimplePrincipal(data.getString("user_id"), attrs);
    }
}
