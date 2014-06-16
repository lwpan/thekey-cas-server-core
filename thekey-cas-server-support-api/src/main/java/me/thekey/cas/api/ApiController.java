package me.thekey.cas.api;

import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.jasig.cas.authentication.principal.Credentials;
import org.restlet.resource.ResourceException;

import java.util.Map;

public interface ApiController {
    public static final class Identity {
        public final String id;
        public final Double strength;

        public Identity(final String id, final Double strength) {
            this.id = id;
            this.strength = (strength != null ? strength : 0.0);
        }

        @Override
        public String toString() {
            return this.id + "[" + this.strength + "]";
        }
    }

    TheKeyRegisteredService lookupServiceByApiKey(String apiKey);

    Map<String, Object> getUserAttributes(TheKeyRegisteredService service, Map<String,
            String> query) throws ResourceException;

    Map<String, Identity> getLinkedIdentities(TheKeyRegisteredService service, Map<String,
            String> query) throws ResourceException;

    Map<String, Object> federatedLogin(TheKeyRegisteredService service, Credentials credentials) throws
            ResourceException;
}
