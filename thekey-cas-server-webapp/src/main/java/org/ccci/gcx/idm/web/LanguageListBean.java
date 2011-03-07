package org.ccci.gcx.idm.web;

import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * provides the list of languages active in our system. It retrieves this from a 
 * properties file.
 * @author ken
 *
 */
public class LanguageListBean {

	//di
	private String location = Constants.DEFAULT_MESSAGES_LOCATION;
	private HashMap<String,String> languages = null;
	
	protected static final Log log = LogFactory.getLog(LanguageListBean.class);
	
	public HashMap<String,String> getLanguageList() 
	{
		
		if(languages == null)
		{
			reloadConfiguration();
		}
		
		if(log.isDebugEnabled()) log.debug("Returning languages: "+languages.size());
		
		return languages;	
	}
	
	/**
	 * load the languages fresh.
	 */
	public void reloadConfiguration()
	{
		if(log.isDebugEnabled()) log.debug("Reloading language list");
		languages = new HashMap<String,String>();
		try
		{
			Properties p = PropertiesLoaderUtils.loadAllProperties(location);
		
			for(Object key : p.keySet())
			{
				languages.put(key.toString().trim().toLowerCase(), 
						      p.getProperty(key.toString().trim()));
				if(log.isDebugEnabled()) log.debug("Adding language: "+p.getProperty(key.toString()));
			}
		}
		catch(Exception e)
		{
			log.error("Exception trying to load languages.",e);
		}
	
	}
	
	public void setLocation(String a_loc)
	{
		location = a_loc;
		this.reloadConfiguration();
	}
	
}
