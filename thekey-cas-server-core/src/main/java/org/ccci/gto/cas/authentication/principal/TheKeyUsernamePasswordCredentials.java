package org.ccci.gto.cas.authentication.principal;

import java.util.BitSet;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class TheKeyUsernamePasswordCredentials extends
	UsernamePasswordCredentials implements TheKeyCredentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = -9122802431823292586L;

    private final BitSet locks = new BitSet();

    public void setObserveLock(final Lock lock, final boolean value) {
	synchronized (locks) {
	    locks.set(lock.index, !value);
	}
    }

    public boolean observeLock(final Lock lock) {
	synchronized (locks) {
	    return !locks.get(lock.index);
	}
    }
}
