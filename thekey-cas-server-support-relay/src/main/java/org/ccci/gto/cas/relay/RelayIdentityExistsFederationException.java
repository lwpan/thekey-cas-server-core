package org.ccci.gto.cas.relay;

import static org.ccci.gto.cas.relay.Constants.ERROR_IDENTITYEXISTS;

import org.ccci.gto.cas.federation.IdentityExistsFederationException;

public class RelayIdentityExistsFederationException extends IdentityExistsFederationException {
    private static final long serialVersionUID = -7429857006177821968L;

    public static final RelayIdentityExistsFederationException ERROR = new RelayIdentityExistsFederationException();

    public RelayIdentityExistsFederationException() {
        super(ERROR_IDENTITYEXISTS);
    }

    public RelayIdentityExistsFederationException(final Object[] args) {
        super(ERROR_IDENTITYEXISTS, args);
    }
}
