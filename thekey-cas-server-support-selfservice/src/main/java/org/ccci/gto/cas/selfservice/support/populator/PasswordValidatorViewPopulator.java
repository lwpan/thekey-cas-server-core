package org.ccci.gto.cas.selfservice.support.populator;

import static org.ccci.gto.cas.selfservice.Constants.VIEW_ATTR_PASSWORDRULES;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.selfservice.validator.PasswordValidator;
import org.ccci.gto.cas.services.web.AbstractViewPopulator;
import org.ccci.gto.cas.services.web.ViewContext;

public final class PasswordValidatorViewPopulator extends AbstractViewPopulator {
    @NotNull
    private PasswordValidator validator;

    public void setPasswordValidator(final PasswordValidator validator) {
	this.validator = validator;
    }

    @Override
    protected void populateInternal(final ViewContext context) {
	context.setAttribute(VIEW_ATTR_PASSWORDRULES,
		validator.getValidationJavascript());
    }
}
