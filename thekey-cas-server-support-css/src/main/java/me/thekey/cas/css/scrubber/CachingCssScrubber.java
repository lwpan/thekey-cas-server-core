package me.thekey.cas.css.scrubber;

import java.net.URI;

public interface CachingCssScrubber extends CssScrubber {
    void removeFromCache(URI uri);
}
