package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;

import org.ccci.gcx.idm.web.Constants;
import org.ccci.gcx.idm.web.config.XmlConfigurator;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.springframework.context.ApplicationContext;

/**
 * Retrieves password configuration status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class PasswordConfigStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status from the password configurator and returns as a list of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		XmlConfigurator pxc = (XmlConfigurator) getBeanFromContext(context);

		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();
		
		try
		{
			statii.add(new StatusBean("Password - Minimum length","Password enforcement rule: MinLength",
					pxc.getElementValue(Constants.CONFIGPASSWORD_MINLENGTH)));
			statii.add(new StatusBean("Password - Maximum length","Password enforcement rule: MaxLength",
					pxc.getElementValue(Constants.CONFIGPASSWORD_MAXLENGTH)));
			statii.add(new StatusBean("Password - lower case required?","Password enforcement rule: HaveLowerCase",
					pxc.getElementValue(Constants.CONFIGPASSWORD_HAVELOWERCASE)));
			statii.add(new StatusBean("Password - upper case required?","Password enforcement rule: HaveUpperCase",
					pxc.getElementValue(Constants.CONFIGPASSWORD_HAVEUPPERCASE)));
			statii.add(new StatusBean("Password - symbol required?","Password enforcement rule: HaveSymbol",
					pxc.getElementValue(Constants.CONFIGPASSWORD_HAVESYMBOL)));
			statii.add(new StatusBean("Password - number required?","Password enforcement rule: HaveNumber",
					pxc.getElementValue(Constants.CONFIGPASSWORD_HAVENUMBER)));
			statii.add(new StatusBean("Password - minimum mix of types","Password enforcement rule: MinMix",
					pxc.getElementValue(Constants.CONFIGPASSWORD_HAVEMINMIX)));
		}
		catch(Exception e)
		{
			statii.add(new StatusBean("Password Configuration","Configuration error",e.getMessage()));
		}
		
		return statii;
	}

	
}
