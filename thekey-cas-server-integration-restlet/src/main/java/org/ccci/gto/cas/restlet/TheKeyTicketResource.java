package org.ccci.gto.cas.restlet;

import org.ccci.gto.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.util.CredentialsUtil;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.integration.restlet.TicketResource;

public class TheKeyTicketResource extends TicketResource {
    @Override
    protected Credentials obtainCredentials() {
        // use the base TicketResource to retrieve the raw credentials
        final Credentials rawCredentials = super.obtainCredentials();

        // try upgrading the credentials
        final TheKeyCredentials credentials = CredentialsUtil.upgradeCredentials(rawCredentials);

        // return the upgraded credentials
        if (credentials != null) {
            return credentials;
        }

        // default to returning the raw credentials
        return rawCredentials;
    }
}
