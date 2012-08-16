package org.ccci.gto.cas.authentication.principal;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;

import java.util.HashMap;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;

public class TheKeyCredentialsToPrincipalResolver implements CredentialsToPrincipalResolver {
    @Override
    public Principal resolvePrincipal(final Credentials rawCredentials) {
        final TheKeyCredentials credentials = (TheKeyCredentials) rawCredentials;
        final GcxUser user = credentials.getGcxUser();

        if (user != null) {
            // generate the attributes for the user authenticating
            final HashMap<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(PRINCIPAL_ATTR_GUID, user.getGUID());
            attrs.put(PRINCIPAL_ATTR_ADDITIONALGUIDS, user.getGUIDAdditional());
            attrs.put(PRINCIPAL_ATTR_EMAIL, user.getEmail());
            attrs.put(PRINCIPAL_ATTR_FACEBOOKID, user.getFacebookId());
            attrs.put(PRINCIPAL_ATTR_FIRSTNAME, user.getFirstName());
            attrs.put(PRINCIPAL_ATTR_LASTNAME, user.getLastName());

            return new SimplePrincipal(user.getEmail(), attrs);
        }

        // no user object, so don't return a principal
        return null;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof TheKeyCredentials;
    }
}