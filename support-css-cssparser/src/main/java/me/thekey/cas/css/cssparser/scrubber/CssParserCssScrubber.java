package me.thekey.cas.css.cssparser.scrubber;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import me.thekey.cas.css.cssparser.scrubber.filter.CssFilter;
import org.ccci.gto.cas.css.scrubber.CssScrubber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;

public class CssParserCssScrubber implements CssScrubber {
    private static final Logger LOG = LoggerFactory.getLogger(CssParserCssScrubber.class);

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

    protected CSSStyleSheet parse(final InputSource source, final URI uri) throws IOException {
        try {
            final CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
            return parser.parseStyleSheet(source, null, (uri != null ? uri.toString() : null));
        } catch (final IOException e) {
            LOG.debug("error parsing CSS", e);
            throw e;
        }
    }

    public String scrub(final URI uri) {
        try {
            final URLConnection conn = uri.toURL().openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).setInstanceFollowRedirects(false);
            }

            final CSSStyleSheet css = this.scrub(new InputSource(new InputStreamReader(conn.getInputStream())), uri);
            return css.toString();
        } catch (final Exception e) {
            LOG.debug("error scrubbing CSS, returning empty CSS", e);
            return "";
        }
    }

    protected CSSStyleSheet scrub(final InputSource source, final URI uri) throws IOException {
        final CSSStyleSheet css = this.parse(source, uri);

        // process any defined css filters
        if (!filters.isEmpty()) {
            for (final CssFilter filter : filters) {
                filter.filter(css);
            }
        }

        return css;
    }
}
