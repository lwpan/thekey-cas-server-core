package me.thekey.cas.service;

/**
 * <b>UserNotFoundException</b> is thrown when attempting to authenticate a non-existent user.
 *
 * @author Greg Crider  Nov 10, 2008  4:36:46 PM
 */
public class UserNotFoundException extends UserException {
    private static final long serialVersionUID = -9146320665490790695L;

    /**
     * When only an error message is known.
     *
     * @param message Error message.
     */
    public UserNotFoundException(final String message) {
        super(message);
    }
}
