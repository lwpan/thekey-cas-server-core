package org.ccci.gto.cas.admin.action.edir;

import me.thekey.cas.service.UserNotFoundException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.admin.Constants;
import org.ccci.gto.cas.admin.action.AbstractUserSearchAction.SearchControlParameters;
import org.ccci.gto.cas.admin.action.AbstractUserUpdateAction;

/**
 * <b>EdirMergeUpdateAction</b> is used to perform the final merge operation
 *
 * @author Greg Crider  Dec 5, 2008  11:54:55 PM
 */
public class EdirMergeUpdateAction extends AbstractUserUpdateAction
{
    private static final long serialVersionUID = -4706959870099371158L ;

    /** Action taken by user */
    private String m_MergeAction = Constants.ACTION_MERGE ;
    /** 
     * Name of the session object that is holding the search control parameters from the
     * initial user search.
     */
    private String m_UserSearchControlParametersName = null ;

    
    /**
     * @return the mergeAction
     */
    public String getMergeAction()
    {
        return this.m_MergeAction ;
    }
    /**
     * @param a_mergeAction the mergeAction to set
     */
    public void setMergeAction( String a_mergeAction )
    {
        this.m_MergeAction = a_mergeAction ;
    }

    
    /**
     * @return the userSearchControlParametersName
     */
    public String getUserSearchControlParametersName()
    {
        return this.m_UserSearchControlParametersName ;
    }
    /**
     * @param a_userSearchControlParametersName the userSearchControlParametersName to set
     */
    public void setUserSearchControlParametersName( String a_userSearchControlParametersName )
    {
        this.m_UserSearchControlParametersName = a_userSearchControlParametersName ;
    }

    /**
     * Initialize the input for the view.
     * 
     * @return Result name.
     */
    public String updateMergeInput()
    {
	// Recover the selected user from merge search lookup
	this.setModel((GcxUser) this.getSession().get(
		Constants.SESSION_SELECTED_USER));
        
	if (log.isDebugEnabled()) {
	    log.debug("***** User being merged: " + this.getModel());
	}
        
	return SUCCESS;
    }
    
    
    /**
     * Handle requests to update the final merge.
     * 
     * @return Result name.
     * @throws me.thekey.cas.service.UserNotFoundException
     */
    public String updateMerge() throws UserNotFoundException {
        // ACTION: Merge
        if ( this.getMergeAction().equals( Constants.ACTION_MERGE ) ) {
	    log.debug("***** Performing final merge update");

            // Recover the primary user being merged into
            GcxUser primaryUser = (GcxUser)this.getSession().get( Constants.SESSION_USER_BEING_UPDATED ) ;
            GcxUser userBeingMerged = (GcxUser)this.getSession().get( Constants.SESSION_SELECTED_USER ) ;

	    // merge the users
	    log.debug(
		    "***** Perparing to merge: Primary user: {} User being merged: {}",
		    primaryUser, userBeingMerged);
	    final GcxUser authenticatedUser = this.getAuthenticatedUser();
	    this.getUserService().mergeUsers(primaryUser, userBeingMerged,
		    this.getApplicationSource(), authenticatedUser.getEmail());

	    // Update the initial user search with the deactivated version of
	    // the user that was merged
	    final SearchControlParameters scp = (SearchControlParameters) this
		    .getSession()
		    .get(this.getUserSearchControlParametersName());
            if ( scp != null ) {
		log.debug("***** Updating user search results with merged user in case it was listed");
		scp.getUserSearchResponse()
			.updateUserInEntries(userBeingMerged);
            }

	    // Put the primary user back in the session so that the update user
	    // view can retrieve it
            this.getSession().put( Constants.SESSION_USER_BEING_UPDATED, primaryUser ) ;

	    return SUCCESS;
	}
	// ACTION: Cancel
	else {
	    log.debug("***** Canceling merge");

	    // Because of the cancel request, we are returning to the previous
	    // merge search
            this.getSession().put( Constants.SESSION_WORKFLOW_FLAG, Constants.WORKFLOW_FLAG_RETURN_TO_PREVIOUS ) ;

	    // Put the user back in the session in case it was changed
	    this.getSession().put(Constants.SESSION_SELECTED_USER,
		    this.getModel());
	    return Constants.ACTION_CANCEL;
        }
    } 
}
