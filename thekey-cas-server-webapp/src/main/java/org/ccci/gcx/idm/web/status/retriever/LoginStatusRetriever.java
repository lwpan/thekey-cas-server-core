package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;

import org.ccci.gcx.idm.web.LoginFormController;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.springframework.context.ApplicationContext;

/**
 * Retrieves login status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class LoginStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status from the logincontroller and returns as a hashmap of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		LoginFormController lc = (LoginFormController) getBeanFromContext(context);
		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();
		
		statii.add(new StatusBean("Login hits","Number of hits to the Login Controller",
				Integer.toString(lc.getHits())));
		statii.add(new StatusBean("Login failures","Number of login failures",
				Integer.toString(lc.getFailedLogins())));
		statii.add(new StatusBean("Login successes","Number of successful logins",
				Integer.toString(lc.getSuccessfulLogins())));
		
		return statii;
	}

	
}
