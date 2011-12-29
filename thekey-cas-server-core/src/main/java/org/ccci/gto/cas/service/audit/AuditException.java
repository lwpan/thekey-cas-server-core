package org.ccci.gto.cas.service.audit;

public class AuditException extends Exception {
    private static final long serialVersionUID = -5926466462227675667L;

    public AuditException() {
	super();
    }

    public AuditException(String message) {
	super(message);
    }

    public AuditException(Throwable cause) {
	super(cause);
    }

    public AuditException(final String message, Throwable cause) {
	super(message, cause);
    }
}
