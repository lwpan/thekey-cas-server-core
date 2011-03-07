package org.ccci.gcx.idm.web.brand.impl;

import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.brand.BrandLocator;

/**
 * ParameterBasedBrandExtractor - This class pulls the template and service parms
 * out of the request and uses them to determine the location of the brand...
 * 
 * FIRST CHOICE is a "template" parameter which if exists will be returned as the location
 * 
 * SECOND CHOICE is a specific location at the host specified in the "service" parameter.
 * That location is always : http://SERVICEHOST/sso/template.css following the
 *   "convention over configuration" sort of model.
 * 
 * You can specify the actual request parameter to use via Spring, but defaults are assumed.
 * 
 * @author ken
 *
 */
public class ConventionBasedBrandLocatorImpl implements BrandLocator {
	
	//settable via DI
	private String conventionLocation = Constants.DEFAULTCONVENTIONLOCATION;
	
	protected static final Log log = LogFactory.getLog(ConventionBasedBrandLocatorImpl.class);

	public String getBrandLocation(String a_url) 
	{
		if(log.isDebugEnabled()) log.debug("Incoming service url: "+a_url);
		
		String location = null;
		
		//Make sure we start with an actual url with a protocol so that we can turn it into a java url object.
		if(!a_url.startsWith("http://") && !a_url.startsWith("https://") )
		{
			a_url = Constants.DEFAULTSERVICEPROTOCOL +a_url;
		}
		

		try
		{
			URL url = new URL(a_url);
			location = url.getProtocol() + "://" + url.getHost();
			if(url.getPort()>0)
			{
				location += ":" + url.getPort();
			}
			location += this.conventionLocation;

		}
		catch(Exception ee)
		{
			log.warn("Couldn't build a valid template url from the service url. Maybe a service wasn't provided (used"
			+a_url +
			" as our parameter) because of an exception:",ee);
		}
			
		log.debug("Returning location = "+location);
		return location;
		
	}
	

	
	//DI
	//These are optional. we'll default if they're not provided.
	public void setConventionLocation(String a_str)
	{
		this.conventionLocation = a_str;
	}
}
