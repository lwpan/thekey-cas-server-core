package org.ccci.gto.cas.web;

import java.lang.reflect.Method;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gto.cas.util.AuthenticationUtil;
import org.ccci.gto.cas.util.UserUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

public class ServiceValidateControllerAdvice implements AfterReturningAdvice {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** Constant representing the Assertion in the model. */
    private static final String MODEL_ASSERTION = "assertion";

    /** Constant representing the attributes in the model. */
    private static final String MODEL_ATTRIBUTES = "casAttrs";

    @NotNull
    private GcxUserService gcxUserService;

    @NotNull
    private AttributeComposer attributeComposer;

    public void afterReturning(final Object returnValue, final Method method,
	    final Object[] args, final Object target) throws Throwable {
	ModelAndView view = (ModelAndView) returnValue;

	// only process if this is a casServiceSuccessView
	if (view.getViewName().equals("casServiceSuccessView")) {
	    // retrieve the current Assertion
	    final Assertion assertion = (Assertion) view.getModel().get(
		    MODEL_ASSERTION);

	    // retrieve the Service and initial authentication for the current
	    // Assertion
	    final Service service = assertion.getService();
	    final List<Authentication> authChain = assertion
		    .getChainedAuthentications();
	    final Authentication authentication = authChain.get(authChain
		    .size() - 1);

	    // retrieve the GcxUser object for the assertion
	    final GcxUser user = AuthenticationUtil.getUser(authentication);
	    Assert.notNull(user);

	    log.debug("Attaching additional attributes to the ticket validation response");

	    // put the user attributes into the Model
	    view.addObject(
		    MODEL_ATTRIBUTES,
		    this.attributeComposer.getUserAttributes(user,
			    service.getId()));

	    log.debug("adding the current service to the domainsVisisted list");

	    // mark the domain for the current service as visited
	    try {
		UserUtil.addVisitedService(this.gcxUserService, user, service,
			Constants.SOURCEIDENTIFIER_SERVICEVALIDATOR);
	    } catch (Exception e) {
		// suppress errors because this isn't critical functionality
		log.error(
			"Error updating the domainsVisited list during ticket validation",
			e);
	    }
	}
    }

    /**
     * @param gcxUserService
     *            the gcxUserService to set
     */
    public void setGcxUserService(final GcxUserService gcxUserService) {
	this.gcxUserService = gcxUserService;
    }

    /**
     * @param attributeComposer
     *            the attributeComposer to set
     */
    public void setAttributeComposer(final AttributeComposer attributeComposer) {
	this.attributeComposer = attributeComposer;
    }
}
