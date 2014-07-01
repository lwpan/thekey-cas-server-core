package me.thekey.cas.util;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public final class CredentialsUtil {
    public static TheKeyCredentials upgradeCredentials(final Credentials rawCredentials) {
        if (rawCredentials instanceof TheKeyCredentials) {
            return (TheKeyCredentials) rawCredentials;
        } else if (rawCredentials instanceof UsernamePasswordCredentials) {
            final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) rawCredentials;
            final TheKeyUsernamePasswordCredentials newCredentials = new TheKeyUsernamePasswordCredentials();
            newCredentials.setUsername(credentials.getUsername());
            newCredentials.setPassword(credentials.getPassword());
            return newCredentials;
        }

        return null;
    }
}
