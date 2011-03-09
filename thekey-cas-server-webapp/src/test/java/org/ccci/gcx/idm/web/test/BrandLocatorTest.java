package org.ccci.gcx.idm.web.test;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.spring2.test.AbstractTransactionalTestCase;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.brand.impl.ConventionBasedBrandLocatorImpl;
import org.ccci.gcx.idm.web.brand.impl.SimpleBrandLocatorImpl;

import java.util.*;


public class BrandLocatorTest extends AbstractTransactionalTestCase
{
    protected static final Log log = LogFactory.getLog( BrandLocatorTest.class ) ;

    
    public void testSimpleBrandLocator()
    {
       //test brand extractor
    	SimpleBrandLocatorImpl locator = new SimpleBrandLocatorImpl();
    	
    	HashMap<String,String> tests = new HashMap<String, String>();
    	
    	//         SERVICE REQUESTED                          TEMPLATE LOCATION
    	tests.put("http://www.mygcx.org/somelocation/someresrouce.htm", "http://www.mygcx.org");
    	tests.put("https://www.mygcx.org/somelocation/somecommunity","https://www.mygcx.org");
    	tests.put("https://www.mygcx.org:8080/someplace/another","https://www.mygcx.org:8080");
    	tests.put("www.mygcx.org", Constants.DEFAULTSERVICEPROTOCOL+"www.mygcx.org");
    	
    	for (Iterator<String> tIterator = tests.keySet().iterator(); tIterator.hasNext();)
    	{
    		String key = tIterator.next();
    		Assert.assertEquals("testing "+key,tests.get(key),locator.getBrandLocation(key));
    	}
    	
    }
    
    public void testParameterBasedBrandLocator()
    {
       //test brand extractor
    	ConventionBasedBrandLocatorImpl locator = new ConventionBasedBrandLocatorImpl();
    	
    	HashMap<String,String> tests = new HashMap<String, String>();
    	
    	//         SERVICE REQUESTED                          TEMPLATE LOCATION
    	tests.put("http://www.mygcx.org/somelocation/someresrouce.htm", "http://www.mygcx.org/sso/template.css");
    	tests.put("https://www.mygcx.org/somelocation/somecommunity","https://www.mygcx.org/sso/template.css");
    	tests.put("https://www.mygcx.org:8080/someplace/another","https://www.mygcx.org:8080/sso/template.css");
    	tests.put("www.mygcx.org", Constants.DEFAULTSERVICEPROTOCOL+"www.mygcx.org/sso/template.css");

    	
    	for (Iterator<String> tIterator = tests.keySet().iterator(); tIterator.hasNext();)
    	{
    		String key = tIterator.next();
    		Assert.assertEquals("testing "+key,locator.getBrandLocation(key),tests.get(key));
    	}
    	
    }
    
    
}
