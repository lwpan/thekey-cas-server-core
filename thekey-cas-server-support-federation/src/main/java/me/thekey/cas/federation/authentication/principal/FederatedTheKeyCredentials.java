package me.thekey.cas.federation.authentication.principal;

import me.thekey.cas.authentication.principal.AbstractTheKeyCredentials;

public abstract class FederatedTheKeyCredentials extends AbstractTheKeyCredentials {
    private static final long serialVersionUID = 5240880181838968226L;

    protected FederatedTheKeyCredentials() {
        this(true);
    }

    protected FederatedTheKeyCredentials(final boolean observeLocks) {
        super(observeLocks);

        // These are federated credentials, so we always allow federation
        this.setObserveLock(Lock.FEDERATIONALLOWED, true);

        // we never care about stale passwords or unverified accounts
        this.setObserveLock(Lock.STALEPASSWORD, false);
        this.setObserveLock(Lock.VERIFIED, false);
    }
}
