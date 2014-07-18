package org.ccci.gto.cas.web.flow;

import org.apache.commons.lang.StringUtils;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;

import javax.validation.constraints.NotNull;

@Deprecated
public final class LegacyLoginAction {
    private static final Logger LOG = LoggerFactory.getLogger(LegacyLoginAction.class);

    /** Instance of ServiceRegistryManager */
    @NotNull
    private ServicesManager servicesManager;

    /** Core we delegate to for handling all ticket related tasks. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    // Request parameters to look for in the request
    private static final String PARAMETER_USERNAME = "username";
    private static final String PARAMETER_PASSWORD = "password";

    public final boolean isAutomatedLogin(final RequestContext context, final Service service,
                                          final UsernamePasswordCredentials credentials) {

	/*
	 * only allow automated login for services that require legacy login
	 * support
	 */
	final RegisteredService rService = servicesManager
		.findServiceBy(service);
        if (rService != null && rService instanceof TheKeyRegisteredService && ((TheKeyRegisteredService) rService)
                .isLegacyLogin()) {
            // check for a username and password in the request
            final ParameterMap params = context.getRequestParameters();
            final String userName = params.get(PARAMETER_USERNAME);
            final String password = params.get(PARAMETER_PASSWORD);

            // only attempt automated login when a username and password are present in the request
            if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(password)) {
                LOG.error("attempting legacy login for {}", rService.getName());

                // populate the credentials object
                credentials.setUsername(userName);
                credentials.setPassword(password);

                // return that an automated login should be attempted
                return true;
            }
        }

        // default to not supporting automated login
        return false;
    }

    public final String submit(final RequestContext context,
	    final Credentials credentials, final MessageContext messageContext)
	    throws Exception {
	try {
	    final Service service = WebUtils.getService(context);
	    final String ticketGrantingTicketId = this.centralAuthenticationService
		    .createTicketGrantingTicket(credentials);
	    final String serviceTicketId = this.centralAuthenticationService
		    .grantServiceTicket(ticketGrantingTicketId, service,
			    credentials);
	    WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
	    return "success";
	} catch (final TicketException e) {
	    populateErrorsInstance(e, messageContext);
	    return "error";
	}
    }

    private void populateErrorsInstance(final TicketException e, final MessageContext messageContext) {
        try {
            messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).defaultText(e.getCode()).build());
        } catch (final Exception fe) {
            LOG.error(fe.getMessage(), fe);
        }
    }

    public final void setCentralAuthenticationService(
	    final CentralAuthenticationService centralAuthenticationService) {
	this.centralAuthenticationService = centralAuthenticationService;
    }

    /**
     * @param servicesManager
     *            the servicesManager to set
     */
    public void setServicesManager(final ServicesManager servicesManager) {
	this.servicesManager = servicesManager;
    }
}
