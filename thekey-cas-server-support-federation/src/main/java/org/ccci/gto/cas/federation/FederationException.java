package org.ccci.gto.cas.federation;


public abstract class FederationException extends Exception {
    private static final long serialVersionUID = -4324411778119490111L;

    private final String code;
    private final Object[] args;

    public FederationException(final String code, final Object... args) {
        super();
        this.code = code;
        this.args = args;
    }

    public FederationException(final String code) {
        this(code, new Object[] {});
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
