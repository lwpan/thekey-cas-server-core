package org.ccci.gto.cas.api;

import java.util.Map;

import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.restlet.resource.ResourceException;

public interface ApiController {
    public final class Identity {
        public final String id;
        public final Double strength;

        public Identity(final String id, final Double strength) {
            this.id = id;
            this.strength = strength;
        }
    }

    public TheKeyRegisteredService lookupServiceByApiKey(final String apiKey);

    public Map<String, Object> getUserAttributes(final TheKeyRegisteredService service, final Map<String, String> query)
            throws ResourceException;

    public Map<String, Identity> getLinkedIdentities(final TheKeyRegisteredService service,
            final Map<String, String> query) throws ResourceException;
}
