package org.ccci.gto.cas.authentication.principal;

import java.util.BitSet;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class TheKeyUsernamePasswordCredentials extends
	UsernamePasswordCredentials implements TheKeyCredentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = -9122802431823292586L;

    private final BitSet locks = new BitSet();

    private GcxUser gcxUser;

    public TheKeyUsernamePasswordCredentials() {
        // set the default administrative locks to observe
        setObserveLock(Lock.NULLUSER, true);
        setObserveLock(Lock.LOCKED, true);
        setObserveLock(Lock.DEACTIVATED, true);
        setObserveLock(Lock.DISABLED, true);
        setObserveLock(Lock.STALEPASSWORD, true);
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
