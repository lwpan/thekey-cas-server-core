package org.ccci.gcx.idm.web.status.retriever;

import java.util.ArrayList;
import java.util.HashMap;

import org.ccci.gcx.idm.web.LanguageListBean;
import org.ccci.gcx.idm.web.status.AbstractStatusRetriever;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.springframework.context.ApplicationContext;

/**
 * Retrieves language list configuration status information.  English only.
 * Use DI to set the 
 * actual spring bean name of this status retriever
 * DI property = "TargetBeanName"
 * @author ken
 *
 */
public class LanguageListStatusRetriever extends AbstractStatusRetriever {

	//inherited abstract DI property "TargetBeanName"
	
	/**
	 * fetches the current status from the languagelist and returns as a list of statusbeans
	 */
	public ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context) {
		LanguageListBean llbean = (LanguageListBean) getBeanFromContext(context);

		ArrayList<StatusBean> statii = new ArrayList<StatusBean>();
		
		HashMap<String,String> langs = llbean.getLanguageList();
		
		statii.add(new StatusBean("Number of Languages","Total number of languages configured",new Integer(langs.size()).toString()));
		
		for(Object lang : llbean.getLanguageList().values())
		{
			statii.add(new StatusBean("Language","Available language translation",lang.toString()));
		}
		
		return statii;
	}

	
}
