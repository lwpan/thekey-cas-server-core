package org.ccci.gto.cas.web.flow;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.config.ServerConfigList;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Deprecated
public class LegacyRedirectHeadersAction extends AbstractAction {
    @NotNull
    private ServerConfigList redlist;

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
	// extract the service from the request
	final String service = ((Service) context.getFlowScope().get("service"))
		.getId();

	// only add the CAS-Service and CAS-Ticket headers when the service is
	// in the redlist
	if (this.redlist.inList(service)) {
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
	    response.setHeader("CAS-Service", service);
	    response.setHeader("CAS-Ticket", ticket);
	}

	// Always return success
	return success();
    }

    /**
     * @param redlist the redlist to set
     */
    public void setRedlist(ServerConfigList redlist) {
	this.redlist = redlist;
    }
}
