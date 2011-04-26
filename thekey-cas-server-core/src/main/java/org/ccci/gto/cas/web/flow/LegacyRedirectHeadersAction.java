package org.ccci.gto.cas.web.flow;

import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Deprecated
public class LegacyRedirectHeadersAction extends AbstractAction {
    @Override
    protected Event doExecute(RequestContext context) throws Exception {
	// Log an error message about this being deprecated functionality
	logger.fatal("Setting Deprecated CAS-Service and CAS-Ticket headers on postLogin redirect. Code should utilize RESTful API for logging in and retrieving a ticket");

	// set the CAS-Service and CAS-Ticket headers on the current response
	// TODO: maybe implement a list of domains that require the CAS-Service
	// and CAS-Ticket headers to encourage people to not use these headers
	final HttpServletResponse response = WebUtils
		.getHttpServletResponse(context);
	final Service service = (Service) context.getFlowScope().get("service");
	final String ticket = (String) context.getRequestScope().get(
		"serviceTicketId");
	response.setHeader("CAS-Service", service.getId());
	response.setHeader("CAS-Ticket", ticket);

	// Always return success
	return success();
    }

}
