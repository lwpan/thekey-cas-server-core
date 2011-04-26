package org.ccci.gto.cas.authentication.handler;

import org.jasig.cas.authentication.handler.BlockedCredentialsAuthenticationException;

public class DeactivatedAccountAuthenticationException extends
	BlockedCredentialsAuthenticationException {
    /**
     * Static instance of class to prevent cost incurred by creating new
     * instance.
     */
    public static final DeactivatedAccountAuthenticationException ERROR = new DeactivatedAccountAuthenticationException();

    /** Unique ID for serialization. */
    private static final long serialVersionUID = -9009488994744949771L;

    /** The default code for this exception used for message resolving. */
    // TODO this should be linked from a centralized constants file
    private static final String CODE = "error.account.deactivated";

    public DeactivatedAccountAuthenticationException() {
	super(CODE);
    }

    public DeactivatedAccountAuthenticationException(final Throwable throwable) {
	super(CODE, throwable);
    }

    public DeactivatedAccountAuthenticationException(final String code) {
	super(code);
    }

    public DeactivatedAccountAuthenticationException(final String code,
	    final Throwable throwable) {
	super(code, throwable);
    }
}
