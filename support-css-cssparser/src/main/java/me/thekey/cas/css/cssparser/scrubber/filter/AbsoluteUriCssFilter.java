package me.thekey.cas.css.cssparser.scrubber.filter;

import com.steadystate.css.dom.CSSImportRuleImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public final class AbsoluteUriCssFilter extends AbstractValueCssFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AbsoluteUriCssFilter.class);

    private final Pattern STRIP_DOTS = Pattern.compile("\\A(\\.\\.\\/)*");

    private URI getBaseUri(final CSSStyleDeclaration styles) {
        if (styles != null) {
            return this.getBaseUri(styles.getParentRule());
        }
        return null;
    }

    private URI getBaseUri(final CSSRule rule) {
        if (rule != null) {
            final CSSStyleSheet css = rule.getParentStyleSheet();
            if (css != null) {
                final String uri = css.getHref();
                if (uri != null) {
                    try {
                        return new URI(uri);
                    } catch (final URISyntaxException ignored) {
                    }
                }
            }

            // maybe the parent rule has a stylesheet with an href
            return this.getBaseUri(rule.getParentRule());
        }

        return null;
    }

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
    protected boolean filterRuleInternal(final CSSRule rule) {
        if (rule instanceof CSSImportRule && rule.getType() == CSSRule.IMPORT_RULE) {
            // get the baseUri
            final URI baseUri = this.getBaseUri(rule);
            if (baseUri == null) {
                return true;
            }

            // get an absolute url for this rule
            final String uri;
            try {
                uri = this.resolveUri(baseUri, ((CSSImportRule) rule).getHref());
            } catch (final IllegalArgumentException e) {
                return false;
            }

            // for {@link CSSImportRuleImpl} we can directly manipulate the href to get around the standard API
            // limitations
            if (rule instanceof CSSImportRuleImpl) {
                ((CSSImportRuleImpl) rule).setHref(uri);
            }
            // The standard API doesn't have a way to directly manipulate the href,
            // so we generate and parse an @import rule to update the href
            else {
                try {
                    final CSSImportRule iRule = (CSSImportRule) rule;
                    rule.setCssText("@import \"" + uri + "\" " + iRule.getMedia().getMediaText() + ";");
                } catch (final DOMException e) {
                    LOG.error("Error updating @import rule", e);
                    return false;
                }
            }
        }

        return super.filterRuleInternal(rule);
    }

    @Override
    protected boolean filterValue(final CSSStyleDeclaration styles, final String name, final CSSValue value) {
        final URI baseUri = this.getBaseUri(styles);
        if (baseUri == null) {
            return true;
        }

        // return false if there is an error filtering this rule
        if (!filterValue(value, baseUri)) {
            return false;
        }

        return super.filterValue(styles, name, value);
    }

    private boolean filterValue(final CSSValue value, final URI baseUri) {
        // handle a value list
        if (value instanceof CSSValueList && value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
            // process all values in this value list
            final CSSValueList lValue = (CSSValueList) value;
            for (int i = 0; i < lValue.getLength(); i++) {
                if (!this.filterValue(lValue.item(i), baseUri)) {
                    return false;
                }
            }
        }

        // handle primitive values
        if (value instanceof CSSPrimitiveValue && value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
            final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
            // only update URI values
            if (pValue.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
                try {
                    pValue.setStringValue(CSSPrimitiveValue.CSS_URI, this.resolveUri(baseUri, pValue.getStringValue()));
                } catch (final IllegalArgumentException e) {
                    return false;
                }
            }
        }

        return true;
    }
}
