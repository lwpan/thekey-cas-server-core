package org.ccci.gcx.idm.web.status;

import java.util.ArrayList;
import org.springframework.context.ApplicationContext;


/**
 * A status retriever should target a specific spring bean and
 * be able to somehow figure out the status and statistics of that bean
 * and then return a map of statusbean items that describe the status.
 * @author ken
 *
 */
public abstract class AbstractStatusRetriever 
{
	// likely set by DI or extending bean.
	private String beanname;
	
	/**
	 * returns the spring bean name for this retriever
	 * @return
	 */
	public String getTargetBeanName()
	{
		return beanname;
	}
	
	/**
	 * sets the spring bean name for this retriever (usually by DI)
	 * @param a_bean
	 */
	public void setTargetBeanName(String a_bean)
	{
		beanname = a_bean;
	}

	/**
	 * returns the bean from the context for this retriever. Utility.
	 * @param context
	 * @return
	 */
	public Object getBeanFromContext(ApplicationContext context)
	{
		return context.getBean(getTargetBeanName());
	}
	
	/**
	 * Get status items for the named bean from the given context.
	 * @return a ArrayList of (StatusBean) of status items 
	 */
	public abstract ArrayList<StatusBean> getStatusListFromContext(ApplicationContext context);

}
