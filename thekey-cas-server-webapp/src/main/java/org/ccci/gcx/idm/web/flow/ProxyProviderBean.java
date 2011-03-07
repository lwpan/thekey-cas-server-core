package org.ccci.gcx.idm.web.flow;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationResponse;
import org.ccci.gcx.idm.core.service.AuthenticationService;
import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.IdmUtil;
import org.springframework.webflow.context.ExternalContext;


/**
 * Provides proxy tickets for SSO.
 * @author ken
 *
 */
public class ProxyProviderBean {

	protected static final Log log = LogFactory.getLog(ProxyProviderBean.class);

	//DI
	private AuthenticationService authservice;
	public void setAuthenticationService (AuthenticationService a_svc)
	{
		authservice = a_svc;
	}
	
	/**
	 * hands off the proxy request to CAS and returns the result. 
	 * 
	 * @param a_externalContext
	 * @return
	 * @throws Exception
	 */
	public boolean processProxyRequest(ExternalContext a_externalContext) throws Exception
	{
		HttpServletRequest request = (HttpServletRequest) a_externalContext.getNativeRequest();
        
		CasAuthenticationRequest casrequest = IdmUtil.buildCasAuthenticationRequest(request);
        
        CasAuthenticationResponse casresponse = (CasAuthenticationResponse) authservice.handleProxyRequest((casrequest));

        boolean result = false;
        
        if(!casresponse.isError())
        {
            result = true;
        }
        
        request.getSession().setAttribute(Constants.GLOBALMESSAGEPARAMETER, casresponse.getContent());
        return result;
        
	}
}
