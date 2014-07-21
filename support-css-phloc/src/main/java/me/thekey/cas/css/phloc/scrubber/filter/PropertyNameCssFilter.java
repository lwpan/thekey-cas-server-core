package me.thekey.cas.css.phloc.scrubber.filter;

import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSStyleRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.visit.CSSVisitor;
import com.phloc.css.decl.visit.DefaultCSSVisitor;
import me.thekey.cas.css.scrubber.filter.ReversibleFilter;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

public final class PropertyNameCssFilter implements CssFilter, ReversibleFilter {
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
    public void filter(final CascadingStyleSheet css, final URI cssUri) {
        CSSVisitor.visitCSS(css, new DefaultCSSVisitor() {
            private CSSStyleRule currentRule;

            @Override
            public void onBeginStyleRule(final CSSStyleRule rule) {
                this.currentRule = rule;
            }

            @Override
            public void onDeclaration(final CSSDeclaration declaration) {
                final boolean contains = names.contains(declaration.getProperty().toLowerCase());
                if ((type == Type.BLACKLIST && contains) || (type == Type.WHITELIST && !contains)) {
                    currentRule.removeDeclaration(declaration);
                }
            }
        });
    }
}
