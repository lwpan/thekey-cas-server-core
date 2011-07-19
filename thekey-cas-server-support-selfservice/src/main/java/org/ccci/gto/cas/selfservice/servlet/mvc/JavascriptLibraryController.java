package org.ccci.gto.cas.selfservice.servlet.mvc;

import static org.ccci.gto.cas.selfservice.Constants.SESSION_ATTR_PASSWORDJAVASCRIPT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.selfservice.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.WebUtils;

/**
 * Provides the password validation client javascript.
 */
public class JavascriptLibraryController implements Controller {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    private PasswordValidator validator;

    public void setPasswordValidator(final PasswordValidator validator) {
	this.validator = validator;
    }

    /**
     * handles a request for a javascript library that isn't a static page.
     */
    public ModelAndView handleRequest(final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	// stuff the clientjavascript into the session
	log.debug("setting the client javascript");
	WebUtils.setSessionAttribute(request, SESSION_ATTR_PASSWORDJAVASCRIPT,
		validator.getValidationJavascript());

	return new ModelAndView("javascriptPasswordValidation");
    }
}
