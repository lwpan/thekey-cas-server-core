package me.thekey.cas.authentication.principal;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;

/**
 * @author Daniel Frett
 */
public interface TheKeyCredentials extends Credentials {
    /**
     * an enumeration of all the supported locks for TheKeyCredentials
     */
    public enum Lock {
        /** there was no account found for the current credentials */
        NULLUSER(0),
        /** The account was deactivated using the admin console */
        DEACTIVATED(1),
        /** Login for this account has been disabled */
        DISABLED(2),
        /** The account is locked from too many failed login attempts */
        LOCKED(3),
        /** The password on the account is stale and needs to be changed */
        STALEPASSWORD(4),
        /** the email address on the account has not been verified yet */
        VERIFIED(5),
        /** these credentials can be used for federated authentication */
        FEDERATIONALLOWED(6);

	public final int index;

        private Lock(final int index) {
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

    /**
     * @return the GcxUser object associated with these credentials
     */
    public GcxUser getUser();
}
