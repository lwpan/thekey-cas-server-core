package org.ccci.gcx.idm.web.brand.impl;

import java.util.Properties;
import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.brand.BrandLocator;

/**
 * SimpleBrandExtractorImpl - This class provides brand extraction at its simplest.
 * It merely pulls the hostname out of the service url and returns it.
 * However, it will check a properties list for an alias if the list is provided via spring.
 * @author ken
 *
 */
public class SimpleBrandLocatorImpl implements BrandLocator {

	private Properties aliasList;
	private String defaultBrandLocation;
	
	protected static final Log log = LogFactory.getLog(SimpleBrandLocatorImpl.class);

	public String getBrandLocation(String a_url) 
	{
		log.debug("Incoming service url: "+a_url);
		String brandloc = defaultBrandLocation;
		if(!a_url.startsWith("http"))
		{
			a_url = Constants.DEFAULTSERVICEPROTOCOL + a_url;
		}
		
		try
		{
			URL url = new URL(a_url);
			brandloc = url.getProtocol() + "://" + url.getHost();// + url.getPath();
			if(url.getPort()>0)
			{
				brandloc += ":" + url.getPort();
			}
		}
		catch(MalformedURLException e)
		{
			log.debug("Couldn't parse the service url so we're giving up and going with the defaultBrandLocation (set in spring configuration)");
		}
		
		log.debug("Sending this brand location in unless we find an alias: "+brandloc);
		
		return checkForAlias(brandloc);
	}
	
	/**
	 * checks for alias in the Properties list we have via spring. If an alias exists, return it
	 * in place of the brand location we're given, otherwise, just return the one we're given.
	 * @param a_brandloc
	 * @return
	 */
	private String checkForAlias(String a_brandloc)
	{
		String alias = a_brandloc;
		try
		{
			if(aliasList.containsKey(a_brandloc))
			{
				alias = aliasList.getProperty(a_brandloc);
				log.debug("Found an alias for "+a_brandloc +" = "+alias);

			}
		}
		catch (NullPointerException npe)
		{			
			log.warn("No aliasList found...  You should set an aliasList in SimpleBrandExtractor");
		}
	
		log.debug("checkForAlias is returning: "+alias);
		
		return alias;
	}
	
	
	
	//DI
	public void setAliasLocations(Properties a_list)
	{
		aliasList = a_list;
	}
	
	public void setDefaultBrandLocation(String a_str)
	{
		this.defaultBrandLocation = a_str;
	}
	
}
