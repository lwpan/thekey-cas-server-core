package org.ccci.gto.cas.admin.action.edir;

import static org.ccci.gcx.idm.web.admin.Constants.ACTION_ACTIVATE;
import static org.ccci.gcx.idm.web.admin.Constants.ACTION_APPLY;
import static org.ccci.gcx.idm.web.admin.Constants.ACTION_CANCEL;
import static org.ccci.gcx.idm.web.admin.Constants.ACTION_DEACTIVATE;
import static org.ccci.gcx.idm.web.admin.Constants.ACTION_MERGE_SEARCH;
import static org.ccci.gcx.idm.web.admin.Constants.ACTION_RESET_PASSWORD;
import static org.ccci.gcx.idm.web.admin.Constants.ACTION_SAVE;
import static org.ccci.gcx.idm.web.admin.Constants.SESSION_SELECTED_USER;
import static org.ccci.gcx.idm.web.admin.Constants.SESSION_STATUS_MESSAGE;
import static org.ccci.gcx.idm.web.admin.Constants.SESSION_USER_BEING_UPDATED;
import static org.ccci.gcx.idm.web.admin.Constants.SESSION_WORKFLOW_FLAG;
import static org.ccci.gcx.idm.web.admin.Constants.WORKFLOW_FLAG_RETURN_TO_PREVIOUS;

import me.thekey.cas.service.UserAlreadyExistsException;
import me.thekey.cas.service.UserNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.admin.action.AbstractUserUpdateAction;
import org.ccci.gto.cas.admin.response.impl.FilteredUserSearchResponse;

import java.util.Map;

/**
 * <b>EdirUserUpdateAction</b> is used to update an existing user.
 *
 * @author Greg Crider  Nov 20, 2008  6:50:05 PM
 */
public class EdirUserUpdateAction extends AbstractUserUpdateAction
{
    private static final long serialVersionUID = -6657838616068036125L ;

    /** Action taken by user */
    private String m_UpdateAction = ACTION_APPLY;

    /**
     * @return the updateAction
     */
    public String getUpdateAction()
    {
        return this.m_UpdateAction ;
    }
    /**
     * @param a_updateAction the updateAction to set
     */
    public void setUpdateAction( String a_updateAction )
    {
        this.m_UpdateAction = a_updateAction ;
    }
    
    
    /**
     * Return a complete copy of the {@link GcxUser} being updated. Take the backup copy from the session,
     * clone it, and then transfer just those attributes that can be updated within the view.
     * 
     * @return Effective {@link GcxUser} that was submitted by admin.
     */
    private GcxUser submittedGcxUser()
    {
	GcxUser result = (GcxUser) ((GcxUser) this.getSession().get(
		SESSION_USER_BEING_UPDATED)).clone();
        
        /*
         * Notes:
         * 
         * - E-mail is not being changed by the admin; he is only updating the userid
         */
	final GcxUser user = this.getModel();
        if ( !result.isDeactivated() ) {
	    result.setEmail(user.getUserid());
        }
	result.setUserid(user.getUserid());
	result.setFirstName(user.getFirstName());
	result.setLastName(user.getLastName());
        result.setDomainsVisited(user.getDomainsVisited());
        result.setGUIDAdditional(user.getGUIDAdditional());
        result.setDomainsVisitedAdditional(user.getDomainsVisitedAdditional());
        result.setPasswordAllowChange(user.isPasswordAllowChange());
        result.setForcePasswordChange(user.isForcePasswordChange());
        result.setLoginDisabled(user.isLoginDisabled());
        result.setLocked(user.isLocked());

        return result ;
    }
    
    
    /**
     * Validate the request to update a user.
     * 
     * @param a_GcxUser User to be updated.
     * 
     * @return <tt>True</tt> if the request is valid.
     */
    private boolean isValidUpdateRequest( GcxUser a_GcxUser )
    {
        boolean result = true ;
        
        if ( StringUtils.isBlank( a_GcxUser.getFirstName() ) ) {
            this.addFieldError( "gcxUser.firstName", this.getText( "edir.error.update.firstname" ) ) ;
            result = false ;
        }
        if ( StringUtils.isBlank( a_GcxUser.getLastName() ) ) {
            this.addFieldError( "gcxUser.lastName", this.getText( "edir.error.update.lastname" ) ) ;
            result = false ;
        }
        if ( StringUtils.isBlank( a_GcxUser.getUserid() ) ) {
            this.addFieldError( "gcxUser.userid", this.getText( "edir.error.update.email" ) ) ;
            result = false ;
        }
        
        // Test to see if the e-mail address changed; if it did, make sure it's not already in use
	GcxUser previousUser = (GcxUser) this.getSession().get(
		SESSION_USER_BEING_UPDATED);
        if ( !previousUser.getUserid().equals( a_GcxUser.getUserid() ) ) {
	    if (this.getUserService().findUserByEmail(a_GcxUser.getUserid()) != null) {
                this.addFieldError( "gcxUser.userid", this.getText( "edir.error.update.emailexists" ) ) ;
                // Restore the original userid
                a_GcxUser.setUserid( previousUser.getUserid() ) ;
                result = false ;
            }
        }
        
        return result ;
    }
    
    
    /**
     * Reset the user being updated in the view. This is called if you left the user detail view
     * in order to do something like a merge search, and are now returning back to the user detail
     * view.
     * 
     * @return Result name.
     */
    public String restoreUserInput()
    {
        // Inject the last known version of the user being updated back into the model
	this.setModel((GcxUser) this.getSession().get(
		SESSION_USER_BEING_UPDATED));
        
	return SUCCESS;
    }


