package org.ccci.gcx.idm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gto.cas.validator.PasswordValidator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.WebUtils;

/**
 * Provides client javascript. Since it only changes on a configuration change, 
 * we can store the compiled string in a singleton value.  we'll also stuff it into each client session
 * for easy access from the jsps.
 * 
 * NOTE: Currently this is serving up only one javascript type, so if more come along, they should be
 *       served up from this controller (and refactoring of the pattern will be in order)
 * @author ken
 *
 */
public class JavascriptLibraryController implements Controller 
{
	protected static final Log log = LogFactory.getLog(JavascriptLibraryController.class);

	private PasswordValidator pwv;
	private String clientjavascript; //store our singleton value for quick access. 
	
	//DI
	public void setPasswordValidator(PasswordValidator a_pwv)
	{
		pwv = a_pwv;
	}
	
	/**
	 * handles a request for a javascript library that isn't a static page.
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if(log.isDebugEnabled()) log.debug("here we are");
		
	if (StringUtils.isEmpty(clientjavascript)) {
	    clientjavascript = pwv.getValidationJavascript();
	}
		
		//stuff the clientjavascript into the 
		if(StringUtils.isEmpty((String)WebUtils.getSessionAttribute(request,Constants.SESSIONATTRIBUTE_CLIENTJAVASCRIPT)))
		{
			if(log.isDebugEnabled()) log.debug("setting the client javascript");
			WebUtils.setSessionAttribute(request, Constants.SESSIONATTRIBUTE_CLIENTJAVASCRIPT, clientjavascript );
			//log.debug(pwv.getClientJavascript());
		}    
	     
	return new ModelAndView("javascriptPasswordValidation");
	}
	
	/*
	 * clears the cache.
	 */
	public void clearCache()
	{
		clientjavascript = null;
	}
}
