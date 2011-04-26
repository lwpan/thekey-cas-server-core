package org.ccci.gcx.idm.web;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gcx.idm.web.SimpleLoginUser;
import org.ccci.gcx.idm.web.validation.PasswordValidator;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

/**
 * Provides self-service use cases for the sso web application. 
 * Implemented as a Spring webflow; see selfserviceflow.xml for configuration for this controller.
 * @author ken
 *
 */
public class SelfServiceController {

	protected static final Log log = LogFactory.getLog(SelfServiceController.class);
	
	private GcxUserService gcxuserservice;
	private PasswordValidator pwv;


	/**
	 * triggers a resetPassword action for this user since they forgot their password.
	 * 
	 * @param user
	 * @return true if email is sent, false if it couldn't be sent because the mail wasn't found, etc.
	 * @throws Exception throws exception if no username was provided (which means spring validation broke)
	 */
	public boolean sendForgotPasswordEmail(SimpleLoginUser user,MessageContext context) throws Exception
	{
		if(StringUtils.isEmpty(user.getUsername()))
			throw new Exception("Email not provided! Validator didn't require a valid username.");

		try
		{
			GcxUser gcxuser = gcxuserservice.findUserByEmail(user.getUsername());
			gcxuserservice.resetPassword(gcxuser, Constants.SOURCEIDENTIFIER_FORGOTPASSWORD, user.getUsername());
		}
		catch (Exception e)
		{
			log.error("An exception ("+e.getMessage()+") occurred trying to find user ("
					+user.getUsername()+") and send a resetPassword message."+e.getMessage());

			context.addMessage(new MessageBuilder().error().source(null).code(Constants.ERROR_SENDFORGOTFAILED).build());

			return false;
		}
		
		return true;
	}
	
	//updates this user's details
	public boolean updateAccountDetails(SimpleLoginUser user,String auth_user,MessageContext context) throws Exception
	{
		if(log.isDebugEnabled())log.debug("AUTHENTICATED USER EMAIL = "+auth_user);

		boolean changepw = false;
		boolean changeun = false;
		String successmessage = Constants.ACCOUNT_UPDATESUCCESS;

		
		try
		{
			if(log.isDebugEnabled()) log.debug("updating account details for: " + auth_user);
			
			GcxUser gcxuser = gcxuserservice.findUserByEmail(auth_user);
			
			if(gcxuser == null)
			{
				context.addMessage(new MessageBuilder().error().source(null).code(Constants.ERROR_UPDATEFAILED).build());
				return false;
			}
			
			gcxuser.setFirstName(user.getFirstName());
			gcxuser.setLastName(user.getLastName());
			gcxuser.setEmail(user.getUsername());
			
			//see if they are changing their email address  if so, make sure the new one doesn't exist
			if(!StringUtils.equals(user.getUsername(),auth_user))
			{
				changeun = true;
				successmessage = Constants.ACCOUNT_UPDATESUCCESS_RESETPASSWORD;

				GcxUser newgcxuser = gcxuserservice.findUserByEmail(user.getUsername());
				
				if(newgcxuser != null)
				{
					log.error("An error occurred: email already exists ("+user.getUsername()+") for ("+auth_user);
					context.addMessage(new MessageBuilder().error().source(null).code(Constants.ERROR_UPDATEFAILED_EMAILEXISTS).build());
					return false;
				}
			}
			
			//just a quick password check here... we're assuming that our validator already made sure we were valid.
			if(StringUtils.isNotBlank(user.getPassword()))
			{
				if(user.getPassword().equals(user.getRetypePassword()))
				{
					if(gcxuser.isPasswordAllowChange())
					{
						if(log.isDebugEnabled())log.debug("Setting User's ("+user.getUsername()+") password to a new value.");
						gcxuser.setPassword(user.getPassword());
						changepw = true;
					}
					else
						if(log.isDebugEnabled())log.debug("User ("+user.getUsername()+") requested password change but isn't allowed.");
				}		
				else
					if(log.isDebugEnabled())log.debug("User ("+user.getUsername()+") requested password change but didn't retype correctly.");
			}
			else
				if(log.isDebugEnabled())log.debug("User ("+user.getUsername()+") didn't request password change");
	
			log.info("Update user ("+user.getUsername()+") by "+auth_user);

			//UPDATE THE USER
			gcxuserservice.updateUser(gcxuser, changepw, Constants.SOURCEIDENTIFIERUSERUPDATE, auth_user);
			
			//if they changed their username, reset their password.
			if(changeun)
			{
				//send a new password.
				if(log.isDebugEnabled()) log.debug("changed username so reset password.");
				gcxuserservice.resetPassword(gcxuser, Constants.SOURCEIDENTIFIERUSERUPDATE, auth_user);
			}
		}
		catch (Exception e)
		{
			log.error("An error occurred when trying to update user ("+user.getUsername()+") by "+auth_user+" :"+e.getMessage(),e);
			context.addMessage(new MessageBuilder().error().source(null).code(Constants.ERROR_UPDATEFAILED).build());
			return false;
		}
		
		//return an appropriate success message
		context.addMessage(new MessageBuilder().error().source(null).code(successmessage).build());
		return true;
		
	}

	public ArrayList<GcxUser> findUsers(SimpleLoginUser user)
	{
		ArrayList<GcxUser> users = new ArrayList<GcxUser>();
		HashSet<String> useremail = new HashSet<String>();

		try
		{
			if(StringUtils.isNotBlank(user.getFirstName())) {
				for(GcxUser u: gcxuserservice.findAllByFirstName(user.getFirstName())) {
					if(!useremail.contains(u.getEmail())) {
						useremail.add(u.getEmail());
						users.add(u);
					}
				}
			}

			if(StringUtils.isNotBlank(user.getLastName())) {
				for(GcxUser u: gcxuserservice.findAllByLastName(user.getLastName())) {
					if(!useremail.contains(u.getEmail())) {
						useremail.add(u.getEmail());
						users.add(u);
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn("findUsers exception.",e);
		}
		if(log.isDebugEnabled()) log.debug("************* RETURNING "+users.size());
		return users;
	}

	public String getTargetViewState(String destination)
	{
		if(destination == null)
		{
			destination = defaultViewState;
		}
		if(log.isDebugEnabled()) log.debug("RETURNING: "+destination+" AS our view!");
		return destination;
	}
	
	public SimpleLoginUser newUserModel()
	{
		return new SimpleLoginUser();
	}
	
	
    //via DI in Spring

    private String defaultViewState;
    public void setDefaultViewState(String a_str){
    	defaultViewState = a_str;
    }
  
    public void setPasswordValidator(PasswordValidator a_pwv)
    {
    	pwv = a_pwv;
    }
    
    public void setGcxUserService(GcxUserService a_svc)
    {
    	this.gcxuserservice = a_svc;
    }
    
    
}
