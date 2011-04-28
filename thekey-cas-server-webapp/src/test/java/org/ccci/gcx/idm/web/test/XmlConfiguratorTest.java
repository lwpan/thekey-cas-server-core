package org.ccci.gcx.idm.web.test;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.config.XmlConfiguratorException;
import org.springframework.test.ConditionalTestCase;



public class XmlConfiguratorTest extends ConditionalTestCase
{
    protected static final Log log = LogFactory.getLog( XmlConfiguratorTest.class ) ;

    
    public void testXmlConfigurator() throws XmlConfiguratorException
    {
       //test brand extractor
    	XmlConfigurator config = new XmlConfigurator("nowhere.xml");

    	boolean exc = false;
    	
    	try
    	{
    		config.getListAsString("server");
    		
    	}
    	catch(XmlConfiguratorException e)
    	{
    		exc = true;
    	}
    	
    	Assert.assertTrue("Should have failed when couldn't get an xml file",exc);
    
	config.setAndParseLocation("src/main/webapp/WEB-INF/classes/config/whitelist.xml");
    	
    	List<String>servers = config.getListAsString("server");
    	Assert.assertTrue("Didn't receive a list of servers.", servers.size()>0);
    	
    	List<String>nothing = config.getListAsString("nothingbaby");
    	
    	
    	Assert.assertFalse("Shouldn't have been any of those returned.", nothing.size()>0);
    	
	config.setAndParseLocation("src/main/webapp/WEB-INF/classes/config/passwords.xml");
    	
    	Assert.assertFalse("No haveMinMix value found", config.getElementValue("haveMinMix").equals(""));
		
    	List<String>blacklist = config.getListAsString(Constants.CONFIGPASSWORD_BLACKLIST);
		Assert.assertTrue("Didn't get our blacklist back",blacklist.size()>0);
    	
    	
    	
    	for(String s : servers)
    	{
    		System.out.println(s);
    		Assert.assertNotNull("Didn't get a string", s);
    	}
    }
}
