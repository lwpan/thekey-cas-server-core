package org.ccci.gto.cas.css.scrubber;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

public class ParsingCachingCssScrubber extends ParsingCssScrubber implements
	CachingCssScrubber {
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
	final Object cachedCss = this.cache.get(key);
	if (cachedCss != null && cachedCss instanceof String) {
	    return (String) cachedCss;
	}

	/* the css isn't cached, so fetch and scrub it */
	final String css = super.scrub(uri);

	/* store the scrubbed css in the cache */
	try {
	    this.cache.put(key, css);
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
	try {
	    this.cache.remove(key);
	} catch (final CacheException e) {
	    log.error("error removing item from cache", e);
	}
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
