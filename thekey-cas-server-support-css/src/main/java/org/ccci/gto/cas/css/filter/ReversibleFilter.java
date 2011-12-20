package org.ccci.gto.cas.css.filter;

public interface ReversibleFilter {
    public enum Type {
	WHITELIST(0), BLACKLIST(1);

	public final int index;

	private Type(final int index) {
	    this.index = index;
	}
    }

    public void setFilterType(final Type type);
}
