package me.thekey.cas.css.phloc.scrubber.filter;

import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSExpressionMemberTermURI;
import com.phloc.css.decl.CSSImportRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.ICSSTopLevelRule;
import com.phloc.css.decl.visit.CSSVisitor;
import com.phloc.css.decl.visit.DefaultCSSUrlVisitor;

import java.net.URI;

public abstract class AbstractUriCssFilter implements CssFilter {
    @Override
    public final void filter(final CascadingStyleSheet css, final URI cssUri) {
        CSSVisitor.visitCSSUrl(css, new DefaultCSSUrlVisitor() {
            @Override
            public void onImport(final CSSImportRule rule) {
                rule.setLocationString(filterImportUri(rule.getLocationString(), cssUri));
            }

            @Override
            public void onUrlDeclaration(final ICSSTopLevelRule rule, final CSSDeclaration declaration,
                                         final CSSExpressionMemberTermURI uri) {
                uri.setURIString(filterDeclarationUri(uri.getURIString(), cssUri));
            }
        });
    }

    protected String filterImportUri(final String uri, final URI cssUri) {
        return uri;
    }

    protected String filterDeclarationUri(final String uri, final URI cssUri) {
        return uri;
    }
}
