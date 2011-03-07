package org.ccci.gcx.idm.core.util;


import java.net.URLDecoder;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.ConditionalTestCase;

//import org.ccci.gcx.idm.core.util.URLUtil;


public class EncodeDecodeTest extends ConditionalTestCase
{
    protected static final Log log = LogFactory.getLog( EncodeDecodeTest.class ) ;

	public void testEncodeDecode() throws Exception
	{
		
		//well, we don't encode anymore...
	
		String orig_service="http://search.mygcx.org/search?q=search+%26+test&ticket=ABC123&b=abc&e=xyz&r=ken";

	    String d_service = URLDecoder.decode(orig_service,"UTF-8");
	    
	    Assert.assertNotNull(d_service);
	    
	    String ticket = URLUtil.extractParm(orig_service, "ticket");
	    
	    log.debug("ticket == "+ticket);
	   
	    Assert.assertNotNull(ticket);
	    
	    
	    /*
	    
	    //mantis test
	    orig_service = "https://www.globalopsccci.org/mantis/login.php?return=/mantis/view.php?id=255&ticket=ST-467-fAW2esreDAeJ94jGCn4Y-cas";
	    d_service = URLDecoder.decode(orig_service,"UTF-8");
	    log.debug("SO now the service == "+d_service);
	    encoded = orig_service;	    
	    log.debug("Encoded = "+encoded);
	    
	    Assert.assertTrue("/mantis/view.php?id=255".equals(URLUtil.extractParm(orig_service, "return")));
	    
	    
	    //mantis test
	    orig_service = "https://www.globalopsccci.org/mantis/login.php?return=/mantis/view.php?id=255&search=ABC123&ticket=ST-467-fAW2esreDAeJ94jGCn4Y-cas";
	    d_service = URLDecoder.decode(orig_service,"UTF-8");
	    log.debug("SO now the service == "+d_service);
	    encoded = orig_service;	    
	    log.debug("Encoded = "+encoded);
	    
	    Assert.assertTrue("/mantis/view.php?id=255".equals(URLUtil.extractParm(orig_service, "return")));
	   
	    
		*/
		
		/*
		
		String service = "http://search.mygcx.org/search?q=search+%26+test";
		String ticket =  "ABC123";
		String correct_location = service + "&ticket="+ticket;
		
		String location = URLUtil.addParmToURL(service, "ticket", ticket);
		
		log.debug("location == "+ location);
		
		Assert.assertTrue(correct_location.equals(location));
		
		
		
		service = "http://search.mygcx.org/search";
		correct_location = service + "?ticket="+ticket;
		
		location = URLUtil.addParmToURL(service, "ticket", ticket);
		Assert.assertTrue(correct_location.equals(location));
		
		
		
	    
		
		
		/*
		String d_service = URLUtil.decode(orig_service);
	    
	    log.debug("decoded service 1 == "+d_service);
		    
	    String e_service = URLUtil.encode(d_service);
	    log.debug("encode 1: "+e_service);
		    
	    
	    String d2_service = d_service;
	    String d3_service = URLUtil.decode(e_service);
	    
	    log.debug("Decoded Encode: == "+d3_service);
	    
	    Assert.assertTrue(d2_service.equals(d3_service));
	    */
	    
	    
	    
	    /*
	    URL u = new URL(orig_service);
		if(log.isDebugEnabled()) 
		{
			log.debug("Authority = "+u.getAuthority());
			log.debug("Query = "+u.getQuery());
			log.debug("Path = "+u.getPath());
			log.debug("Protocol = "+u.getProtocol());
		}
		
		log.debug("QUERY: "+u.getQuery());
		
		String d_query = URLDecoder.decode(u.getQuery(),"UTF-8");
		log.debug("DECODED QUERY: "+d_query);
		
		String e_query = URLEncoder.encode(d_query, "UTF-8");
		
		log.debug("ENCODED QUERY: "+e_query);
		
		String query = (StringUtils.isNotEmpty(e_query)) ? 
        		"?" + e_query :
        		"";
		

		
		String ret = u.getProtocol() +"://"+ u.getAuthority() + u.getPath() + query;

	    
	    //String e_service = URLEncoder.encode(d_service,"UTF-8");
	    
	    log.debug("ENCODED and recombined service == "+ ret);
	    
	    String lastret = URLUtil.encode(d_service);
	    
	    Assert.assertTrue(orig_service.equals(ret));
	    */
	}
}

