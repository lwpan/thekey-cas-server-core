package org.ccci.gto.cas.authentication.handler;

import static org.ccci.gto.cas.Constants.ERROR_STALEPASSWORD;
import static org.ccci.gto.cas.Constants.ERROR_TYPE_STALEPASSWORD;

import org.jasig.cas.authentication.handler.AuthenticationException;

public class StalePasswordAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = -2603205570021608229L;

    /**
     * Static instance of class to prevent cost incurred by creating new
     * instance.
     */
    public static final StalePasswordAuthenticationException ERROR = new StalePasswordAuthenticationException();

    public StalePasswordAuthenticationException() {
        super(ERROR_STALEPASSWORD, null, ERROR_TYPE_STALEPASSWORD);
    }
}
