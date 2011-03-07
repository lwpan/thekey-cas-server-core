package org.ccci.gcx.idm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.util.WebUtils;
import org.springframework.validation.BindException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.SimpleLoginUser;

/**
 * ForcePasswordChangeFormController - Handles the force a user to change their password use case.
 * 
 * 
 * @author ken
 *
 */

public class ForcePasswordChangeFormController extends SimpleFormController 
{
			
	private GcxUserService gcxuserservice;

	protected static final Log log = LogFactory.getLog(ForcePasswordChangeFormController.class);

	public ForcePasswordChangeFormController()
	{
		setCommandClass(SimpleLoginUser.class);
		setCommandName("user");
	}
	
	protected Object formBackingObject (HttpServletRequest request) throws Exception {
		//SimpleLoginUser loginFormUser = (SimpleLoginUser) super.formBackingObject(request);
		
		//This should be our previously logged in user.
		SimpleLoginUser loggedInUser = (SimpleLoginUser) WebUtils.getSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOGINUSER);
		if(loggedInUser == null || !loggedInUser.isAuthenticated())
		{
			throw new Exception("Force Change Password has an invalid state (no authenticated user)");
		}
		
		return loggedInUser;
	}
	
	/**
	 * Once a user hits submit on the ForcePasswordChange form...
	 */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors)
     throws Exception
	{
		
		//our form values bound to our simple user model object
		SimpleLoginUser form = (SimpleLoginUser) command;
		
		SimpleLoginUser loggedInUser = (SimpleLoginUser) WebUtils.getSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOGINUSER);
		
		if(!loggedInUser.isAuthenticated())
		{
			throw new Exception("Force Change Password has an invalid state (no authenticated user)");
		}
		
		if(form.getPassword() == null) throw new Exception("SPRING configuration error. Validation must not have occurred because your password was null.");
		
        try
        {
        	if(log.isDebugEnabled()) log.debug("Looking up user: "+ loggedInUser.getUsername());
        	GcxUser user = gcxuserservice.findUserByEmail(loggedInUser.getUsername());
        	if(user==null) throw new Exception("User not found:"+loggedInUser.getUsername());
        	
    		if(log.isDebugEnabled()) log.debug("Found a user via userservice: "+user.getGUID());
        	
        	if(user.isForcePasswordChange())
        	{
        		if(log.isDebugEnabled()) log.debug("Resetting password.");
        		
        		user.setPassword(form.getPassword());
        		user.setForcePasswordChange(false);
        		
        		gcxuserservice.updateUser(user, true, Constants.SOURCEIDENTIFIER_FORCECHANGEPASSWORD, loggedInUser.getUsername());

        		if(log.isDebugEnabled()) log.debug("Looks like it was a success... now force a relogin (with the new password)");
        		
        		WebUtils.setSessionAttribute(request, Constants.SESSIONATTRIBUTE_LOGIN, loggedInUser.getUsername()); //set this guy for the logincontroller

        		return new ModelAndView("redirect:login.htm"); //done, let them login now with their new password.
        	}else
        		log.debug("USER PASSWORD isn't forced to change! so we're not changing it.");
        	
        }
        catch(Exception al)
        {
        	log.error("Exception occurred when trying to ForceChangePassword for "+form.getUsername(),al);
    		errors.rejectValue(null, Constants.ERROR_CHANGEPASSWORDFAILED);
        }
        
		return this.showForm(request, errors, Constants.VIEW_FORCECHANGEPASSWORD);
	}
	
	protected ModelAndView processFormSubmission(HttpServletRequest request,
            HttpServletResponse response,
            Object command,
            BindException errors) throws Exception
	{
		//handle cancel...
		if(request.getParameter(Constants.REQUESTPARAMETER_CANCEL) != null)
		{
			response.sendRedirect(Constants.VIEW_LOGIN);
			return null;
		}
		return super.processFormSubmission(request, response, command, errors);
	}
	
	
	//DI
	public void setGcxUserService(GcxUserService a_svc)
	{
		this.gcxuserservice=a_svc;
	}

	 

}
