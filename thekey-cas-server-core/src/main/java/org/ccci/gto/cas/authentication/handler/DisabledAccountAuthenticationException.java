package org.ccci.gto.cas.authentication.handler;

import org.jasig.cas.authentication.handler.BlockedCredentialsAuthenticationException;

public class DisabledAccountAuthenticationException extends
	BlockedCredentialsAuthenticationException {
    /**
     * Static instance of class to prevent cost incurred by creating new
     * instance.
     */
    public static final DisabledAccountAuthenticationException ERROR = new DisabledAccountAuthenticationException();

    /** Unique ID for serialization. */
    private static final long serialVersionUID = -7626284006912590595L;

    /** The default code for this exception used for message resolving. */
    // TODO this should be linked from a centralized constants file
    private static final String CODE = "error.login.disabled";

    public DisabledAccountAuthenticationException() {
	super(CODE);
    }

    public DisabledAccountAuthenticationException(final Throwable throwable) {
	super(CODE, throwable);
    }

    public DisabledAccountAuthenticationException(final String code) {
	super(code);
    }

    public DisabledAccountAuthenticationException(final String code,
	    final Throwable throwable) {
	super(code, throwable);
    }
}
