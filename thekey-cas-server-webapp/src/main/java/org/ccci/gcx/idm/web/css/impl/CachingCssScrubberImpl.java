package org.ccci.gcx.idm.web.css.impl;

import java.net.URI;

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

	
    public String scrub(final URI uri) {
	return scrub(uri, false);
    }
	
	
    /**
     * @param uri
     *            - css location
     * @param reload
     *            - do we want to reload. Yes = fetch again from server, No =
     *            use the cache (if exists)
     * @return
     */
    public String scrub(final URI uri, boolean reload) {
		//retrive from cache if exists

	String cacheKey = calculateCacheKey(uri);
		
		String css = (String) cssCache.getFromGroup(cacheKey, CACHEGROUP);
		
		if(reload)
		{
	    log.debug("Reloading this cssurl because reloadparm detected.");
			css = null;
		}
		
		//if cache miss
		if(css == null)
		{
	    log.debug("cssCache MISSED... loading css...");
			//fetch from simplescrubber
	    css = super.scrub(uri);
			
			//store if we have something.
			if(css!=null)
			{
				try
				{
		    log.debug("have some css... putting into cache.");
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
	    log.debug("cssCache HIT... returning css from cache");
		}
		return css;
	}

    /**
     * Returns a key to use for the cache location
     * 
     * @param uri
     * @return
     */
    private String calculateCacheKey(final URI uri) {
	// for now, lets assume the url itself is a fine key.
	return uri.toString();
    }
}
