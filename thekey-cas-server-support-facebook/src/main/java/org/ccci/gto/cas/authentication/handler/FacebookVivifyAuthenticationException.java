package org.ccci.gto.cas.authentication.handler;

import static org.ccci.gto.cas.facebook.Constants.ERROR_FACEBOOKVIVIFYERROR;

import org.jasig.cas.authentication.handler.UncategorizedAuthenticationException;

public class FacebookVivifyAuthenticationException extends
	UncategorizedAuthenticationException {
    private static final long serialVersionUID = -1186059216258558213L;

    /**
     * Static instance of class to prevent cost incurred by creating new
     * instance.
     */
    public static final FacebookVivifyAuthenticationException ERROR = new FacebookVivifyAuthenticationException();

    public FacebookVivifyAuthenticationException() {
	super(ERROR_FACEBOOKVIVIFYERROR);
    }

    public FacebookVivifyAuthenticationException(final Throwable throwable) {
	super(ERROR_FACEBOOKVIVIFYERROR, throwable);
    }

    public FacebookVivifyAuthenticationException(final String code) {
	super(code);
    }

    public FacebookVivifyAuthenticationException(final String code,
	    final Throwable throwable) {
	super(code, throwable);
    }
}
