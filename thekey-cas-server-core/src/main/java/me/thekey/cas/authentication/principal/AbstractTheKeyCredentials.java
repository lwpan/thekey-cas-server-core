package me.thekey.cas.authentication.principal;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;

import java.util.BitSet;

public abstract class AbstractTheKeyCredentials implements Credentials, TheKeyCredentials {
    private static final long serialVersionUID = 4703833352612727461L;

    private final BitSet locks = new BitSet();

    private GcxUser user;

    protected AbstractTheKeyCredentials() {
        this(true);
    }

    protected AbstractTheKeyCredentials(final boolean observeLocks) {
        // set the default administrative locks to observe
        for (final Lock lock : Lock.values()) {
            setObserveLock(lock, observeLocks);
        }
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

    public void setUser(final GcxUser user) {
        this.user = user;
    }

    @Override
    public GcxUser getUser() {
        return this.user;
    }
}
