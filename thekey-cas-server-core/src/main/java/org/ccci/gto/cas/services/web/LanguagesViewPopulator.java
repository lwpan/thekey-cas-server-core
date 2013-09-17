package org.ccci.gto.cas.services.web;

import static org.ccci.gto.cas.Constants.VIEW_ATTR_LOCALE;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.springframework.web.servlet.support.RequestContextUtils;

public final class LanguagesViewPopulator extends AbstractViewPopulator {
    @NotNull
    private Languages languages;

    @Override
    protected void populateInternal(final ViewContext context) {
        final Locale locale = RequestContextUtils.getLocale(context.getRequest());

	// set locale related attributes
        context.setAttribute(VIEW_ATTR_LOCALE, locale);
        context.setAttribute("dir", this.languages.getDirection(locale));
        context.setAttribute("languages", this.languages);
    }

    /**
     * @param languages
     *            the list of supported languages to use
     */
    public void setLanguages(final Languages languages) {
	this.languages = languages;
    }
}
