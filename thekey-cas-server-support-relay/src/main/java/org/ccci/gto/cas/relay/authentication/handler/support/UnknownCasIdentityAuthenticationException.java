package org.ccci.gto.cas.relay.authentication.handler.support;

import static org.ccci.gto.cas.relay.Constants.ERROR_UNKNOWNIDENTITY;

import org.ccci.gto.cas.federation.authentication.handler.UnknownIdentityAuthenticationException;

public class UnknownCasIdentityAuthenticationException extends UnknownIdentityAuthenticationException {
    private static final long serialVersionUID = -6683106267776109938L;

    public static final UnknownCasIdentityAuthenticationException ERROR = new UnknownCasIdentityAuthenticationException();

    public UnknownCasIdentityAuthenticationException() {
        this(ERROR_UNKNOWNIDENTITY);
    }

    public UnknownCasIdentityAuthenticationException(final String code) {
        super(code);
    }
}
