package me.thekey.cas.css.scrubber.filter;

public interface ReversibleFilter {
    enum Type {
        WHITELIST(0), BLACKLIST(1);

        public final int index;

        private Type(final int index) {
            this.index = index;
        }
    }

    void setFilterType(Type type);
}
