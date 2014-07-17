package me.thekey.cas.service;

/**
 * <b>UserAlreadyExistsException</b> is thrown when attempting to create a new
 * user that already exists.
 *
 * @author Greg Crider  Oct 29, 2008  9:48:32 AM
 */
public class UserAlreadyExistsException extends UserException {
    private static final long serialVersionUID = 6854651828515500887L;

    public UserAlreadyExistsException() {
        super();
    }

    /**
     * When only an error message is known.
     *
     * @param message Error message.
     */
    public UserAlreadyExistsException(final String message) {
        super(message);
    }
}
