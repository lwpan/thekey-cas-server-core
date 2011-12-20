package org.ccci.gto.cas.css.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.css.CSSStyleDeclaration;

import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.Property;

public final class PropertyValueCssFilter extends AbstractStyleCssFilter
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

    @Override
    protected void filterStyles(final CSSStyleDeclaration styles) {
	if (this.type != null) {
	    /*
	     * for {@link CSSStyleDeclarationImpl} we can directly manipulate
	     * the Property objects to get around the standard API limitations
	     */
	    if (styles instanceof CSSStyleDeclarationImpl) {
		final List<Property> properties = ((CSSStyleDeclarationImpl) styles)
			.getProperties();
		for (int j = 0; j < properties.size(); j++) {
		    final Property property = properties.get(j);
		    final boolean matches = this.checkValue(property.getValue()
			    .toString());
		    if ((this.type == Type.BLACKLIST && matches)
			    || (this.type == Type.WHITELIST && !matches)) {
			properties.remove(j);
			j--;
		    }
		}
	    } else {
		// the set of properties to remove from this rule
		final HashSet<String> propertiesToRemove = new HashSet<String>();

		final HashSet<String> seen = new HashSet<String>();
		for (int j = 0; j < styles.getLength(); j++) {
		    final String name = styles.item(j);

		    /*
		     * remove any duplicate properties because the standard API
		     * doesn't provide a way to manipulate properties that share
		     * the same name in a rule
		     */
		    if (seen.contains(name.toLowerCase())) {
			propertiesToRemove.add(name);
			continue;
		    }

		    /*
		     * add this property to the list of seen properties
		     */
		    seen.add(name.toLowerCase());

		    /*
		     * check to see if the value matches
		     */
		    final boolean matches = this.checkValue(styles
			    .getPropertyValue(name));
		    if ((this.type == Type.BLACKLIST && matches)
			    || (this.type == Type.WHITELIST && matches)) {
			propertiesToRemove.add(name);
		    }
		}

		// remove any properties that have invalid values
		this.removeProperties(styles, propertiesToRemove);
	    }
	}
    }
}
