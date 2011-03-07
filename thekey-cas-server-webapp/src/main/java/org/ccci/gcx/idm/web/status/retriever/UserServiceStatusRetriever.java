package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.ccci.gcx.idm.web.Constants;

import org.springframework.context.ApplicationContext;

/**
 * Retrieves userservice status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class UserServiceStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status of the userservice and returns as a list of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		GcxUserService gcxuserservice = (GcxUserService) getBeanFromContext(context);

		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();
		
		String userserviceresult = "No";
		String transitionalserviceresult = "No";
		GcxUser u = null;
		
		try
		{
			//see if we can grab our user
			u = gcxuserservice.findUserByEmail(Constants.HEALTH_USERSVC_TESTUSER);
			if(u != null)
				userserviceresult = "Yes";
		}
		catch(Exception e)
		{
			userserviceresult = e.getMessage();
		}
		
		try
		{	
			//see if we can grab a transitional user
			u = gcxuserservice.findTransitionalUserByEmail(Constants.HEALTH_USERSVC_TESTTRANSITIONALUSER);
			if(u!=null)
				transitionalserviceresult = "Yes";
		}
		catch(Exception e)
		{
			transitionalserviceresult = e.getMessage();
		}
		
		
		statii.add(new StatusBean("UserService - lookup user","Can we connect to the user repository?",
				userserviceresult ));
		statii.add(new StatusBean("UserService - lookup transitional user","Can we connect to transitional user repository?",
				transitionalserviceresult ));
		
		return statii;
	}

	
}
