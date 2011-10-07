package org.ccci.gto.cas.css.scrubber;

import java.net.URI;

public interface CachingCssScrubber extends CssScrubber {
    public void removeFromCache(final URI uri);
}
