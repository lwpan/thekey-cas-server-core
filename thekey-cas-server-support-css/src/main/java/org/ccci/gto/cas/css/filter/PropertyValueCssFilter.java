package org.ccci.gto.cas.css.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public final class PropertyValueCssFilter extends AbstractValueCssFilter
	implements ReversibleFilter {
    private Type type = null;
    private final ArrayList<Pattern> values = new ArrayList<Pattern>();

    @Override
    public void setFilterType(final Type type) {
	this.type = type;
    }

    public void addValue(final String pattern) {
	if (pattern != null) {
	    this.values.add(Pattern.compile(pattern));
	}
    }

    public void setValues(final Collection<? extends String> patterns) {
	this.values.clear();
	if (patterns != null) {
	    for (final String pattern : patterns) {
		this.addValue(pattern);
	    }
	}
    }

    private boolean checkValue(final String value) {
	for (final Pattern pattern : this.values) {
	    if (pattern.matcher(value).find()) {
		return true;
	    }
	}

	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.css.filter.AbstractValueCssFilter#filterValue(org.w3c
     * .dom.css.CSSStyleDeclaration, org.w3c.dom.css.CSSValue)
     */
    @Override
    protected boolean filterValue(final CSSStyleDeclaration styles,
	    final String name, final CSSValue value) {
	// short-circuit if a filter type wasn't set
	if (this.type == null) {
	    return true;
	}

	// check to see if this value matches the specified patterns
	final boolean matches = this.checkValue(value.getCssText());
	if ((this.type == Type.BLACKLIST && matches)
		|| (this.type == Type.WHITELIST && !matches)) {
	    return false;
	}

	// return the default value
	return super.filterValue(styles, name, value);
    }
}
