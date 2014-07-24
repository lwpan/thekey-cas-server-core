package me.thekey.cas.css.phloc.scrubber.filter;

import com.phloc.css.decl.CSSExpressionMemberTermURI;
import com.phloc.css.decl.CSSImportRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.regex.Pattern;

public final class AbsoluteUriCssFilter extends AbstractUriCssFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AbsoluteUriCssFilter.class);

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
    protected boolean filterImportRule(final CSSImportRule rule, final URI cssUri) {
        try {
            rule.setLocationString(this.resolveUri(cssUri, rule.getLocationString()));
        } catch(final Exception e) {
            LOG.debug("error processing import rule {}", rule,  e);
            return false;
        }

        // return success
        return true;
    }

    @Override
    protected boolean filterDeclarationUri(final CSSExpressionMemberTermURI uri, final URI cssUri) {
        try {
            uri.setURIString(this.resolveUri(cssUri, uri.getURIString()));
        } catch(final Exception e) {
            LOG.debug("error processing uri in declaration", e);
            return false;
        }

        return true;
    }
}
