package org.ccci.gto.cas.services;

import org.jasig.cas.services.RegisteredService;

public interface TheKeyRegisteredService extends RegisteredService {
    /**
     * Does this service require legacy headers
     * 
     * @return true if it needs legacy headers, false otherwise.
     */
    boolean isLegacyHeaders();

    /**
     * Does this service require legacy login support
     * 
     * @return true if it needs legacy login support, false otherwise.
     */
    boolean isLegacyLogin();
}
