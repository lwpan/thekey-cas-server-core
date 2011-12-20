package org.ccci.gto.cas.css.filter;

import java.util.Collection;
import java.util.HashSet;

import org.w3c.dom.css.CSSStyleDeclaration;

public final class PropertyNameCssFilter extends AbstractStyleCssFilter
	implements ReversibleFilter {
    private Type type = null;
    private final HashSet<String> names = new HashSet<String>();

    public void addName(final String name) {
	if (name != null) {
	    this.names.add(name.toLowerCase());
	}
    }

    public void setNames(final Collection<? extends String> names) {
	this.names.clear();
	if (names != null) {
	    for (final String name : names) {
		this.addName(name);
	    }
	}
    }

    @Override
    public void setFilterType(final Type type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.css.filter.AbstractStyleCssFilter#filterStyles(org.w3c
     * .dom.css.CSSStyleDeclaration)
     */
    @Override
    protected void filterStyles(final CSSStyleDeclaration styles) {
	// the set of properties to remove from this rule
	final HashSet<String> propertiesToRemove = new HashSet<String>();

	// this is a whitelist CssFilter
	if (type == Type.WHITELIST) {
	    // add any properties not in the whitelist to propertiesToRemove
	    for (int j = 0; j < styles.getLength(); j++) {
		final String name = styles.item(j);
		if (!this.names.contains(name.toLowerCase())) {
		    propertiesToRemove.add(name);
		}
	    }
	}
	// this is a blacklist CssFilter
	else if (type == Type.BLACKLIST) {
	    // add all blacklisted properties to propertiesToRemove
	    propertiesToRemove.addAll(this.names);
	}

	/* remove all properties that need to be removed */
	for (final String property : propertiesToRemove) {
	    /*
	     * removeProperty only removes the first property with the specified
	     * name, loop until all properties using the specified name are
	     * removed
	     */
	    while (true) {
		final int properties = styles.getLength();
		styles.removeProperty(property);
		if (properties == styles.getLength()) {
		    break;
		}
	    }
	}
    }
}
