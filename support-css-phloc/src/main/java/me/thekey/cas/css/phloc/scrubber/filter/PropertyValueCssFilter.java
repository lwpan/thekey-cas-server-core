package me.thekey.cas.css.phloc.scrubber.filter;

import com.phloc.css.ECSSVersion;
import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSStyleRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.visit.CSSVisitor;
import com.phloc.css.decl.visit.DefaultCSSVisitor;
import com.phloc.css.writer.CSSWriterSettings;
import me.thekey.cas.css.scrubber.filter.ReversibleFilter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public final class PropertyValueCssFilter implements CssFilter, ReversibleFilter {
    private Type type = null;
    private final ArrayList<Pattern> values = new ArrayList<>();

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
    public void filter(final CascadingStyleSheet css, URI cssUri) {
        CSSVisitor.visitCSS(css, new DefaultCSSVisitor() {
            private CSSStyleRule currentRule;

            @Override
            public void onBeginStyleRule(final CSSStyleRule rule) {
                this.currentRule = rule;
            }

            @Override
            public void onDeclaration(final CSSDeclaration declaration) {
                final boolean matches = checkValue(declaration.getExpression().getAsCSSString(new CSSWriterSettings
                        (ECSSVersion.CSS30), 0));
                if ((type == Type.BLACKLIST && matches) || (type == Type.WHITELIST && !matches)) {
                    this.currentRule.removeDeclaration(declaration);
                }
            }
        });
    }
}
