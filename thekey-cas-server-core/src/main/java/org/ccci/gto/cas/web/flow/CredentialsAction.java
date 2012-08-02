package org.ccci.gto.cas.web.flow;

import org.ccci.gto.cas.authentication.principal.TheKeyCredentials.Lock;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.jasig.cas.authentication.principal.Credentials;

public class CredentialsAction {
    public void initialize(final Credentials credentials) {
        // enable locks for the credentials object
        if (credentials instanceof TheKeyUsernamePasswordCredentials) {
            final TheKeyUsernamePasswordCredentials creds = (TheKeyUsernamePasswordCredentials) credentials;
            creds.setObserveLock(Lock.NULLUSER, true);
            creds.setObserveLock(Lock.LOCKED, true);
            creds.setObserveLock(Lock.DEACTIVATED, true);
            creds.setObserveLock(Lock.DISABLED, true);
            creds.setObserveLock(Lock.STALEPASSWORD, true);
        }
    }
}
