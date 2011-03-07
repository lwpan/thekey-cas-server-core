package org.ccci.gcx.idm.core.util;

import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class URLUtil {
	
	protected static final Log log = LogFactory.getLog(URLUtil.class);

	
	/**
	 * decodes an incoming service
	 * @param service
	 * @return
	 */
	/*
	public static String decode(String service) 
	{
		try
		{
			service = URIUtil.decode(service,"UTF-8");

			if(log.isDebugEnabled())
				log.debug("decoded service="+service);
		}
		catch(Exception e)
		{
			log.error("Unable to decode service: "+service+"... returning what we received but that might not be good."+e.getMessage());
		}
		
        return service;
	}
    
	*/

	/**
	 * Extracts a query parameter from an url and returns the value. 
	 * If you need to get more than one parm, you'll be better off
	 * using extractParmsMap and then calling it for each one you want.
	 * It is best you call with a still encoded url so that ampersands in parameter values
	 * don't cause trouble.
	 * @param url
	 * @param parm
	 * @return value of parm or null.
	 */
	public static String extractParm(String url, String parm) 
	{
		
		String ret = null;
		try
		{
			URI uri = new URI(url);
			UrlEncodedQueryString uc = UrlEncodedQueryString.parse(uri);
			ret = uc.get(parm);
		}
		catch(Exception e)
		{
			log.error("Could not extract parameter from url. returning null: "+url+" -- " + e.getMessage());
			ret = null;
		}
        return ret;
        
	}



	
	/**
	 * Returns a Map of name/value pairs for the query string. 
	 * @param url
	 * @return map or null
	 */
	
	/*
	public static Map<String,String> extractParmsMap(String query)
	{
		
		
		if(StringUtils.isEmpty(query)) 
			return null;
		
		if(log.isDebugEnabled()) log.debug("Extracting parms map for: "+query);
		
		Map parmsMap = new LinkedHashMap<String,String>();
        String params[] = query.split("&");

        String lastkey = null;
        
        for (String param : params) 
        {
           String temp[] = param.split("=");
           
           //if we only got a fragment then add that fragment to the last key's data with an
           //  ampersand because we split on one that was part of the text.
           switch(temp.length)
           {
	           case 1: 
	        	   if(temp[0] != null)
	        		   parmsMap.put(lastkey, parmsMap.get(lastkey)+"&"+temp[0]);
	        	   break;
	           
	           case 2:
	        	   if(temp[0] != null)
	        	   {
	        		   parmsMap.put(temp[0],temp[1]);
	        		   lastkey = temp[0];
	        	   }
	        	   break;
	        	   
	           case 3:
	        	   StringBuilder b = new StringBuilder();
	        	   b.append(temp[1]).append("=").append(temp[2]);
	        	   parmsMap.put(temp[0], b.toString());
	        	   break;
	           default:
	        	   parmsMap.put(temp[0],temp[1]);
	        	   lastkey = temp[0];
	        	   break;
           }
           
        }
        log.debug("MAP=="+parmsMap.toString());
        return parmsMap;
        
       
	}
*/

	/**
	 * Encodes a decoded url, even ones with ampersands in their query parms.
	 */

	/*
	public static String encode(String url) 
	{
		if(log.isDebugEnabled()) log.debug("Encoding url: "+url);
		
		
		try {
		
		    URL u_url = new URL(url);
			String query = u_url.getQuery();

		    URI u = new URI(url.substring(0, url.indexOf("?")));
			
			if(log.isDebugEnabled()) 
			{
				log.debug("Authority = "+u.getAuthority());
				log.debug("Query = "+query);
				log.debug("Path = "+u.getPath());
			}
			
			
			Map parmsMap = URLUtil.extractParmsMap(query);
			
			UrlEncodedQueryString uc = UrlEncodedQueryString.create();
			
			for (Iterator it = parmsMap.entrySet().iterator(); it.hasNext();) 
			{
				   Map.Entry entry = (Map.Entry) it.next();
				   uc.append((String)entry.getKey(), (String)entry.getValue());
			}		
			
			url = uc.apply(u).toString();
			
		} catch (Exception e) {
			log.error("Returning url because we could not encode it: "+url);
			e.printStackTrace();
		} 
		
		return url;
		

		
		
	}
	*/
	
}
