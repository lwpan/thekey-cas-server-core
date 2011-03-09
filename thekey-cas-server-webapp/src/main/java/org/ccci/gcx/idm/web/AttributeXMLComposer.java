package org.ccci.gcx.idm.web;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.config.XmlConfiguratorException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * composes xml attributes that we'll insert into our validation result. A list of servers 
 * must be provided (via an xml configuration file, as implemented) who are authorized
 * to receive "extended attributes".  attributes common to all can also be returned.
 * @author ken
 *
 */
public class AttributeXMLComposer {

	protected static final Log log = LogFactory.getLog(AttributeXMLComposer.class);
	
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
	 * returns a string representing the <cas:attribute> entities for this user.
	 * @param a_user
	 * @param service
	 * @return
	 */
	public String composeUserAttributes(GcxUser a_user,String service)
		throws Exception
	{
		HashMap<String,String> attributes = new HashMap<String,String>();

		//any extended attributes that everyone gets can go here.
		// KEY = the method on gcxUser to call; VALUE = name we want to call it
		attributes.put("GUID","ssoGuid");
		
		//only authorized services get extended data
		if(isExtendedService(service))
		{
			attributes.put("FirstName","firstName");
			attributes.put("LastName","lastName");
			//attributes.put("DomainsVisitedString","DomainsVisitedString");
			//attributes.put("DomainsVisitedAdditionalString","DomainsVisitedAdditionalString");
			attributes.put("GUIDAdditionalString","GUIDAdditionalString");
		}
		
		return composeUserAttributeFragment(a_user, attributes);
		
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
	 * determines if a particular service is in the extended service list
	 * @param service
	 * @return
	 */
	public  boolean isExtendedService(String service) 
	{
		List<String> ext = getExtendedServices();
		
		if(log.isDebugEnabled()) log.debug("Checking to see if "+service+ " is allowed extended attributes");
		
		boolean retval = false;
		
		URL url;
		try {
			url = new URL(service);
			if(log.isDebugEnabled()) log.debug("Checking for: "+url.getAuthority());
			if(ext.contains(url.getAuthority()))
				retval = true;
		} catch (MalformedURLException e) {
			if(log.isDebugEnabled()) log.debug("Could not create URL from : "+service);
		}
	
		if(log.isDebugEnabled()) log.debug("  and the verdict is: "+retval+" with num servers whitelisted = "+ext.size());
		
		return retval;
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

	/**
	 * Takes a gcxuser and a hashmap of (gcxuser.properties,desired element name) 
	 * and returns xml of "cas:attributes" 
	 * @param a_user - a valid gcxuser
	 * @param a_attrlist - a list of attributes to create
	 * @throws  
	 * @throws SecurityException 
	 */
	private String composeUserAttributeFragment(GcxUser a_user, HashMap<String,String> a_attrlist) throws Exception
	{
		if(log.isDebugEnabled()) log.debug("Composing attributes...");
		Document document = DocumentHelper.createDocument();

		Class userclass = a_user.getClass();
		Element root = document.addElement("attributes");
		root.addNamespace("cas","http://www.yale.edu/tp/cas");
	    Element attributesElement = root.addElement(Constants.CAS_ATTRIBUTE);
		
		for(String attr : a_attrlist.keySet())
		{
			if(log.isDebugEnabled()) log.debug("Composing attribute for: "+attr);
			Method m = userclass.getMethod("get"+attr, null);
			if(m != null)
			{
				String value = (String) m.invoke(a_user, null);
				if(log.isDebugEnabled()) log.debug("  -> found a value:"+value);
			    Element e = attributesElement.addElement(a_attrlist.get(attr));
			    if(StringUtils.isNotBlank(value))
			    	e.addText(value);
			}
			else
			{
				log.warn("Wasn't able to compose a method for attribute: "+attr);
			}
		}
		
		if(log.isDebugEnabled()) log.debug("attributes we built: "+root.asXML());
		
		
		
		Matcher m = Pattern.compile("<attributes xmlns:cas=\"http://www.yale.edu/tp/cas\">(.*)</attributes>").matcher(root.asXML());
		if(!m.find())
		{
	        log.warn("Not sure why but we aren't getting our attributes from: "+root.asXML());
	    }

		if(log.isDebugEnabled()) log.debug("Composed the following masterpiece:"+m.group(1));
		
	    return m.group(1);
		
	}
	
}
