package org.ccci.gto.cas.authentication.handler;

import org.jasig.cas.authentication.handler.BlockedCredentialsAuthenticationException;

public class LockedAccountAuthenticationException extends
	BlockedCredentialsAuthenticationException {
    /**
     * Static instance of class to prevent cost incurred by creating new
     * instance.
     */
    public static final LockedAccountAuthenticationException ERROR = new LockedAccountAuthenticationException();

    /** Unique ID for serialization. */
    private static final long serialVersionUID = 5461166534587022478L;

    /** The default code for this exception used for message resolving. */
    // TODO this should be linked from a centralized constants file
    private static final String CODE = "error.account.locked";

    public LockedAccountAuthenticationException() {
	super(CODE);
    }

    public LockedAccountAuthenticationException(final Throwable throwable) {
	super(CODE, throwable);
    }

    public LockedAccountAuthenticationException(final String code) {
	super(code);
    }

    public LockedAccountAuthenticationException(final String code,
	    final Throwable throwable) {
	super(code, throwable);
    }
}
