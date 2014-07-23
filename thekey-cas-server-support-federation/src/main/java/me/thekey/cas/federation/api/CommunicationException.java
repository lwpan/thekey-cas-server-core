package me.thekey.cas.federation.api;

public class CommunicationException extends Exception {
    private static final long serialVersionUID = -2501398851776426248L;

    public CommunicationException() {
    }

    public CommunicationException(final Throwable cause) {
        super(cause);
    }
}
