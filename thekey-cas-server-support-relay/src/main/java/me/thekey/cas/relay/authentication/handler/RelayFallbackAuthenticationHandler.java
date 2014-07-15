package me.thekey.cas.relay.authentication.handler;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.relay.authentication.handler.RelayAuthenticationHandler;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.ccci.gto.cas.federation.authentication.handler.UnknownIdentityAuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;

import javax.validation.constraints.NotNull;

public class RelayFallbackAuthenticationHandler implements AuthenticationHandler {
    @NotNull
    private AuthenticationHandler baseHandler;

    @NotNull
    private RelayAuthenticationHandler relayHandler;

    public void setBaseHandler(final AuthenticationHandler handler) {
        this.baseHandler = handler;
    }

    public void setRelayHandler(final RelayAuthenticationHandler handler) {
        this.relayHandler = handler;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return credentials instanceof TheKeyUsernamePasswordCredentials && this.baseHandler.supports(credentials);
    }

    @Override
    public boolean authenticate(final Credentials rawCredentials) throws AuthenticationException {
        if (rawCredentials instanceof TheKeyUsernamePasswordCredentials) {
            final TheKeyUsernamePasswordCredentials credentials = (TheKeyUsernamePasswordCredentials) rawCredentials;

            // short-circuit if federation is not allowed for these credentials
            if (!credentials.observeLock(TheKeyCredentials.Lock.FEDERATIONALLOWED)) {
                return this.baseHandler.authenticate(credentials);
            }

            // try base handler
            final TheKeyUsernamePasswordCredentials baseCreds = credentials.clone();
            AuthenticationException baseException = null;
            try {
                // quit processing if the credentials authenticated successfully
                if (this.baseHandler.authenticate(baseCreds)) {
                    credentials.setAttributes(baseCreds.getAttributes());
                    return true;
                }
            } catch (final AuthenticationException e) {
                baseException = e;
            }

            // try relay handler, we ignore the Verified lock for federated credentials
            final TheKeyUsernamePasswordCredentials relayCreds = credentials.clone();
            relayCreds.setObserveLock(TheKeyCredentials.Lock.VERIFIED, false);
            relayCreds.setObserveLock(TheKeyCredentials.Lock.STALEPASSWORD, false);
            try {
                if (this.relayHandler.authenticate(relayCreds)) {
                    credentials.setAttributes(relayCreds.getAttributes());
                    return true;
                }
            } catch (final UnknownIdentityAuthenticationException propagated) {
                // propagate the UnknownIdentityAuthenticationException
                credentials.setAttributes(relayCreds.getAttributes());
                throw propagated;
            } catch (final AuthenticationException ignored) {
                // ignore other AuthenticationExceptions
            }

            // return the response for the base handler
            credentials.setAttributes(baseCreds.getAttributes());
            if (baseException != null) {
                throw baseException;
            }
            return false;
        }

        // default to false
        return false;
    }
}
