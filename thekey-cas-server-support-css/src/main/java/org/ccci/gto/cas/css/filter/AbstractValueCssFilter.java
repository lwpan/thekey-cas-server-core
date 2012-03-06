package org.ccci.gto.cas.css.filter;

import java.util.HashSet;
import java.util.List;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.Property;

public class AbstractValueCssFilter extends AbstractStyleCssFilter {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.css.filter.AbstractStyleCssFilter#filterStyles(org.w3c
     * .dom.css.CSSStyleDeclaration)
     */
    @Override
    protected final void filterStyles(final CSSStyleDeclaration styles) {
	/*
	 * for {@link CSSStyleDeclarationImpl} we can directly manipulate the
	 * Property objects to get around the standard API limitations
	 */
	if (styles instanceof CSSStyleDeclarationImpl) {
	    final List<Property> properties = ((CSSStyleDeclarationImpl) styles)
		    .getProperties();
	    for (int j = 0; j < properties.size(); j++) {
		final Property property = properties.get(j);
		if (!this.filterValue(styles, property.getName(),
			property.getValue())) {
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
		 * doesn't provide a way to manipulate properties that share the
		 * same name in a rule
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
		if (!this.filterValue(styles, name,
			styles.getPropertyCSSValue(name))) {
		    propertiesToRemove.add(name);
		}
	    }

	    // remove any properties that have invalid values
	    this.removeProperties(styles, propertiesToRemove);
	}

	// run any additional filtering needed for this rule
	this.filterStylesInternal(styles);
    }

    protected void filterStylesInternal(final CSSStyleDeclaration styles) {
    }

    protected boolean filterValue(final CSSStyleDeclaration styles,
	    final String name, final CSSValue value) {
	return true;
    }
}
