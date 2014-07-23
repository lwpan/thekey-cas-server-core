package me.thekey.cas.relay.service;

import org.ccci.gcx.idm.core.model.impl.GcxUser;

public interface RelayUserManager {
    /**
     * Locate the user with the specified Relay guid.
     *
     * @param guid the Relay guid being search for
     * @return {@link GcxUser} with the specified Relay guid, or <tt>null</tt> if not found.
     */
    GcxUser findUserByRelayGuid(String guid);

    /**
     * retrieve the Relay GUID for the specified user
     *
     * @param user the user to look the Relay GUID up for
     * @return the user's Relay GUID or null if they don't have one
     */
    String getRelayGuid(GcxUser user);
}
