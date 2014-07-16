package org.ccci.gto.cas.css.filter;

import static org.ccci.gto.cas.css.Constants.PARAMETER_CSS_URI;

import com.steadystate.css.dom.CSSImportRuleImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class FilteredImportCssFilter extends AbstractRuleCssFilter implements
	ReversibleFilter {
    private Type type = null;
    private final HashSet<URI> uris = new HashSet<URI>();
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
	    } catch (final URISyntaxException e) {
		log.debug("error parsing uri", e);
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
	    return type == null
		    || (type == Type.BLACKLIST && uris.contains(uri))
		    || (type == Type.WHITELIST && !uris.contains(uri));
	}

	return true;
    }

    @Override
    protected boolean filterRule(final CSSRule rule) {
	if (rule instanceof CSSImportRule
		&& rule.getType() == CSSRule.IMPORT_RULE) {
	    // get the uri
	    URI uri;
	    try {
		uri = new URI(((CSSImportRule) rule).getHref());
	    } catch (final URISyntaxException e) {
		log.debug("error parsing import url", e);
		return false;
	    }

	    // generate a filtered uri when needed
	    if (this.isFilteredUri(uri)) {
                uri = filterUri.buildFromMap(Collections.singletonMap(PARAMETER_CSS_URI, uri));
	    }

	    /*
	     * for {@link CSSImportRuleImpl} we can directly manipulate the href
	     * to get around the standard API limitations
	     */
	    if (rule instanceof CSSImportRuleImpl) {
		((CSSImportRuleImpl) rule).setHref(uri.toString());
	    }
	    /*
	     * The standard API doesn't have a way to directly manipulate the
	     * href, so we generate and parse an @import rule to update the href
	     */
	    else {
		try {
		    final CSSImportRule iRule = (CSSImportRule) rule;
		    rule.setCssText("@import \"" + uri.toString() + "\" "
			    + iRule.getMedia().getMediaText() + ";");
		} catch (final DOMException e) {
		    log.debug("Error updating @import rule", e);
		    return false;
		}
	    }
	}

	return super.filterRule(rule);
    }
}
