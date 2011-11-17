package org.ccci.gto.cas.web.flow;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Deprecated
public class LegacyRedirectHeadersAction extends AbstractAction {
    /** Instance of ServiceRegistryManager */
    @NotNull
    private ServicesManager servicesManager;

    @Override
    protected Event doExecute(final RequestContext context) throws Exception {
	// extract the service from the request
	final Service service = (Service) context.getFlowScope().get("service");

	/*
	 * only add the CAS-Service and CAS-Ticket headers when the service
	 * requires the legacy headers
	 */
	final RegisteredService rService = servicesManager
		.findServiceBy(service);
	if (rService != null && rService instanceof TheKeyRegisteredService
		&& ((TheKeyRegisteredService) rService).isLegacyHeaders()) {
	    // Log an error message about this being legacy functionality
	    logger.fatal("Setting Legacy CAS-Service and CAS-Ticket headers on postLogin redirect for "
		    + service
		    + ". Code should be updated to utilize RESTful API for logging in and retrieving a ticket");

	    // set the CAS-Service and CAS-Ticket headers on the current
	    // response
	    final HttpServletResponse response = WebUtils
		    .getHttpServletResponse(context);
	    final String ticket = (String) context.getRequestScope().get(
		    "serviceTicketId");
	    response.setHeader("CAS-Service", service.getId());
	    response.setHeader("CAS-Ticket", ticket);
	}

	// Always return success
	return success();
    }

    /**
     * @param servicesManager
     *            the servicesManager to set
     */
    public void setServicesManager(final ServicesManager servicesManager) {
	this.servicesManager = servicesManager;
    }
}
