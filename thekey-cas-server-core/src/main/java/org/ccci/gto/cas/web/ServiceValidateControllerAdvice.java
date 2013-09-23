package org.ccci.gto.cas.web;

import static org.ccci.gto.cas.Constants.AUDIT_SOURCE_SERVICEVALIDATOR;

import java.lang.reflect.Method;
import java.util.List;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
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
    private static final Logger LOG = LoggerFactory.getLogger(ServiceValidateControllerAdvice.class);

    /** Constant representing the Assertion in the model. */
    private static final String MODEL_ASSERTION = "assertion";

    @NotNull
    private UserManager userManager;

    /**
     * @param manager
     *            the gcxUserService to set
     */
    public void setUserManager(final UserManager manager) {
        this.userManager = manager;
    }

    @Override
    public void afterReturning(final Object returnValue, final Method method,
	    final Object[] args, final Object target) throws Throwable {
	ModelAndView view = (ModelAndView) returnValue;

	// only process if this is a casServiceSuccessView
	if (view.getViewName().equals("casServiceSuccessView")) {
            LOG.debug("Attaching additional attributes to the ticket validation response");

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

	    // mark the domain for the current service as visited
	    try {
                LOG.debug("adding the current service to the domainsVisisted list");
                UserUtil.addVisitedService(this.userManager, user, service, AUDIT_SOURCE_SERVICEVALIDATOR);
	    } catch (Exception e) {
		// suppress errors because this isn't critical functionality
                LOG.error("Error updating the domainsVisited list during ticket validation", e);
	    }
	}
    }
}
