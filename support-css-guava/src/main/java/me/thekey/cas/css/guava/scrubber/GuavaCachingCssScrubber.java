package me.thekey.cas.css.guava.scrubber;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import me.thekey.cas.css.scrubber.CachingCssScrubber;
import me.thekey.cas.css.scrubber.ForwardingCssScrubber;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.concurrent.ExecutionException;

public class GuavaCachingCssScrubber extends ForwardingCssScrubber implements CachingCssScrubber {
    @NotNull
    private LoadingCache<URI, String> cache;

    public void setCacheSpec(final String spec) {
        this.cache = CacheBuilder.from(spec).weigher(new CssWeigher()).build(new CssCacheLoader());
    }

    @Override
    public String scrub(final URI uri) {
        try {
            return this.cache.get(uri);
        } catch (final ExecutionException e) {
            return "";
        }
    }

    @Override
    public void removeFromCache(final URI uri) {
        this.cache.invalidate(uri);
    }

    private class CssCacheLoader extends CacheLoader<URI, String> {
        @Override
        public String load(final URI uri) throws Exception {
            return GuavaCachingCssScrubber.super.scrub(uri);
        }
    }

    private static class CssWeigher implements Weigher<URI, String> {
        @Override
        public int weigh(final URI uri, final String css) {
            return css.length();
        }
    }
}
