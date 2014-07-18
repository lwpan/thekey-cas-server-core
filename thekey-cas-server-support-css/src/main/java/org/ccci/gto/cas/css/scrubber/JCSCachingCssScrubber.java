package org.ccci.gto.cas.css.scrubber;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.net.URI;

public class JCSCachingCssScrubber implements CachingCssScrubber {
    private static final Logger LOG = LoggerFactory.getLogger(JCSCachingCssScrubber.class);

    @NotNull
    private JCS cache;

    @NotNull
    private CssScrubber cssScrubber;

    public void setCache(final JCS cache) {
        this.cache = cache;
    }

    public void setCssScrubber(final CssScrubber cssScrubber) {
        this.cssScrubber = cssScrubber;
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

    @Override
    public String scrub(final URI uri) {
        final String key = this.getCacheKey(uri);

        // check to see if the requested css is cached
        final Object cachedCss = this.cache.get(key);
        if (cachedCss != null && cachedCss instanceof String) {
            return (String) cachedCss;
        }

        // the css isn't cached, so fetch and scrub it
        final String css = this.cssScrubber.scrub(uri);

        // store the scrubbed css in the cache
        try {
            this.cache.put(key, css);
        } catch (final CacheException e) {
            // not being able to store css in the cache isn't fatal, but log the error anyways
            LOG.error("Error storing CSS in cache", e);
        }

        // return the scrubbed css
        return css;
    }

    @Override
    public void removeFromCache(final URI uri) {
        final String key = this.getCacheKey(uri);
        try {
            this.cache.remove(key);
        } catch (final CacheException e) {
            LOG.error("error removing item from cache", e);
        }
    }
}
