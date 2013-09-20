package me.thekey.cas.authentication.handler;

import static me.thekey.cas.Constants.ERROR_TYPE_UNVERIFIED;
import static me.thekey.cas.Constants.ERROR_UNVERIFIED;

import org.jasig.cas.authentication.handler.AuthenticationException;

public class UnverifiedAccountAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = 3652401331409713817L;

    /**
     * Static instance of class to prevent cost incurred by creating new instance.
     */
    public static final UnverifiedAccountAuthenticationException ERROR = new UnverifiedAccountAuthenticationException();

    public UnverifiedAccountAuthenticationException() {
        super(ERROR_UNVERIFIED, null, ERROR_TYPE_UNVERIFIED);
    }
}
