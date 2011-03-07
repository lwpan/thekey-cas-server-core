package org.ccci.gcx.idm.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.springframework.web.util.WebUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser; //use once we pass validation to accomplish signup
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.core.util.RandomGUID;

/**
 * Provides signup wizard use case of sso web application
 * @author ken
 *
 */
public class SignupWizardController extends AbstractWizardFormController {

	private int hits=0;
	private int signups = 0;
	
	protected static final Log log = LogFactory.getLog(SignupWizardController.class);

	public int getHits()
	{
		return hits;
	}
	
	public int getSignups()
	{
		return signups;
	}
	
	
	//define our command object.
	public SignupWizardController() {

		setCommandClass(SimpleLoginUser.class);
		setCommandName("user");
		//pages in our wizard: reference according to int id (0,1,2,3)
		setPages(new String[]{
				"SignUp_Email",
				"SignUp_Name",
				"SignUp_Success"});
		}
		

	
	//setup our command object
	protected Object formBackingObject(HttpServletRequest request) throws Exception{

		// * From what I can tell from the docs
		 //*  formBackingObject() should only be called ONCE and the FBO session should be happening automatically... but...

		SimpleLoginUser user = (SimpleLoginUser) WebUtils.getSessionAttribute(request, "FBO_user");
		
		if(user == null)
		{
			user = new SimpleLoginUser();
			WebUtils.setSessionAttribute(request, "FBO_user", user);
		}
		hits++;
		return user;

	}

	/**
	 * Once the user clicks "finish" the wizard calls processFinish. Creates user using 
	 * wizard gathered form data and returns to login page.
	 */
	protected ModelAndView processFinish(HttpServletRequest req,
			HttpServletResponse res, Object command, BindException errors)
			throws Exception {

			SimpleLoginUser form = (SimpleLoginUser) command;
		
	        Date currentDate = new Date() ;
	        RandomGUID guid = new RandomGUID( true ) ;

	        GcxUser user = new GcxUser() ;
	        
	        user.setCreateDate( currentDate ) ;
	        user.setEmail( form.getUsername() ) ;
	        user.setGUID( guid ) ;
	        //user.setPassword( "changeme" ) ; JIRA: IDM-11 - gcxuserservice now provides a random password.
	        user.setFirstName( form.getFirstName() ) ;
	        user.setLastName( form.getLastName() ) ;
	        user.setForcePasswordChange( true ) ;
	        user.setLocked( false ) ;
	        user.setLoginDisabled( false ) ;
	        user.setLoginTime( null ) ;
	        user.setPasswordAllowChange( true ) ;
	        user.setDomainsVisitedString("");
	        user.setDomainsVisitedAdditionalString("");
	        user.setGUIDAdditionalString("");
	        
	        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** User: " + user ) ;
	        /*= INFO =*/ if ( log.isInfoEnabled() ) log.info( "***** Preparing to create through service" ) ;
	        
	        gcxuserservice.createTransitionalUser( user, "SignupWizardController");
		

		req.getSession().removeAttribute("FBO_user"); //take out our fbo object
		WebUtils.setSessionAttribute(req, Constants.SESSIONATTRIBUTE_LOGIN, form.getUsername()); //set this guy for the logincontroller

		signups++;
		return new ModelAndView("SignUp_Success");
		
	}
	
	protected ModelAndView processCancel(HttpServletRequest req,
			HttpServletResponse res, Object command, BindException errors)
			throws Exception {
		if(log.isDebugEnabled()) log.debug("cancel is selected!");
		req.getSession().removeAttribute("FBO_user");
		res.sendRedirect("login.htm");
		return null;
	}

	protected void validatePage(Object command, Errors errors, int page)
	{
		SimpleLoginUser user = (SimpleLoginUser) command;
		SimpleLoginUserValidator validator = (SimpleLoginUserValidator) getValidator();
		if(log.isDebugEnabled()) log.debug("****** Validating for page: "+page + ". so far errors = "+errors.getErrorCount());
		//based on the page we're on in the wizard, validate the one just posted.
		switch (page)
		{
			case 0:
				validator.validateUsername(user, errors);
				break;

			case 1:
				validator.validateName(user, errors);
				break;
		}
		if(log.isDebugEnabled()) log.debug("Number of validation errors: "+errors.getErrorCount());
		
		
	}
	
	protected ModelAndView handleInvalidSubmit(HttpServletRequest request,
            HttpServletResponse response) throws Exception
	{
			request.getSession().removeAttribute("FBO_user");
			response.sendRedirect(Constants.VIEW_LOGIN);
			return null;
	}
	
	
	//DI
	private GcxUserService gcxuserservice;
	public void setGcxUserService(GcxUserService a_svc)
	{
		gcxuserservice = a_svc;
	}

}
