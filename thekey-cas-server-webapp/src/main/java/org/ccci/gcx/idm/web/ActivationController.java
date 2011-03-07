package org.ccci.gcx.idm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.WebUtils;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;

/**
 * ActivationController - Activates a user. Recognized incoming parameters are:
 *    u=ken@burcham.com
 *    k=temporary password
 *    
 * After authenticating with username/temporary password, return ForceChangePassword view.
 * 
 * @author ken
 *
 */

public class ActivationController implements Controller 
{
			
	private GcxUserService gcxuserservice;

	protected static final Log log = LogFactory.getLog(ActivationController.class);

	/**
	 * handles an activation request. if the username and pin are valid, activates the user
	 * and returns the forcepasswordchange view.
	 * note: if the user is already activated but the temp password is still valid,
	 *       we return them to the forcepassword change page.
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		String username = request.getParameter(Constants.ACTIVATIONPARAMETER_USERNAME);
		String pin      = request.getParameter(Constants.ACTIVATIONPARAMETER_PIN);
		
		//probably need a more sophisticated check here...
		if(StringUtils.isEmpty(username) || username.length() > Constants.USERNAME_MAXLENGTH)
			throw new Exception("There was a problem with your username.  Please check your email.");
		
		if(StringUtils.isEmpty(pin) || pin.length() > Constants.PIN_MAXLENGTH)
			throw new Exception("There was a problem with your temporary password.  Please check your email.");
		
		if(log.isDebugEnabled()) log.debug("Trying to activate username: "+username);
		
		GcxUser tsUser = gcxuserservice.findTransitionalUserByEmail(username);
		GcxUser activatedUser = null;
		
		//prepare form object for change password.
		SimpleLoginUser form = new SimpleLoginUser();
		
		if(tsUser == null)
		{
			log.warn("Regular user could not be found:"+username);
			
			//lets see if they have already activated...  if so, send them to force change password.
			activatedUser = gcxuserservice.findUserByEmail(username);
			
			if(activatedUser == null)
			{	
				//not activated and we can't find them, so return an error...
				return errorModelAndView(Constants.ERROR_ACTIVATIONFAILED, username);
			}
			
			//are an activated user but they are trying to activate again... see if temporary password
			// is still the user password. This could happen if something happened after activation
			// but before the user had changed their password... in which case we want them to try again.
			try
			{
				if(log.isDebugEnabled()) log.debug("Trying to authenticate previously activated user with the given pin");
				activatedUser.setPassword(pin);
				gcxuserservice.authenticate(activatedUser);
			}catch(Exception e)
			{
				log.warn("Could not authenticate a user that was previously activated: "+username);
				//if there is an exception, we'll return an error.
				return errorModelAndView(Constants.ERROR_ACTIVATIONFAILED, username);
			}
			
			
		}
		
		//can arrive here with either a tsUser OR an activatedUser
		if(tsUser != null)
    	{
    		if(log.isDebugEnabled())log.debug("Transitional user located, lets activate them.");
    		
    		//does the entered password match the temporary password issued?
    		if(tsUser.getPassword().toUpperCase().equals(pin.toUpperCase()))
    		{
    			//activate them
    			log.info("Activating transitional user:"+username);
    			gcxuserservice.activateTransitionalUser(tsUser, Constants.SOURCEIDENTIFIER_ACTIVATION, tsUser.getEmail());
    			
    		}
    		else
    		{
    			log.error("Activation error: credentials are invalid: "+username);
    			return errorModelAndView(Constants.ERROR_ACTIVATIONFAILED, username);
    		}
    	}

		if(activatedUser == null)
		{
			//Now look up their new user object and force them to change their password.
			activatedUser = gcxuserservice.findUserByEmail(username);
		}
		
		if(activatedUser == null)
		{
			log.error("Activation error: could not recover user after activation: "+username);
			return errorModelAndView(Constants.ERROR_ACTIVATIONFAILED, username);
		} 
		
		log.info("Activation SUCCESS now FORCE PASSWORD CHANGE : "+username);

		form.setUsername(username);
		form.setAuthenticated(true);
		form.setFirstName(activatedUser.getFirstName());
		form.setLastName(activatedUser.getLastName());
		WebUtils.setSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOGINUSER , form);

		
		return new ModelAndView(Constants.VIEW_FORCECHANGEPASSWORD,Constants.MODEL_LOGINUSER,form);

	}
	 
	
	/**
	 * Utility method: give me the error and the username and I'll return you to the login page all populated like
	 * @param a_error
	 * @param a_username
	 * @return
	 */
	private ModelAndView errorModelAndView(String a_error, String a_username)
	{
		SimpleLoginUser user = new SimpleLoginUser();
		user.setUsername(a_username);
		ModelAndView mv = new ModelAndView(Constants.VIEW_MESSAGE,
											Constants.MODEL_LOGINUSER, 
											user);
		mv.addObject(Constants.MESSAGE_VIEW_TITLE,Constants.MESSAGE_VIEW_TITLE_CODE);
		mv.addObject(Constants.MESSAGE_VIEW_NOTICE,Constants.MESSAGE_VIEW_NOTICE_CODE);
		mv.addObject(Constants.MESSAGE_VIEW_MESSAGE,a_error);
		log.error("Activation returning this: "+a_error+" for user: "+a_username);
		return mv;
	}
	
	
	//DI
	public void setGcxUserService(GcxUserService a_svc)
	{
		this.gcxuserservice=a_svc;
	}


}
