package org.ccci.gto.cas.authentication.handler;

import static org.ccci.gto.cas.Constants.ERROR_FACEBOOKIDALREADYLINKED;

import org.jasig.cas.authentication.handler.BlockedCredentialsAuthenticationException;

public class FacebookIdAlreadyExistsAuthenticationException extends
	BlockedCredentialsAuthenticationException {
    /**
     * Static instance of class to prevent cost incurred by creating new
     * instance.
     */
    public static final FacebookIdAlreadyExistsAuthenticationException ERROR = new FacebookIdAlreadyExistsAuthenticationException();

    /** Unique ID for serialization. */
    private static final long serialVersionUID = 7032923532295964902L;

    public FacebookIdAlreadyExistsAuthenticationException() {
	super(ERROR_FACEBOOKIDALREADYLINKED);
    }

    public FacebookIdAlreadyExistsAuthenticationException(
	    final Throwable throwable) {
	super(ERROR_FACEBOOKIDALREADYLINKED, throwable);
    }

    public FacebookIdAlreadyExistsAuthenticationException(final String code) {
	super(code);
    }

    public FacebookIdAlreadyExistsAuthenticationException(final String code,
	    final Throwable throwable) {
	super(code, throwable);
    }
}
