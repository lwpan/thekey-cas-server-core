package org.ccci.gto.cas.federation;

import me.thekey.cas.federation.FederationException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;

public interface FederationProcessor {
    public boolean supports(final Credentials credentials);

    public boolean createIdentity(final Credentials credentials, final Number strength) throws FederationException;

    boolean linkIdentity(GcxUser user, Credentials credentials, Number strength) throws FederationException;
}
