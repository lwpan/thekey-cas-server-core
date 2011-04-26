package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;

import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.springframework.context.ApplicationContext;

/**
 * Retrieves redlist configuration status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class RedlistConfigStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status from the targetbeanname configurator and returns as a list of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		XmlConfigurator pxc = (XmlConfigurator) getBeanFromContext(context); //uses targetbeanname

		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();
		
		try
		{
			for(String s : pxc.getListAsString(Constants.CONFIGSERVERELEMENT))
			{
				statii.add(new StatusBean("Redlisted Server","(Grandfathered) service authorized to receive extended login header attributes",s));
			}
		}
		catch(Exception e)
		{
			statii.add(new StatusBean("Redlisted Servers","Configuration error",e.getMessage()));
		}
		
		return statii;
	}

	
}
