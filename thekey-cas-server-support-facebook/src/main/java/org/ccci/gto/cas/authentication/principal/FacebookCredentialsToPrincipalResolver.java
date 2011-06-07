package org.ccci.gto.cas.authentication.principal;

import java.util.HashMap;

import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.User;

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
    protected Principal resolveOAuth2Principal(
	    final OAuth2Credentials credentials) {
	// lookup the current facebook user
	final FacebookClient fbClient = new DefaultFacebookClient(
		((FacebookCredentials) credentials).getAccessToken());
	final User user = fbClient.fetchObject("me", User.class,
		Parameter.with("fields", "id,first_name,last_name,email"));

	// store the user attributes that may be required to create a new
	// account
	final HashMap<String, Object> attrs = new HashMap<String, Object>();
	attrs.put("firstName", user.getFirstName());
	attrs.put("lastName", user.getLastName());
	attrs.put("emailAddress", user.getEmail());

	// return a new Principal object
	return new SimplePrincipal(user.getId(), attrs);
    }
}
