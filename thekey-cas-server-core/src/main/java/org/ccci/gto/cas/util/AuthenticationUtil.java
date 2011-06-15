package org.ccci.gto.cas.util;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_KEYUSER;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.MutableAuthentication;

public final class AuthenticationUtil {
    /**
     * This method guarantees the specified Authentication object has mutable
     * attributes
     * 
     * @param authentication
     * @return an Authentication object with mutable attributes
     */
    public static Authentication makeMutable(final Authentication authentication) {
	// don't do anything if authentication is already mutable
	if (authentication instanceof MutableAuthentication) {
	    return authentication;
	}

	// create a new MutableAuthentication object
	final Authentication newAuth = new MutableAuthentication(
		authentication.getPrincipal(),
		authentication.getAuthenticatedDate());
	newAuth.getAttributes().putAll(authentication.getAttributes());

	// return the new MutableAuthentication object
	return newAuth;
    }

    /**
     * @param user
     *            the user to set in the Authentication object
     */
    public static void setUser(final Authentication authentication,
	    final GcxUser user) {
	authentication.getAttributes().put(AUTH_ATTR_KEYUSER, user);
    }

    /**
     * @return the user currently stored in the Authentication object
     */
    public static final GcxUser getUser(final Authentication authentication) {
	return (GcxUser) authentication.getAttributes().get(AUTH_ATTR_KEYUSER);
    }
}
