package me.thekey.cas.css.phloc.scrubber.filter;

import com.phloc.css.decl.CSSUnknownRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.visit.CSSVisitor;
import com.phloc.css.decl.visit.DefaultCSSVisitor;

import java.net.URI;

public final class UnknownRuleCssFilter implements CssFilter {
    @Override
    public void filter(final CascadingStyleSheet css, final URI cssUri) {
        CSSVisitor.visitCSS(css, new DefaultCSSVisitor() {
            @Override
            public void onUnknownRule(CSSUnknownRule aUnknownRule) {
                css.removeRule(aUnknownRule);
            }
        });
    }
}
