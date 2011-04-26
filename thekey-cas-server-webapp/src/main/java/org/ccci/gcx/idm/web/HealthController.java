package org.ccci.gcx.idm.web;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.status.StatusBean;
import org.ccci.gcx.idm.web.status.StatusCollector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.webflow.context.ExternalContext;

public class HealthController implements Controller, ApplicationContextAware
{

	// DI - via ApplicationContextAware implementation
	private ApplicationContext context;
	private StatusCollector collector;
	
	protected static final Log log = LogFactory.getLog(HealthController.class);
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.info("admin user?: "+request.getSession().getAttribute(Constants.SESSIONATTRIBUTE_ADMIN));
		
		if(request.getSession().getAttribute(Constants.SESSIONATTRIBUTE_ADMIN)==null)
		{
			return null;
		}
		
		log.info("showing health for admin user: " + request.getSession().getAttribute(Constants.SESSIONATTRIBUTE_ADMIN));
		
		ArrayList<StatusBean> statii = collector.getAllStatusesFromContext(context);
		
		ModelAndView mv = new ModelAndView(Constants.VIEW_HEALTH);
		mv.addObject(Constants.MODEL_HEALTHSTATUSLIST,statii);
		
		return mv;
	}
	
	/**
	 * returns whether or not the sessionattribute_admin is present. it is only present
	 * if the user has logged in as a user in the administrator group
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isAdmin(ExternalContext context) throws Exception {
        HttpServletRequest request = (HttpServletRequest) context.getNativeRequest();
        boolean isadmin = false;
        
		log.info("admin user?: "+request.getSession().getAttribute(Constants.SESSIONATTRIBUTE_ADMIN));
        
		if(request.getSession().getAttribute(Constants.SESSIONATTRIBUTE_ADMIN)!=null)
		{
			isadmin = true;
		}
		return isadmin;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext)
	{
		context = applicationContext;
	}
	public void setStatusCollector(StatusCollector a_c)
	{
		collector = a_c;
	}
	
}
