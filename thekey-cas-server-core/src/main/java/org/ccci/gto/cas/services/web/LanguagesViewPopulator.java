package org.ccci.gto.cas.services.web;

import javax.validation.constraints.NotNull;

import org.springframework.web.servlet.support.RequestContextUtils;

public final class LanguagesViewPopulator extends AbstractViewPopulator {
    @NotNull
    private Languages languages;

    @Override
    protected void populateInternal(final ViewContext context) {
	final String locale = RequestContextUtils.getLocale(
		context.getRequest()).getLanguage();

	// set locale related attributes
	context.setAttribute("locale", locale);
	context.setAttribute("languages", languages);
    }

    /**
     * @param languages
     *            the list of supported languages to use
     */
    public void setLanguages(final Languages languages) {
	this.languages = languages;
    }
}
