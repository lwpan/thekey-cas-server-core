package org.ccci.gto.persist;

public class DaoException extends Exception {
    private static final long serialVersionUID = -3657668315343649688L;

    public DaoException() {
	super();
    }

    public DaoException(final String message, final Throwable cause) {
	super(message, cause);
    }

    public DaoException(final String message) {
	super(message);
    }

    public DaoException(final Throwable cause) {
	super(cause);
    }
}
