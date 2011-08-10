package org.ccci.gto.cas.authentication.principal;

import java.util.BitSet;

import javax.validation.constraints.NotNull;

import org.jasig.cas.authentication.principal.Credentials;

public class OAuth2Credentials implements Credentials, TheKeyCredentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = -4603224077053631621L;

    private final BitSet locks = new BitSet();

    /** The Opaque authorization code */
    @NotNull
    private String code;

    /** an optional state value */
    private String state;

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
	this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
	return this.code;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
	this.state = state;
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
