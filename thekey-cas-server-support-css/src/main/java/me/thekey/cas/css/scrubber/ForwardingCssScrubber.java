package me.thekey.cas.css.scrubber;

import javax.validation.constraints.NotNull;
import java.net.URI;

public abstract class ForwardingCssScrubber implements CssScrubber {
    @NotNull
    private CssScrubber delegate;

    public void setDelegate(final CssScrubber scrubber) {
        this.delegate = scrubber;
    }

    @Override
    public String scrub(final URI uri) {
        return delegate != null ? delegate.scrub(uri) : "";
    }
}
