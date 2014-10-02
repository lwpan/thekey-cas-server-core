package me.thekey.cas.relay;

import static org.ccci.gto.cas.relay.Constants.ERROR_IDENTITYEXISTS;

import me.thekey.cas.federation.IdentityExistsFederationException;

import java.io.Serializable;

public class RelayIdentityExistsFederationException extends IdentityExistsFederationException {
    private static final long serialVersionUID = -2996769690397894939L;

    public static final RelayIdentityExistsFederationException ERROR = new RelayIdentityExistsFederationException();

    public RelayIdentityExistsFederationException() {
        super(ERROR_IDENTITYEXISTS);
    }

    public RelayIdentityExistsFederationException(final Serializable[] args) {
        super(ERROR_IDENTITYEXISTS, args);
    }
}
