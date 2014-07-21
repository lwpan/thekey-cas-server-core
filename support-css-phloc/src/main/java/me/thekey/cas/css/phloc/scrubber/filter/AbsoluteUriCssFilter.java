package me.thekey.cas.css.phloc.scrubber.filter;

import java.net.URI;
import java.util.regex.Pattern;

public final class AbsoluteUriCssFilter extends AbstractUriCssFilter {
    private final Pattern STRIP_DOTS = Pattern.compile("\\A(\\.\\.\\/)*");

    private String resolveUri(final URI baseUri, final String relUri) {
        URI uri = baseUri.resolve(relUri);

        // strip out any preceding ../ from the path
        final String path = uri.getRawPath();
        if (path != null && path.length() >= 4 && path.substring(0, 4).equals("/../")) {
            final URI rootUri = uri.resolve("/");
            uri = rootUri.resolve(STRIP_DOTS.matcher(rootUri.relativize(uri).toString()).replaceAll(""));
        }

        return uri.normalize().toString();
    }

    @Override
    protected String filterImportUri(final String uri, final URI cssUri) {
        return this.resolveUri(cssUri, uri);
    }

    @Override
    protected String filterDeclarationUri(final String uri, final URI cssUri) {
        return this.resolveUri(cssUri, uri);
    }
}
