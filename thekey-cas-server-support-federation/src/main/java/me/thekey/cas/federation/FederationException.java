package me.thekey.cas.federation;

import java.io.Serializable;

public abstract class FederationException extends Exception {
    private static final long serialVersionUID = -8938640306068240138L;

    private final String code;
    private final Serializable[] args;

    public FederationException(final String code, final Serializable... args) {
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

    public Serializable[] getArgs() {
        return this.args;
    }
}
