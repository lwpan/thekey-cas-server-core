package org.ccci.gcx.idm.web.flow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.binding.message.MessageBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.service.AuthenticationService;
import org.ccci.gcx.idm.web.IdmUtil;

/**
 * Destroys Ticket Generating Cookie and tells CAS we're logged out.
 * @author ken
 *
 */
public class TicketDestroyerBean {

	private CookieRetrievingCookieGenerator cg;
	private AuthenticationService authservice;
	
	protected static final Log log = LogFactory.getLog(TicketDestroyerBean.class);

	
	public void setAuthenticationService (AuthenticationService a_svc)
	{
		authservice = a_svc;
	}
	/**
	 * Destroy a cas ticket. Also invalidates the session (which might need to be broken out at some point?)
	 * @param a_externalContext
	 * @param a_messageContext
	 * @return
	 */
	public void destroyCASTicket(ExternalContext a_externalContext, MessageContext a_messageContext)
	{
		if(log.isDebugEnabled())log.debug("DESTROYING CAS ticket... (Logging out)");
		HttpServletRequest req = (HttpServletRequest) a_externalContext.getNativeRequest();
		HttpServletResponse res = (HttpServletResponse) a_externalContext.getNativeResponse();
		
		try
		{
			if(log.isDebugEnabled()) log.debug("OK, now calling CAS to logout...");
			authservice.handleLogoutRequest(IdmUtil.buildCasAuthenticationRequest(req));
		}catch(AuthenticationException ae)
		{
			log.warn("Strange: Got an error trying to logout from CAS:",ae);
		}
		
		cg.removeCookie(res);
		a_messageContext.addMessage(new MessageBuilder().error().source(null).code("logoutsuccessful").build());		
		req.getSession().invalidate();
	}
	
	public void setCookieGenerator(CookieRetrievingCookieGenerator a_cg)
	{
		cg = a_cg;
	}
	
}
