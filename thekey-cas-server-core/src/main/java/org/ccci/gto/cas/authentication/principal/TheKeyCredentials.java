package org.ccci.gto.cas.authentication.principal;

import org.jasig.cas.authentication.principal.Credentials;

/**
 * @author Daniel Frett
 */
public interface TheKeyCredentials extends Credentials {
    /**
     * an enumeration of all the supported locks for TheKeyCredentials
     */
    public enum Lock {
	NULLUSER(0), DEACTIVATED(1), DISABLED(2), LOCKED(3), STALEPASSWORD(4);

	public final int index;

	Lock(final int index) {
	    this.index = index;
	}
    }

    /**
     * indicate whether or not the specified lock should be observed by these
     * credentials
     * 
     * @return a boolean value indicating whether or not to observe the lock
     */
    public boolean observeLock(final Lock lock);
}
