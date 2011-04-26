package org.ccci.gcx.idm.core;

/**
 * provides an exception class for authentication errors.
 * 
 * @author Ken Burcham, Daniel Frett
 */
public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
	super();
    }

    public AuthenticationException(String message) {
	super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
	super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
	super(cause);
    }
}
