package org.ccci.gto.cas.authentication.principal;

import java.util.BitSet;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class TheKeyUsernamePasswordCredentials extends UsernamePasswordCredentials implements TheKeyCredentials {
    private static final long serialVersionUID = -3324348334827807846L;

    private final BitSet locks = new BitSet();

    private GcxUser user;

    public TheKeyUsernamePasswordCredentials() {
        // set the default administrative locks to observe
        setObserveLock(Lock.NULLUSER, true);
        setObserveLock(Lock.LOCKED, true);
        setObserveLock(Lock.DEACTIVATED, true);
        setObserveLock(Lock.DISABLED, true);
        setObserveLock(Lock.STALEPASSWORD, true);
        setObserveLock(Lock.VERIFIED, true);
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
