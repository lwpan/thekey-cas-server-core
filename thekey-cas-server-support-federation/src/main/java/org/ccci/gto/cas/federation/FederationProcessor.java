package org.ccci.gto.cas.federation;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;

public interface FederationProcessor {
    public boolean supports(final Credentials credentials);

    public boolean linkIdentity(final GcxUser user, final Credentials credentials, final Number strength)
            throws FederationException;

    public boolean createIdentity(final Credentials credentials, final Number strength) throws FederationException;
}
