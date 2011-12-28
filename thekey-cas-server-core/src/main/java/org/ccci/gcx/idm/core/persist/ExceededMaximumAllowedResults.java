package org.ccci.gcx.idm.core.persist;

import org.ccci.gto.persist.DaoException;

/**
 * {@link ExceededMaximumAllowedResults} is thrown whenever a search exceeds the
 * maximum allowed results.
 */
public class ExceededMaximumAllowedResults extends DaoException {
    private static final long serialVersionUID = -4906869593643904120L ;

    public ExceededMaximumAllowedResults() {
	super();
    }

    public ExceededMaximumAllowedResults(final String message,
	    final Throwable cause) {
	super(message, cause);
    }

    public ExceededMaximumAllowedResults(final String message) {
	super(message);
    }

    public ExceededMaximumAllowedResults(final Throwable cause) {
	super(cause);
    }
}
