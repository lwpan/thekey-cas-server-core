package org.ccci.gto.cas.web;

import java.lang.reflect.Method;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.IdmUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.validation.Assertion;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.web.servlet.ModelAndView;

public class ServiceValidateControllerAdvice implements AfterReturningAdvice {
    private static final Log log = LogFactory
	    .getLog(ServiceValidateControllerAdvice.class);

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

	log.debug("serviceValidate afterReturning");

	// only process if this is a casServiceSuccessView
	if (view.getViewName().equals("casServiceSuccessView")) {
	    // retrieve the current Assertion
	    final Assertion assertion = (Assertion) view.getModel().get(
		    MODEL_ASSERTION);

	    // retrieve the Service and Principal for the current Assertion
	    final Service service = assertion.getService();
	    final List<Authentication> authChain = assertion
		    .getChainedAuthentications();
	    final Principal principal = authChain.get(authChain.size() - 1)
		    .getPrincipal();

	    // retrieve the GcxUser object for the validated user
	    final GcxUser user = this.gcxUserService.findUserByEmail(principal
		    .getId());

	    // put the user attributes into the Model
	    view.addObject(
		    MODEL_ATTRIBUTES,
		    this.attributeComposer.getUserAttributes(user,
			    service.getId()));

	    // mark the domain for the current service as visited
	    IdmUtil.addDomainVisited(user, service.getId(),
		    this.gcxUserService,
		    Constants.SOURCEIDENTIFIER_SERVICEVALIDATOR);
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
     * @param attributeComposer the attributeComposer to set
     */
    public void setAttributeComposer(final AttributeComposer attributeComposer) {
	this.attributeComposer = attributeComposer;
    }
}
