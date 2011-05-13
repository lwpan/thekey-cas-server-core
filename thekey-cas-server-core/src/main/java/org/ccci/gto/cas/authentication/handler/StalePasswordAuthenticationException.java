package org.ccci.gto.cas.authentication.handler;

import org.ccci.gto.cas.Constants;
import org.jasig.cas.authentication.handler.BlockedCredentialsAuthenticationException;

public class StalePasswordAuthenticationException extends
	BlockedCredentialsAuthenticationException {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = 3708004900256273176L;

    /**
     * Static instance of class to prevent cost incurred by creating new
     * instance.
     */
    public static final StalePasswordAuthenticationException ERROR = new StalePasswordAuthenticationException();

    /** The default code for this exception used for message resolving. */
    private static final String CODE = Constants.ERROR_STALEPASSWORD;

    public StalePasswordAuthenticationException() {
	super(CODE);
    }

    public StalePasswordAuthenticationException(final Throwable throwable) {
	super(CODE, throwable);
    }

    public StalePasswordAuthenticationException(final String code) {
	super(code);
    }

    public StalePasswordAuthenticationException(final String code,
	    final Throwable throwable) {
	super(code, throwable);
    }
}
