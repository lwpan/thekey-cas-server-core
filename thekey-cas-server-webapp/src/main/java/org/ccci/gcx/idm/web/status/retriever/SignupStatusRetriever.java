package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;

import org.ccci.gcx.idm.web.SignupWizardController;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.springframework.context.ApplicationContext;

/**
 * Retrieves signup status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class SignupStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status from the signupcontroller and returns as a list of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		SignupWizardController lc = (SignupWizardController) getBeanFromContext(context);
		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();
		
		statii.add(new StatusBean("Signup hits","Number of hits to the Signup Controller",
				Integer.toString(lc.getHits())));
		statii.add(new StatusBean("Signup completions","Number of complete Signups",
				Integer.toString(lc.getSignups())));
		
		return statii;
	}

	
}
