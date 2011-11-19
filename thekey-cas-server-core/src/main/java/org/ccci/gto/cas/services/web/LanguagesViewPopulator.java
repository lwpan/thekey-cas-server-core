package org.ccci.gto.cas.services.web;

import javax.validation.constraints.NotNull;

import org.springframework.web.servlet.support.RequestContextUtils;

public final class LanguagesViewPopulator extends AbstractViewPopulator {
    @NotNull
    private LanguagesList languages;

    @Override
    protected void populateInternal(final ViewContext context) {
	final String locale = RequestContextUtils.getLocale(
		context.getRequest()).getLanguage();

	// set locale related attributes
	context.setAttribute("locale", locale);
	context.setAttribute("languages", languages.getSortedLanguages());
    }

    /**
     * @param languages
     *            the list of supported languages to use
     */
    public void setLanguages(final LanguagesList languages) {
	this.languages = languages;
    }
}
