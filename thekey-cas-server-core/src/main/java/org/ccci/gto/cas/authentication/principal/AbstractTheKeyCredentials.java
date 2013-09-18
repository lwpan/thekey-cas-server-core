package org.ccci.gto.cas.authentication.principal;

import java.util.BitSet;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;

public abstract class AbstractTheKeyCredentials implements Credentials, TheKeyCredentials {
    private static final long serialVersionUID = 4703833352612727461L;

    private final BitSet locks = new BitSet();

    private GcxUser gcxUser;

    public AbstractTheKeyCredentials() {
        this(true);
    }

    public AbstractTheKeyCredentials(final boolean observeLocks) {
        // set the default administrative locks to observe
        setObserveLock(Lock.NULLUSER, observeLocks);
        setObserveLock(Lock.LOCKED, observeLocks);
        setObserveLock(Lock.DEACTIVATED, observeLocks);
        setObserveLock(Lock.DISABLED, observeLocks);
        setObserveLock(Lock.STALEPASSWORD, observeLocks);
    }

    public void setObserveLock(final Lock lock, final boolean value) {
        synchronized (locks) {
            locks.set(lock.index, !value);
        }
    }

    @Override
    public boolean observeLock(final Lock lock) {
        synchronized (locks) {
            return !locks.get(lock.index);
        }
    }

    public void setGcxUser(final GcxUser gcxUser) {
        this.gcxUser = gcxUser;
    }

    @Override
    public GcxUser getGcxUser() {
        return gcxUser;
    }
}
