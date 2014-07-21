package me.thekey.cas.css.phloc.scrubber.filter;

import static org.ccci.gto.cas.css.Constants.PARAMETER_CSS_URI;

import me.thekey.cas.css.scrubber.filter.ReversibleFilter;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class FilteredImportCssFilter extends AbstractUriCssFilter implements ReversibleFilter {
    private Type type = null;
    private final HashSet<URI> uris = new HashSet<>();
    private UriBuilder filterUri;

    public FilteredImportCssFilter() {
        this.setFilterUri("?");
    }

    @Override
    public void setFilterType(final Type type) {
        this.type = type;
    }

    /**
     * @param uri the filter uri to use for filtering imported css
     */
    public void setFilterUri(final String uri) {
        this.filterUri = UriBuilder.fromUri(uri);
        this.filterUri.queryParam(PARAMETER_CSS_URI, "{" + PARAMETER_CSS_URI + "}");
    }

    public void addUri(final String uri) {
        if (uri != null) {
            try {
                this.addUri(new URI(uri));
            } catch (final URISyntaxException ignored) {
            }
        }
    }

    public void addUri(final URI uri) {
        if (uri != null && uri.isAbsolute()) {
            uris.add(uri);
        }
    }

    public void setUris(final Collection<?> uris) {
        this.uris.clear();
        if (uris != null) {
            for (final Object uri : uris) {
                if (uri instanceof URI) {
                    this.addUri((URI) uri);
                } else {
                    this.addUri(uri.toString());
                }
            }
        }
    }

    private boolean isFilteredUri(final URI uri) {
        if (uri.isAbsolute()) {
            return type == null || (type == Type.BLACKLIST && uris.contains(uri)) || (type == Type.WHITELIST && !uris
                    .contains(uri));
        }
        return true;
    }

    @Override
    protected String filterImportUri(final String importUri, final URI cssUri) {
        // XXX: we are currently avoiding checked exceptions because we have no way of deleting the invalid rule
        URI uri = URI.create(importUri);

        // generate a filtered uri when needed
        if (this.isFilteredUri(uri)) {
            uri = filterUri.buildFromMap(Collections.singletonMap(PARAMETER_CSS_URI, uri));
        }

        return uri.toString();
    }
}
