package org.ccci.gto.cas.authentication;

import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationMetaDataPopulator;
import org.jasig.cas.authentication.principal.Credentials;

public final class TheKeyUserAuthenticationMetaDataPopulator implements AuthenticationMetaDataPopulator {
    @Override
    public Authentication populateAttributes(Authentication authentication, final Credentials credentials) {
        // only process if the provided credentials are supported
        if (this.supports(credentials)) {
            // make sure the authentication object is mutable
            authentication = AuthenticationUtil.makeMutable(authentication);

            // lookup and store the user in the Authentication response
            AuthenticationUtil.setUser(authentication, ((TheKeyCredentials) credentials).getUser());
        }

        // return the authentication object
        return authentication;
    }

    /**
     * @return true if the credentials are not null and the credentials class is
     *         an implementation of TheKeyCredentials.
     */
    public final boolean supports(final Credentials credentials) {
        return credentials instanceof TheKeyCredentials;
    }
}
