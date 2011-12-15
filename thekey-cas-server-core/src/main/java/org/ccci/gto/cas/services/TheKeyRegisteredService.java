package org.ccci.gto.cas.services;

import org.jasig.cas.services.RegisteredService;

public interface TheKeyRegisteredService extends RegisteredService {
    /**
     * Does this service require legacy headers
     * 
     * @return true if it needs legacy headers, false otherwise.
     */
    public boolean isLegacyHeaders();

    /**
     * Does this service require legacy login support
     * 
     * @return true if it needs legacy login support, false otherwise.
     */
    public boolean isLegacyLogin();

    /**
     * This method returns the contact email for this registered service
     * 
     * @return the contact email
     */
    public String getContactEmail();

    /**
     * This method returns if there is a custom template css for this registered
     * service
     * 
     * @return the url of the template css to be used with this service
     */
    public String getTemplateCssUrl();
}
