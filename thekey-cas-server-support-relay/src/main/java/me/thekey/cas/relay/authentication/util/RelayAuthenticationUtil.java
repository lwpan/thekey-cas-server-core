package me.thekey.cas.relay.authentication.util;

import static me.thekey.cas.relay.Constants.CREDS_ATTR_CAS_ASSERTION;
import static org.ccci.gto.cas.relay.Constants.ATTR_GUID;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.jasig.cas.client.validation.Assertion;

public class RelayAuthenticationUtil {
    public static Assertion getAssertion(final TheKeyCredentials credentials) {
        return credentials.getAttribute(CREDS_ATTR_CAS_ASSERTION, Assertion.class);
    }

    public static void setAssertion(final TheKeyCredentials credentials, final Assertion assertion) {
        credentials.setAttribute(CREDS_ATTR_CAS_ASSERTION, assertion);
    }

    public static String getRelayGuid(final Assertion assertion) {
        return (String) assertion.getPrincipal().getAttributes().get(ATTR_GUID);
    }
}
