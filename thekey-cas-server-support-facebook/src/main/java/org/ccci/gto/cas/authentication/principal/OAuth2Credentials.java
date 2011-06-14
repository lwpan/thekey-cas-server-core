package org.ccci.gto.cas.authentication.principal;

import java.util.BitSet;

import org.jasig.cas.authentication.principal.Credentials;

public class OAuth2Credentials implements Credentials, TheKeyCredentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = -4603224077053631621L;

    private final BitSet locks = new BitSet();

    /** The Opaque authorization code */
    private final String code;

    /** an optional state value */
    private final String state;

    public OAuth2Credentials(final String code) {
	this(code, null);
    }

    public OAuth2Credentials(final String code, final String state) {
	this.code = code;
	this.state = state;
    }

    /**
     * @return the code
     */
    public String getCode() {
	return this.code;
    }

    /**
     * @return the state
     */
    public String getState() {
	return this.state;
    }

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
