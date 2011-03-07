package org.ccci.gcx.idm.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.config.XmlConfiguratorException;

/**
 * Utility for working with the redlist (servers that are approved for legacy cas http headers upon login)
 * This service is 
 * @author ken
 *
 */
public class RedListBean 
{
	protected static final Log log = LogFactory.getLog(RedListBean.class);
	
	//represents our configuration file (bean set by DI)
	private XmlConfigurator config;
	public void setConfigurator(XmlConfigurator a_config)
	{
		config = a_config;
		this.reloadServerList();
		
	}
	
	//we store our list of servers that are authorized to receive extended services here...
	private List<String> extendedServices;
	
	
	/**
	 * determines if a particular service is in the extended service list
	 * @param service
	 * @return
	 */
	public boolean isListedService(String service) 
	{
		List<String> ext = getExtendedServices();
		
		if(log.isDebugEnabled()) log.debug("Checking to see if "+service+ " is allowed http response attributes");
		
		boolean retval = false;
		URL url;
		try {
			url = new URL(service);
			if(ext.contains(url.getAuthority()))
				retval = true;
		} catch (MalformedURLException e) {
			//retval already equals false...
		}
		if(log.isDebugEnabled()) log.debug("  and the verdict is: "+retval+" with num servers redlisted = "+ext.size());
		
		return retval;
	}
	
	/**
	 * reload the server list.
	 */
	public void reloadServerList()
	{
		extendedServices=null;
		
		try
		{
			config.refresh();

			List<String> init = config.getListAsString(Constants.CONFIGSERVERELEMENT);
			List<String> temp = new ArrayList<String>();
			
			//expand the list to include some possible variations. This'll save time searching for matches.
			for(String service: init)
			{
				if(service.startsWith("www"))
				{
					temp.add(service);
					temp.add(service.substring(4));
				}
				else
				{
					temp.add(service);
					temp.add("www."+service);
				}
			}	

			extendedServices = temp;
		}
		catch(XmlConfiguratorException e)
		{
			log.error("There was a problem trying to retrieve the servers authorized for extended services. No servers will be authorized.",e);
		}
	}
	
	/**
	 * if the server list isn't initialized, initialize it and then return the list.
	 * @return
	 */
	private List<String> getExtendedServices() {
		if(extendedServices == null)
		{
			this.reloadServerList();
		}
			
		return extendedServices;
	}	
}
