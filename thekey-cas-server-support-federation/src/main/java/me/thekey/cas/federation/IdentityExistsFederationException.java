package me.thekey.cas.federation;

import static org.ccci.gto.cas.federation.Constants.ERROR_IDENTITYEXISTS;

import java.io.Serializable;

public class IdentityExistsFederationException extends FederationException {
    private static final long serialVersionUID = -4304065861434442489L;

    public static final IdentityExistsFederationException ERROR = new IdentityExistsFederationException();

    public IdentityExistsFederationException() {
        super(ERROR_IDENTITYEXISTS);
    }

    public IdentityExistsFederationException(final Serializable... args) {
        super(ERROR_IDENTITYEXISTS, args);
    }

    public IdentityExistsFederationException(final String code) {
        super(code);
    }

    public IdentityExistsFederationException(final String code, final Serializable... args) {
        super(code, args);
    }
}
