package org.ccci.gto.cas.css.scrubber;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

public class ParsingCachingCssScrubber extends ParsingCssScrubber implements
	CachingCssScrubber {
    private static final String CACHEGROUP = "ParsingCachingCssScrubber";

    @NotNull
    private JCS cache;

    public void setCache(final JCS cache) {
	this.cache = cache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ccci.gto.cas.css.scrubber.ParsingCssScrubber#scrub(java.net.URI)
     */
    @Override
    public String scrub(final URI uri) {
	final String key = this.getCacheKey(uri);

	/* check to see if the requested css is cached */
	final Object cachedCss = this.cache.getFromGroup(key, CACHEGROUP);
	if (cachedCss != null && cachedCss instanceof String) {
	    return (String) cachedCss;
	}

	/* the css isn't cached, so fetch and scrub it */
	final String css = super.scrub(uri);

	/* store the scrubbed css in the cache */
	try {
	    this.cache.putInGroup(key, CACHEGROUP, css);
	} catch (final CacheException e) {
	    /*
	     * not being able to store css in the cache isn't fatal, but log the
	     * error anyways
	     */
	    log.error("Error storing CSS in cache", e);
	}

	/* return the scrubbed css */
	return css;
    }

    public void removeFromCache(final URI uri) {
	final String key = this.getCacheKey(uri);
	this.cache.remove(key, CACHEGROUP);
    }

    /**
     * Returns a key to use for the cache location
     * 
     * @param uri
     * @return
     */
    private String getCacheKey(final URI uri) {
	// for now, lets assume the url itself is a fine key.
	return uri.toString();
    }
}
