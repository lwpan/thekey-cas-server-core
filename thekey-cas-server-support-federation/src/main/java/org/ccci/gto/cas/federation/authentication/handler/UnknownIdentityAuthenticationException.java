package org.ccci.gto.cas.federation.authentication.handler;

import static org.ccci.gto.cas.federation.Constants.ERROR_TYPE_UNKNOWNIDENTITY;
import static org.ccci.gto.cas.federation.Constants.ERROR_UNKNOWNIDENTITY;

import org.jasig.cas.authentication.handler.AuthenticationException;

public class UnknownIdentityAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = -410365276195205362L;

    public static final UnknownIdentityAuthenticationException ERROR = new UnknownIdentityAuthenticationException();

    public UnknownIdentityAuthenticationException() {
        this(ERROR_UNKNOWNIDENTITY);
    }

    public UnknownIdentityAuthenticationException(final String code) {
        super(code, null, ERROR_TYPE_UNKNOWNIDENTITY);
    }
}
