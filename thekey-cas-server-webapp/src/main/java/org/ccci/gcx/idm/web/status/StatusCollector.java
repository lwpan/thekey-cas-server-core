package org.ccci.gcx.idm.web.status;

import java.util.ArrayList;

import org.springframework.context.ApplicationContext;

/**
 * Iterates its list of status retrievers and returns
 * the aggregated statii.
 * 
 * adding a new status retriever: write a bean that subclasses AbstractStatusRetriever
 * and then add it to the list of status retrievers DI provides to StatusCollector.
 * @author ken
 *
 */
public class StatusCollector {

	private ArrayList<AbstractStatusRetriever> retrievers;
	
	public void setStatusRetrievers(ArrayList<AbstractStatusRetriever> a_list)
	{
		retrievers = a_list; 
	}
	
	/**
	 * Iterates over the provided (via DI) status retrievers and returns an aggregated list
	 * of all status beans for all retrievers. This list can then be given as a model to a view
	 * and all status items can be displayed on a page.  
	 * @param context
	 * @return
	 */
	public ArrayList<StatusBean> getAllStatusesFromContext(ApplicationContext context)
	{
		ArrayList<StatusBean> allStatii = new ArrayList<StatusBean>();
		for(AbstractStatusRetriever r :retrievers)
		{
			allStatii.addAll(r.getStatusListFromContext(context));
		}
		return allStatii;
	}
	
	
}
