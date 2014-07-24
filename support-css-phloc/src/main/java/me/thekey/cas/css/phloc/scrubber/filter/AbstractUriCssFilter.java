package me.thekey.cas.css.phloc.scrubber.filter;

import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSExpressionMemberTermURI;
import com.phloc.css.decl.CSSImportRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.ICSSTopLevelRule;
import com.phloc.css.decl.IHasCSSDeclarations;
import com.phloc.css.decl.visit.CSSVisitor;
import com.phloc.css.decl.visit.DefaultCSSUrlVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public abstract class AbstractUriCssFilter implements CssFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractUriCssFilter.class);

    @Override
    public final void filter(final CascadingStyleSheet css, final URI cssUri) {
        CSSVisitor.visitCSSUrl(css, new DefaultCSSUrlVisitor() {
            @Override
            public void onImport(final CSSImportRule rule) {
                if (!filterImportRule(rule, cssUri)) {
                    css.removeImportRule(rule);
                }
            }

            @Override
            public void onUrlDeclaration(final ICSSTopLevelRule rule, final CSSDeclaration declaration,
                                         final CSSExpressionMemberTermURI uri) {
                if (!filterDeclarationUri(uri, cssUri)) {
                    if (rule instanceof IHasCSSDeclarations) {
                        ((IHasCSSDeclarations) rule).removeDeclaration(declaration);
                    } else {
                        LOG.error("unrecognized rule type {}", rule.getClass());
                        throw new IllegalStateException();
                    }
                }
            }
        });
    }

    /**
     * Filter the specified {@link CSSImportRule}
     *
     * @param rule   The @import rule being filtered
     * @param cssUri the uri of the CSS being processed
     * @return boolean indicating if the rule was successfully filtered
     */
    protected boolean filterImportRule(final CSSImportRule rule, final URI cssUri) {
        return true;
    }

    /**
     * Filter the specified url() declaration
     *
     * @param uri    the url() declaration being filtered
     * @param cssUri the uri of the CSS being processed
     * @return boolean indicating if the rule was successfully filtered
     */
    protected boolean filterDeclarationUri(final CSSExpressionMemberTermURI uri, final URI cssUri) {
        return true;
    }
}
