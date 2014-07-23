package me.thekey.cas.css.cssparser.scrubber.filter;

import me.thekey.cas.css.scrubber.filter.ReversibleFilter;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.util.Collection;
import java.util.HashSet;

public final class PropertyNameCssFilter extends AbstractStyleCssFilter implements ReversibleFilter {
    private Type type = null;
    private final HashSet<String> names = new HashSet<>();

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

    @Override
    protected void filterStyles(final CSSStyleDeclaration styles) {
        // this is a blacklist CssFilter
        if (type == Type.BLACKLIST) {
            // remove all black listed property names
            this.removeProperties(styles, this.names);
        }
        // this is a whitelist CssFilter
        else if (type == Type.WHITELIST) {
            // remove any properties not in the whitelist
            final HashSet<String> propertiesToRemove = new HashSet<String>();
            for (int j = 0; j < styles.getLength(); j++) {
                final String name = styles.item(j);
                if (!this.names.contains(name.toLowerCase())) {
                    propertiesToRemove.add(name);
                }
            }
            this.removeProperties(styles, propertiesToRemove);
        }
    }
}