    /**
     * Initialize the input for the view.
     * 
     * @return Result name.
     */
    public String updateUserInput()
    {
        // Put the selected user in the model object
	final Map<String, Object> session = this.getSession();
	final GcxUser user = (GcxUser) session.get(SESSION_SELECTED_USER);
	this.setModel(user);
        
        // Keep a copy of the user in the session for update actions; this will prevent a call back to
        // the LDAP server to get an original copy. This is necessary because we don't want to have a
        // lot of hidden variables holding the password, etc. in the view. We are going to make a deep
        // clone copy of the object because if it is the same object as what's in the modelObject()
        // property it's just a pointer; we need a clean copy that won't be altered.
	session.put(SESSION_USER_BEING_UPDATED, user.clone());
        
        // Remove the selected user from the session (because it may be reused by merge search)
        //this.getSession().remove( Constants.SESSION_SELECTED_USER ) ;
        
	if (log.isDebugEnabled()) {
	    log.debug("***** Selected User now in ModelObject: " + user);
	}
        
	return SUCCESS;
    }
    
    
    /**
     * Handle requests to update the user details.
     * 
     * @return Result name.
     * @throws me.thekey.cas.service.UserNotFoundException
     */
    public String updateUser() throws UserNotFoundException {
	final Map<String, Object> session = this.getSession();
        String result = EdirUserUpdateAction.SUCCESS ;
	final GcxUser authenticatedUser = this.getAuthenticatedUser();
        
        // Generate the submitted version of the user
        GcxUser submittedUser = this.submittedGcxUser() ;
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Submitted user: " + submittedUser ) ;
        
        // ACTION: Apply & Save
	final String action = this.getUpdateAction();
	if (action.equals(ACTION_APPLY) || action.equals(ACTION_SAVE)) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Apply/Save changes to user" ) ;
            if ( this.isValidUpdateRequest( submittedUser ) ) {
                this.getUserService().updateUser(submittedUser);
		session.put(SESSION_USER_BEING_UPDATED,
			submittedUser.clone());
		if (action.equals(ACTION_APPLY)) {
		    session.put(SESSION_STATUS_MESSAGE,
			    "The requested changes to the user details have been saved.");
		    result = ACTION_APPLY;
                } else {
		    result = SUCCESS;
                }
            } else {
		result = ERROR;
            }
        // ACTION: Deactivate
	} else if (action.equals(ACTION_DEACTIVATE)) {
            // Since we are deactivating, we drop any changes made by admin
	    submittedUser = (GcxUser) session.get(SESSION_USER_BEING_UPDATED);
	    log.debug("***** Deactivating user");
	    if (log.isTraceEnabled()) {
		log.trace("***** User input: " + this.getModel());
	    }
            // Deactivate the user
	    this.getUserService().deactivateUser(submittedUser,
		    this.getApplicationSource(), authenticatedUser.getEmail());
            // Create a cloned copy of this deactivated in case it needs to be updated in the view
	    session.put(SESSION_USER_BEING_UPDATED, submittedUser.clone());
	    session.put(SESSION_STATUS_MESSAGE,
		    "The user has been deactivated.");
	    result = ACTION_DEACTIVATE;
        // ACTION: Activate
	} else if (action.equals(ACTION_ACTIVATE)) {
            // Since we are activating, we drop any changes made by admin
	    submittedUser = (GcxUser) session.get(SESSION_USER_BEING_UPDATED);
	    log.debug("***** Activating user");
	    if (log.isTraceEnabled()) {
		log.trace("***** User input: " + this.getModel());
	    }
            try {
                // Activate the user
		this.getUserService().reactivateUser(submittedUser,
			this.getApplicationSource(),
			authenticatedUser.getEmail());
                // Create a cloned copy of this activated in case it needs to be updated in the view
		session.put(SESSION_USER_BEING_UPDATED, submittedUser.clone());
		result = ACTION_ACTIVATE;
		session.put(SESSION_STATUS_MESSAGE,
			"The user has been activated.");
            } catch ( UserAlreadyExistsException guaee ) {
                this.addActionError( this.getText( "edir.error.update.cantactivate", new String[] { submittedUser.getUserid() } ) ) ;
                result = EdirUserUpdateAction.ERROR ;
            }
        // ACTION: Reset Password
	} else if (action.equals(ACTION_RESET_PASSWORD)) {
            // Since we are resetting the password, we drop any changes made by admin
	    submittedUser = (GcxUser) session.get(SESSION_USER_BEING_UPDATED);
	    log.debug("***** Resetting password for user");
	    if (log.isTraceEnabled()) {
		log.trace("***** User input: " + this.getModel());
	    }
	    session.put(
		    SESSION_STATUS_MESSAGE,
		    "The user's password has been reset, and an e-mail notification has been sent out.");
	    result = ACTION_RESET_PASSWORD;
        // ACTION: Merge Search
	} else if (action.equals(ACTION_MERGE_SEARCH)) {
            // Since we are doing a merge search, we drop any changes made by admin
	    submittedUser = (GcxUser) session.get(SESSION_USER_BEING_UPDATED);
            // Since we are going into another search, make sure we don't show the current user in the results
	    session.put(
		    FilteredUserSearchResponse.SESSION_FILTERED_USER_OBJECT,
		    submittedUser);
	    result = ACTION_MERGE_SEARCH;
        // ACTION: Cancel
        } else {
	    log.debug("***** Cancel user update");

            // Since we are cancelling, make sure we don't accept any last minute changes to the user
	    submittedUser = (GcxUser) session.get(SESSION_USER_BEING_UPDATED);
	    result = ACTION_CANCEL;
        }
        
        // Put the submitted version of the user back in the model object in case we are returning to the view
	this.setModel(submittedUser);
        
        // Upon completion return to the previous workflow
	session.put(SESSION_WORKFLOW_FLAG, WORKFLOW_FLAG_RETURN_TO_PREVIOUS);
        // Put the user back in the session in case it was changed
	session.put(SESSION_SELECTED_USER, submittedUser);
        
        return result ;
    }
}
