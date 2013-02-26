package org.ccci.gto.cas.federation.util;

import static org.ccci.gto.cas.federation.Constants.AUTH_ATTR_REQUIREPROXYVALIDATION;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.validation.Assertion;

public final class FederationUtil {
    public static boolean requireProxyValidation(final Assertion assertion) {
        final Authentication authentication = assertion.getChainedAuthentications().get(0);
        final Boolean requireProxyValidation = (Boolean) authentication.getAttributes().get(
                AUTH_ATTR_REQUIREPROXYVALIDATION);
        return requireProxyValidation != null && requireProxyValidation.booleanValue();
    }
}
