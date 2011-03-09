package org.ccci.gcx.idm.web.test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.util.URLUtil;
import org.springframework.test.ConditionalTestCase;



public class EncodeDecodeTest extends ConditionalTestCase
{
    protected static final Log log = LogFactory.getLog( HttpClientTest.class ) ;

	public void testEncodeDecode() throws AuthenticationException, URISyntaxException, UnsupportedEncodingException, MalformedURLException
	{
	
		String orig_service="http://search.mygcx.org/search?q=search+%26+test&ticket=ABC123";

	    String d_service = URLDecoder.decode(orig_service,"UTF-8");
	    
	    log.debug("SO now the service == "+d_service);
	   
	    String ticket = URLUtil.extractParm(orig_service, "ticket");
	    
	    log.debug("Ticket="+ticket);
	    
	    Assert.assertNotNull(ticket);
	   
	    
	    
	    
	    
	    
	    
	    /*
	    URL u = new URL(d_service);
		if(log.isDebugEnabled()) 
		{
			log.debug("Authority = "+u.getAuthority());
			log.debug("Query = "+u.getQuery());
			log.debug("Path = "+u.getPath());
			log.debug("Protocol = "+u.getProtocol());
		}
		
		log.debug("QUERY: "+u.getQuery());
		
		String query = u.getQuery();
		
		HashMap parmsMap = new HashMap<String,String>();
        String params[] = query.split("&");

        String lastkey = null;
        
        for (String param : params) {
           String temp[] = param.split("=");
           
           //if we only got a fragment then add that fragment to the last key's data with an
           //  ampersand because we split on one that was part of the text.
           if(temp.length == 1)
           {
        	   parmsMap.put(lastkey, parmsMap.get(lastkey)+"&"+temp[0]);
           }
           else
           {
        	   parmsMap.put(temp[0],temp[1]);
        	   lastkey = temp[0];
           }
           
        }
        log.debug("MAP=="+parmsMap.toString());
		
		
		/*
		 * String d_query = URLDecoder.decode(u.getQuery(),"UTF-8");
		 
		log.debug("DECODED QUERY: "+d_query);
		
		String e_query = URLEncoder.encode(d_query, "UTF-8");
		
		log.debug("ENCODED QUERY: "+e_query);
		
		String query = (StringUtils.isNotEmpty(e_query)) ? 
        		"?" + e_query :
        		"";
		

		
		String ret = u.getProtocol() +"://"+ u.getAuthority() + u.getPath() + query;

	    
	    //String e_service = URLEncoder.encode(d_service,"UTF-8");
	    
	    log.debug("ENCODED and recombined service == "+ ret);
	    
	    //String lastret = URLUtil.encode(d_service);
	    
	    //Assert.assertTrue(orig_service.equals(ret));
*/	    
	}
}
