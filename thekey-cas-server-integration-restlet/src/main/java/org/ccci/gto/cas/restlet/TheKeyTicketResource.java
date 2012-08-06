package org.ccci.gto.cas.restlet;

import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.integration.restlet.TicketResource;

public class TheKeyTicketResource extends TicketResource {
    @Override
    protected Credentials obtainCredentials() {
        // use the base TicketResource to retrieve the raw credentials
        final Credentials rawCredentials = super.obtainCredentials();

        // cast various Credentials types to corresponding TheKeyCredentials
        // type
        if (rawCredentials instanceof UsernamePasswordCredentials) {
            final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) rawCredentials;
            final TheKeyUsernamePasswordCredentials newCredentials = new TheKeyUsernamePasswordCredentials();
            newCredentials.setUsername(credentials.getUsername());
            newCredentials.setPassword(credentials.getPassword());
            return newCredentials;
        }

        return rawCredentials;
    }
}
