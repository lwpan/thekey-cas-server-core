package org.ccci.gto.cas.federation;

public abstract class FederationException extends Exception {
    private static final long serialVersionUID = -4324411778119490111L;

    private String code;

    public FederationException(final String code) {
        super();
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
