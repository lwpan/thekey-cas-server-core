package org.ccci.gcx.idm.web.config;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * XmlConfigurator - provides an xml configuration utility.
 * once you use "setLocation(filename)" to provide the xml file location 
 * you can then call the various "getListAs____(elementname)" methods
 * to retrieve the particular list or node items you desire.
 * @author ken
 *
 */
public class XmlConfigurator 
{
	private String location;
	private Document document;
	protected static final Log log = LogFactory.getLog(XmlConfigurator.class);

	
	public XmlConfigurator()
	{
		//jesus rules!
	}
	
	public XmlConfigurator(String a_loc)
	{
		location = a_loc;
	}
	
	/**
	 * the xml filename location.
	 * @return
	 */
	public String getLocation()
	{
		return location;
	}
	
	/**
	 * sets location without attempting to load the document
	 * @param a_loc
	 * @return
	 */
	public void setLocation(String a_loc)
	{
		location = a_loc;
		document=null;
	}
	
	/**
	 * sets the location and attempts to load and parse the xml document.
	 * @param a_loc
	 * @throws MalformedURLException if location is invalid
	 * @throws DocumentException if the xml file is invalid
	 */
	public void setAndParseLocation(String a_loc) throws XmlConfiguratorException
	{
		location = a_loc;
		document=null;
		parseLocation();
	}
	
	/**
	 * parses the file location in our location property and sets the document property
	 * @throws XmlConfiguratorException
	 */
	private void parseLocation() throws XmlConfiguratorException
	{
		SAXReader reader = new SAXReader();
		try{
			if(log.isDebugEnabled()) log.debug("attempting to parse document: "+location);
			URL fileurl = ResourceUtils.getURL(location);
			document = reader.read(fileurl);
			if(log.isDebugEnabled()) log.debug("parse succeeded.");
		}
		catch(DocumentException e)
		{
			log.warn("Could not create Document from location given: "+location,e);
			throw new XmlConfiguratorException(e);
		}
		catch(FileNotFoundException e)
		{
			log.warn("Could not find file from location given: "+location,e);
			throw new XmlConfiguratorException(e);
		}
	}
	
	/**
	 * call to reload the xml file and reparse the document.
	 * @throws XmlConfiguratorException
	 */
	public void refresh() throws XmlConfiguratorException
	{
		if(log.isDebugEnabled()) log.debug("Refreshing configuration document: "+location);
		document=null;
		parseLocation();
	}
	
	/** 
	 * returns the children of the given element as a list of strings.
	 * This works with one element <something>hi</something>
	 * or with multiple <something> elements.  It'll return however many it finds.
	 * @param element
	 * @return arraylist (empty or with elements)
	 * @throws XmlConfiguratorException
	 */
	public List<String> getListAsString(String element) throws XmlConfiguratorException 
	{

		List<Element> elts = getListAsElement(element);

		ArrayList<String> children = new ArrayList<String>();
		
		for(Element e : elts)
		{
			children.add(e.getText());
			if(log.isDebugEnabled()) log.debug("added string to '"+element+"' list: "+e.getText());
		}
		
		return children;
		
	}
	
	/**
	 * returns the children of the given element as a list of elements.
	 * @param element
	 * @return
	 * @throws XmlConfiguratorException
	 */
	public List<Element> getListAsElement(String element) throws XmlConfiguratorException
	{
		return getDocument().getRootElement().elements(element);
	}
	
	/**
	 * returns the text value of an element with the given name
	 * @param element
	 * @return "" or "value"
	 * @throws XmlConfigurationException
	 */
	public String getElementValue(String element) throws XmlConfiguratorException
	{
		if(log.isDebugEnabled()) log.debug("getting element value for "+element);
		Document doc = getDocument();
		String retval = "";
		
		if(doc != null)
		{
			Node n = doc.getRootElement().element(element);
			if(n != null)
			{
				retval = n.getText();
			}
		}
		if(log.isDebugEnabled()) log.debug("element "+element+" value = "+retval);
		return retval;
	}
	
	/**
	 * returns the document, parsing the location if not already parsed.
	 * @return
	 * @throws XmlConfiguratorException
	 */
	private Document getDocument() throws XmlConfiguratorException
	{
		//if they've set the location but not parsed it, parse it.
		if(document==null && !StringUtils.isEmpty(location))
		{
			parseLocation();
		}
			
		if(document==null)
			throw new XmlConfiguratorException(new Exception("System Error: XML Configuration is invalid. This is a server error."));
		
		return document;
	}
	
}
