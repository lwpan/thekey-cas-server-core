package org.ccci.gcx.idm.web.flow;

import org.springframework.webflow.context.ExternalContext;
import org.ccci.gcx.idm.web.Constants;

import javax.servlet.http.HttpServletResponse;

public class DynamicRedirector {

	
	public void redirectToService(ExternalContext context) throws Exception {
		String service = context.getSessionMap().getString(Constants.SESSIONATTRIBUTE_SERVICE);
		if(service != null )
		{
			((HttpServletResponse) context.getNativeResponse()).sendRedirect(service);
		}
		else
		{
			throw new Exception("Dynamic Redirector found no service to send.");
		}
		
	}

	
}
