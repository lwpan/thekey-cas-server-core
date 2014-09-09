package me.thekey.cas.css.phloc.scrubber;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.resource.URLResource;
import com.phloc.css.ECSSVersion;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.reader.CSSReader;
import com.phloc.css.writer.CSSWriter;
import com.phloc.css.writer.CSSWriterSettings;
import me.thekey.cas.css.phloc.scrubber.filter.CssFilter;
import me.thekey.cas.css.scrubber.CssScrubber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public class PhlocCssScrubber implements CssScrubber {
    private static final Logger LOG = LoggerFactory.getLogger(PhlocCssScrubber.class);

    private final ArrayList<CssFilter> filters = new ArrayList<>();

    public void addFilter(final CssFilter filter) {
        this.filters.add(filter);
    }

    public void setFilters(final Collection<? extends CssFilter> filters) {
        this.filters.clear();
        if (filters != null) {
            this.filters.addAll(filters);
        }
    }

    protected CascadingStyleSheet parse(final URI uri) throws MalformedURLException {
        return CSSReader.readFromStream(new URLResource(uri), CCharset.CHARSET_UTF_8_OBJ, ECSSVersion.CSS30);
    }

    @Override
    public String scrub(final URI uri) {
        try {
            // parse CSS
            final CascadingStyleSheet css = this.parse(uri);

            // process any defined css filters
            if (!filters.isEmpty()) {
                for (final CssFilter filter : filters) {
                    filter.filter(css, uri);
                }
            }

            // output processed CSS
            final CSSWriter writer = new CSSWriter(new CSSWriterSettings(ECSSVersion.CSS30, false));
            return writer.getCSSAsString(css);
        } catch (final Exception e) {
            LOG.debug("error scrubbing CSS, returning empty CSS", e);
            return "";
        }
    }
}
