package org.ccci.gto.cas.css.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

import com.steadystate.css.dom.CSSImportRuleImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.Property;

public final class AbsoluteUriCssFilter extends AbstractStyleCssFilter {
    private final Pattern STRIP_DOTS = Pattern.compile("\\A(\\.\\.\\/)*");

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.css.filter.AbstractStyleCssFilter#filterStyles(org.w3c
     * .dom.css.CSSStyleDeclaration)
     */
    @Override
    protected void filterStyles(final CSSStyleDeclaration styles) {
	// get the baseUri
	final URI baseUri = this.getBaseUri(styles);
	if (baseUri == null) {
	    return;
	}

	/*
	 * for {@link CSSStyleDeclarationImpl} we can directly manipulate the
	 * Property objects to get around the standard API limitations
	 */
	if (styles instanceof CSSStyleDeclarationImpl) {
	    final List<Property> properties = ((CSSStyleDeclarationImpl) styles)
		    .getProperties();
	    for (int j = 0; j < properties.size(); j++) {
		filterValue(properties.get(j).getValue(), baseUri);
	    }
	} else {
	    // this code doesn't handle 2 properties sharing the same name
	    // correctly due to an API limitation, but we do our best
	    for (int j = 0; j < styles.getLength(); j++) {
		filterValue(styles.getPropertyCSSValue(styles.item(j)), baseUri);
	    }
	}
    }

    private void filterValue(final CSSValue value, final URI baseUri) {
	// handle a value list
	if (value instanceof CSSValueList
		&& value.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
	    // process all values in this value list
	    final CSSValueList lValue = (CSSValueList) value;
	    for (int i = 0; i < lValue.getLength(); i++) {
		this.filterValue(lValue.item(i), baseUri);
	    }
	}

	// handle primitive values
	if (value instanceof CSSPrimitiveValue
		&& value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
	    final CSSPrimitiveValue pValue = (CSSPrimitiveValue) value;
	    // only update URI values
	    if (pValue.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
		pValue.setStringValue(CSSPrimitiveValue.CSS_URI,
			this.resolveUri(baseUri, pValue.getStringValue()));
	    }
	}
    }

    private String resolveUri(final URI baseUri, final String relUri) {
	URI uri = baseUri.resolve(relUri);

	// strip out any preceding ../ from the path
	final String path = uri.getRawPath();
	if (path != null && path.substring(0, 4).equals("/../")) {
	    final URI rootUri = uri.resolve("/");
	    uri = rootUri.resolve(STRIP_DOTS.matcher(
		    rootUri.relativize(uri).toString()).replaceAll(""));
	}

	return uri.normalize().toString();
    }

    private URI getBaseUri(final CSSRule rule) {
	if (rule != null) {
	    final CSSStyleSheet css = rule.getParentStyleSheet();
	    if (css != null) {
		final String uri = css.getHref();
		if (uri != null) {
		    try {
			return new URI(uri);
		    } catch (final URISyntaxException e) {
		    }
		}
	    }

	    // maybe the parent rule has a stylesheet with an href
	    return this.getBaseUri(rule.getParentRule());
	}

	return null;
    }

    private URI getBaseUri(final CSSStyleDeclaration styles) {
	if (styles != null) {
	    return this.getBaseUri(styles.getParentRule());
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.css.filter.AbstractStyleCssFilter#filterRuleInternal
     * (org.w3c.dom.css.CSSRule)
     */
    @Override
    protected void filterRuleInternal(final CSSRule rule) {
	if (rule instanceof CSSImportRule
		&& rule.getType() == CSSRule.IMPORT_RULE) {
	    // get the baseUri
	    final URI baseUri = this.getBaseUri(rule);
	    if (baseUri == null) {
		return;
	    }

	    // get an absolute url for this rule
	    final String uri = this.resolveUri(baseUri,
		    ((CSSImportRule) rule).getHref());

	    /*
	     * for {@link CSSImportRuleImpl} we can directly manipulate the href
	     * to get around the standard API limitations
	     */
	    if (rule instanceof CSSImportRuleImpl) {
		((CSSImportRuleImpl) rule).setHref(uri);
	    }
	    /*
	     * The standard API doesn't have a way to directly manipulate the
	     * href, so we generate and parse an @import rule to update the href
	     */
	    else {
		try {
		    final CSSImportRule iRule = (CSSImportRule) rule;
		    rule.setCssText("@import \"" + uri + "\" "
			    + iRule.getMedia().getMediaText() + ";");
		} catch (final DOMException e) {
		    log.error("Error updating @import rule", e);
		}
	    }
	}
    }
}
