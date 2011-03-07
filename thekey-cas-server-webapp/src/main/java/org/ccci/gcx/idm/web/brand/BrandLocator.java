package org.ccci.gcx.idm.web.brand;

/**
 * BrandLocator parses the service url string and determines the location
 *  of the brand directory for that url.
 * 
 * @author ken
 *
 */

public interface BrandLocator {
	
	/**
	 * Takes a String containing the service url and uses it to determine the location
	 * of the brand is that we'll view. 
	 * 
	 * @param String - service url 
	 * @return an url to the css of our template
	 */
	public String getBrandLocation(String service_url);
	
}
