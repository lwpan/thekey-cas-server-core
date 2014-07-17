package me.thekey.cas.service;

/**
 * <b>UserException</b> is thrown whenever an error occurs with a GUID or E-mail address. This is primarily
 * a constraint violation when the same GUID/E-mail value is attempted to be used for two different
 * users.
 *
 * @author Greg Crider  Oct 22, 2008  6:12:37 PM
 */
public class UserException extends Exception {
    private static final long serialVersionUID = -832703657676934413L;

    public UserException() {
        super();
    }

    /**
     * When only an error message is known.
     *
     * @param message Error message.
     */
    public UserException(final String message) {
        super(message);
    }

    /**
     * When caused by an underlying {@link Throwable}.
     *
     * @param message Error message.
     * @param cause   Underlying {@link Throwable}.
     */
    public UserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
