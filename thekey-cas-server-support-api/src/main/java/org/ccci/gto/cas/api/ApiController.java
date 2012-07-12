package org.ccci.gto.cas.api;

import java.util.Map;

import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.restlet.resource.ResourceException;

public interface ApiController {
    public TheKeyRegisteredService lookupServiceByApiKey(final String apiKey);

    public Map<String, Object> getUserAttributes(final TheKeyRegisteredService service, final String guid,
            final String email) throws ResourceException;
}
