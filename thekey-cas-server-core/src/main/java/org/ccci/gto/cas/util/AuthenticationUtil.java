package org.ccci.gto.cas.util;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_KEYUSER;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.Authentication;

public final class AuthenticationUtil {
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
