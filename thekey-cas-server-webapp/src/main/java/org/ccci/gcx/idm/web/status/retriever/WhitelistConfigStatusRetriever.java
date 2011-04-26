package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;

import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.springframework.context.ApplicationContext;

/**
 * Retrieves whitelist configuration status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class WhitelistConfigStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status from the password configurator and returns as a list of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		XmlConfigurator pxc = (XmlConfigurator) getBeanFromContext(context);

		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();
		
		try
		{
			for(String s : pxc.getListAsString(Constants.CONFIGSERVERELEMENT))
			{
				statii.add(new StatusBean("Whitelisted Server","Service authorized to receive extended attributes",s));
			}
		}
		catch(Exception e)
		{
			statii.add(new StatusBean("Whitelisted Servers","Configuration error",e.getMessage()));
		}
		
		return statii;
	}

	
}
