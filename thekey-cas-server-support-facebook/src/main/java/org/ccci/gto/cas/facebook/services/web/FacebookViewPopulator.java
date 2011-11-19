package org.ccci.gto.cas.facebook.services.web;

import java.util.Collections;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.services.web.AbstractViewPopulator;
import org.ccci.gto.cas.services.web.ViewContext;

public final class FacebookViewPopulator extends AbstractViewPopulator {
    @NotNull
    private Map<String, String> attributes;

    @Override
    protected void populateInternal(final ViewContext context) {
	context.setAttribute("facebook", attributes);
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(final Map<String, String> attributes) {
	this.attributes = Collections.unmodifiableMap(attributes);
    }
}
