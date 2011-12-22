package org.ccci.gto.cas.css.filter;

import java.util.Collection;
import java.util.HashSet;

import org.w3c.dom.css.CSSRule;

public final class RuleTypeCssFilter extends AbstractRuleCssFilter implements
	ReversibleFilter {
    private Type type = null;
    private final HashSet<Short> types = new HashSet<Short>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.css.filter.ReversibleFilter#setFilterType(org.ccci.gto
     * .cas.css.filter.ReversibleFilter.Type)
     */
    @Override
    public void setFilterType(final Type type) {
	this.type = type;
    }

    public void addType(final Short type) {
	this.types.add(type);
    }

    public void setTypes(final Collection<? extends Short> types) {
	this.types.clear();
	if (types != null) {
	    this.types.addAll(types);
	}
    }

    /**
     * Check to see if the type of the specified rule is on the whitelist or
     * blacklist
     * 
     * @param rule
     *            the rule to filter
     * @return true if this rule type is on the whitelist, this rule type isn't
     *         on the blacklist, or there isn't a whitelist or blacklist
     */
    @Override
    protected boolean filterRule(final CSSRule rule) {
	return type == null
		|| (type == Type.WHITELIST && types.contains(rule.getType()))
		|| (type == Type.BLACKLIST && !types.contains(rule.getType()));
    }
}
