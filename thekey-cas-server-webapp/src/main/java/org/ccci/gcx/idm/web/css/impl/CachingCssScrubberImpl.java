package org.ccci.gcx.idm.web.css.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jcs.JCS;

/**
 * provides a caching layer for cssscrubber
 * @author ken
 *
 */
public class CachingCssScrubberImpl extends SimpleCssScrubberImpl 
{
	private JCS cssCache = null;
	private static final String CACHEGROUP = "CSS_SCRUBBER";
	
	protected static final Log log = LogFactory.getLog(CachingCssScrubberImpl.class);

	
	public void setCssCache(JCS a_cache)
	{
		cssCache = a_cache;
	}

	
	public String scrub(String cssUrl) 
	{
		return scrub(cssUrl, false);
	}
	
	
	/**
	 * 
	 * @param cssUrl - css location
	 * @param reload - do we want to reload. Yes = fetch again from server, No = use the cache (if exists)
	 * @return
	 */
	public String scrub(String cssUrl, boolean reload)
	{

		//retrive from cache if exists

		String cacheKey = calculateCacheKey( cssUrl );  
		
		String css = (String) cssCache.getFromGroup(cacheKey, CACHEGROUP);
		
		if(reload)
		{
			if(log.isDebugEnabled()) log.debug("Reloading this cssurl because reloadparm detected.");
			css = null;
		}
		
		//if cache miss
		if(css == null)
		{
			if(log.isDebugEnabled()) log.debug("cssCache MISSED... loading css...");
			//fetch from simplescrubber
			css = super.scrub(cssUrl);
			
			//store if we have something.
			if(css!=null)
			{
				try
				{
					if(log.isDebugEnabled()) log.debug("have some css... putting into cache.");
					cssCache.putInGroup( cacheKey, CACHEGROUP, css );
				}
				catch( Exception e)
				{
					log.warn("Exception while trying to store css in cache: ",e);
				}
			}
		}
		else
		{
			if(log.isDebugEnabled()) log.debug("cssCache HIT... returning css from cache");
		}
		return css;
	}
	
	/**
	 * Returns a key to use for the cache location
	 * 
	 * @param a_str
	 * @return
	 */
	private String calculateCacheKey(String a_str)
	{
		// for now, lets assume the url itself is a fine key.
		return a_str;
	}
	
	
	
}
