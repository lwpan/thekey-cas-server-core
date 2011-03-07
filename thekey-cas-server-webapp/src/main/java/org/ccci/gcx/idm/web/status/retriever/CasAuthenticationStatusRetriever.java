package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;

import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationRequest;
import org.ccci.gcx.idm.core.authentication.client.impl.CasAuthenticationResponse;
import org.ccci.gcx.idm.core.service.AuthenticationService;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.ccci.gcx.idm.web.Constants;

import org.springframework.context.ApplicationContext;

/**
 * Retrieves cas authentication status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class CasAuthenticationStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status of the userservice and returns as a list of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		AuthenticationService authservice = (AuthenticationService) getBeanFromContext(context);
		
		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();

		CasAuthenticationRequest casreq = new CasAuthenticationRequest();
		casreq.setPrincipal(Constants.HEALTH_USERSVC_TESTUSER);
		casreq.setCredential("JesusRules!");
		casreq.setService("http://www.mygcx.org");
		
		String authserviceresult = "No";
		
		try
		{
			CasAuthenticationResponse casresponse = 
		        	(CasAuthenticationResponse) authservice.handleLoginRequest(casreq);
			
			authserviceresult = "Yes";
		}catch(Exception e)
		{
			authserviceresult = e.getMessage();
		}
		
		statii.add(new StatusBean("CasAuthenticationService - login user","Can we connect to the CAS server?",
				authserviceresult ));
		
		return statii;
	}

	
}
