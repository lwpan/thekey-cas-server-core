package me.thekey.cas.css.phloc.scrubber.filter;

import com.phloc.css.decl.CascadingStyleSheet;

import java.net.URI;

public interface CssFilter {
    void filter(CascadingStyleSheet css, URI cssUri);
}
