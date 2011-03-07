package org.ccci.gcx.idm.web.flow;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationResponse;
import org.ccci.gcx.idm.core.service.AuthenticationService;
import org.ccci.gcx.idm.web.IdmUtil;
import org.ccci.gcx.idm.web.LoginFormController;
import org.springframework.util.Assert;
import org.springframework.webflow.context.ExternalContext;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.core.util.URLUtil;


/**
 * Uses the authentication service to check to see if 
 *   the user is presenting a ticket generating cookie that 
 *   proves they've already logged in. If so then we'll get a ticket back for them to use
 *   in redirecting to the service.
 * @author ken
 *
 */
public class TicketCheckerBean  {

	private AuthenticationService authservice;

	protected static final Log log = LogFactory.getLog(TicketCheckerBean.class);
	
	
	public void setAuthenticationService (AuthenticationService a_svc)
	{
		authservice = a_svc;
	}

	
	/**
	 * Checks to see if CAS approves the TGC and will give us a ticket.
	 * IF YES: returns true and service=ticket 
	 * IF NO:  returns false
	 */
	public boolean retrieveCASTicket(ExternalContext context) throws Exception {
        HttpServletRequest request = (HttpServletRequest) context.getNativeRequest();
        
        CasAuthenticationRequest casrequest = IdmUtil.buildCasAuthenticationRequest(request);
        CasAuthenticationResponse response = (CasAuthenticationResponse) authservice.handleSSORequest(casrequest);

        //add service to the session for all following flows and controllers.
        request.getSession().setAttribute(Constants.SESSIONATTRIBUTE_SERVICE, 
        									casrequest.getService());
        if(response.getLocation() != null)
        {
        	request.getSession().setAttribute(Constants.SESSIONATTRIBUTE_TICKET, URLUtil.extractParm(response.getLocation(), Constants.REQUESTPARAMETER_TICKET));
        }
               
        boolean retval = false;
        if(response.isAuthenticated())
        {
        	//cas approved us and issued a ticket!
        	context.getSessionMap().put(Constants.SESSIONATTRIBUTE_LOCATION, response.getLocation());

        	//TODO: whatever else is necessary
        	retval = true;
        }
        	
        return retval;
	}
	
	/**
	 * Checks to see if the request has "gateway=true" as a parameter condition. 
	 * @param context
	 * @return true if gateway=true is present, false otherwise.
	 * @throws Exception
	 */
	public boolean isGatewayMode(ExternalContext context) throws Exception 
	{
        HttpServletRequest request = (HttpServletRequest) context.getNativeRequest();
        
        boolean isgateway = false;
        
        //check for gateway=true value
        String gateway = request.getParameter(Constants.REQUESTPARAMETER_GATEWAY);
		if(StringUtils.isNotBlank(gateway))
		{
			if(gateway.equals(Constants.REQUESTPARAMETER_GATEWAY_TRUE))
				isgateway = true;
		}
        return isgateway;
	}
	
	/**
	 * Checks to see if the username parameter is present in the request 
	 *  >> If username parameter exists, return true
	 *  >> If not, return false. 
	 * @param context
	 * @return true, false
	 * @throws Exception
	 */
	public boolean hasCredentials(ExternalContext context) throws Exception
	{
		boolean auth = 
			StringUtils.isNotEmpty((String)context.getRequestParameterMap().get(Constants.REQUESTPARAMETER_USERNAME));
		
		if(log.isDebugEnabled())
			log.debug("hasCredentials says: "+auth);
		
		return auth;
	}
	

}
