package org.ccci.gto.cas.service.audit;

@Deprecated
public class AuditException extends Exception {
    private static final long serialVersionUID = -3243146938365082905L;

    public AuditException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
