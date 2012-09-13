package org.ccci.gto.cas.services;

import java.util.Set;

import org.jasig.cas.services.RegisteredService;

public interface TheKeyRegisteredService extends RegisteredService {
    /**
     * Is the API enabled for this registered service
     * 
     * @return
     */
    public boolean isApiEnabled();

    /**
     * Is the specified API supported for this registered service
     * 
     * @param name
     *            the name of the API that is being checked
     * @return
     */
    public boolean isApiSupported(final String name);

    /**
     * Returns the set of all the supported APIs for this registered service
     * 
     * @return
     */
    public Set<String> getSupportedApis();

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
     * Is the serviceId a regular expression
     * 
     * @return true if the serviceId is a regular expression
     */
    public boolean isRegex();

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

    /**
     * This method returns the api key for this registered service
     * 
     * @return
     */
    public String getApiKey();
}
