package org.ccci.gcx.idm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.util.RandomGUID;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

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
	setPages(new String[] {
		"serviceSignupEmailView",
		"serviceSignupNameView",
		"serviceSignupSuccessView" });
		}

    /**
     * Once the user clicks "finish" the wizard calls processFinish. Creates
     * user using wizard gathered form data and returns to login page.
     */
    protected ModelAndView processFinish(final HttpServletRequest req,
	    final HttpServletResponse res, final Object command,
	    final BindException errors) throws Exception {
	final SimpleLoginUser form = (SimpleLoginUser) command;

	// generate a new GcxUser object
	final GcxUser user = new GcxUser();
	user.setGUID(RandomGUID.generateGuid(true));
	user.setEmail(form.getUsername());
	user.setFirstName(form.getFirstName());
	user.setLastName(form.getLastName());
	user.setPasswordAllowChange(true);
	user.setForcePasswordChange(true);
	user.setLoginDisabled(false);
	user.setVerified(false);
	// user.setPassword("randompassword");

	// create the new user in the GcxUserService
	if (logger.isInfoEnabled()) {
	    logger.info("***** User: " + user);
	    logger.info("***** Preparing to create through service");
	}
	gcxuserservice.createUser(user, "SignupWizardController");

	// return the signup success view
	return new ModelAndView("serviceSignupSuccessView");
    }

	protected ModelAndView processCancel(HttpServletRequest req,
			HttpServletResponse res, Object command, BindException errors)
			throws Exception {
		if(log.isDebugEnabled()) log.debug("cancel is selected!");
	res.sendRedirect("/login");
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
	response.sendRedirect("/login");
			return null;
	}
	
	
	//DI
	private GcxUserService gcxuserservice;
	public void setGcxUserService(GcxUserService a_svc)
	{
		gcxuserservice = a_svc;
	}

}
