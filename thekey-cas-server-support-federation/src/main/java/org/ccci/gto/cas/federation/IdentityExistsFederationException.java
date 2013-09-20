package org.ccci.gto.cas.federation;

import static org.ccci.gto.cas.federation.Constants.ERROR_IDENTITYEXISTS;

public class IdentityExistsFederationException extends FederationException {
    private static final long serialVersionUID = 910784292512781602L;

    public static final IdentityExistsFederationException ERROR = new IdentityExistsFederationException();

    public IdentityExistsFederationException() {
        super(ERROR_IDENTITYEXISTS);
    }

    public IdentityExistsFederationException(final Object... args) {
        super(ERROR_IDENTITYEXISTS, args);
    }

    public IdentityExistsFederationException(final String code) {
        super(code);
    }

    public IdentityExistsFederationException(final String code, final Object... args) {
        super(code, args);
    }
}
