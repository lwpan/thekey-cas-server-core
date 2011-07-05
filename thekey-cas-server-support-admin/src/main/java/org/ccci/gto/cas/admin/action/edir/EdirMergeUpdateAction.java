package org.ccci.gto.cas.admin.action.edir;

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
    
    
    public GcxUser getGcxUser()
    {
	return (GcxUser) this.getModel();
    }

    public void setGcxUser( GcxUser a_GcxUser )
    {
        this.setModelObject( a_GcxUser ) ;
    }


    /**
     * Initialize the input for the view.
     * 
     * @return Result name.
     */
    public String updateMergeInput()
    {
        String result = EdirMergeUpdateAction.SUCCESS ;
        
        // Recover the selected user from mearch search lookup
        this.setModelObject( (GcxUser)this.getSession().get( Constants.SESSION_SELECTED_USER ) ) ;
        
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** User being merged: " + this.getGcxUser() ) ;
        
        return result ;
    }
    
    
    /**
     * Handle requests to update the final merge.
     * 
     * @return Result name.
     */
    public String updateMerge()
    {
	String result = SUCCESS;
        GcxUser authenticatedUser = (GcxUser)this.getSession().get( Constants.SESSION_AUTHENTICATED_USER ) ;
        
        // ACTION: Merge
        if ( this.getMergeAction().equals( Constants.ACTION_MERGE ) ) {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Performing final merge update" ) ;
            // Recover the primary user being merged into
            GcxUser primaryUser = (GcxUser)this.getSession().get( Constants.SESSION_USER_BEING_UPDATED ) ;
            GcxUser userBeingMerged = (GcxUser)this.getSession().get( Constants.SESSION_SELECTED_USER ) ;
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Perparing to merge:\n\t:Primary user: " + primaryUser + "\n\tUser being merged: " + userBeingMerged ) ;
            this.getGcxUserService().mergeUsers( primaryUser, userBeingMerged, this.getApplicationSource(), authenticatedUser.getEmail() ) ;
            // Update the initial user search with the deactivated version of the user that was merged
            SearchControlParameters scp = (SearchControlParameters)this.getSession().get( this.getUserSearchControlParametersName() ) ;
            if ( scp != null ) {
                /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Updating user search results with merged user in case it was listed" ) ;
                scp.getUserSearchResponse().udpateUserInEntries( userBeingMerged ) ;
            }
            // Enforce the primary user back in the session so that update user view can retrieve it
            this.getSession().put( Constants.SESSION_USER_BEING_UPDATED, primaryUser ) ;
        // ACTION: Cancel
        } else {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Canceling merge" ) ;
            // Because of the cancel request, we are returning to the previous merge search
            this.getSession().put( Constants.SESSION_WORKFLOW_FLAG, Constants.WORKFLOW_FLAG_RETURN_TO_PREVIOUS ) ;
            // Put the user back in the session in case it was changed
            this.getSession().put( Constants.SESSION_SELECTED_USER, this.getGcxUser() ) ;
            result = Constants.ACTION_CANCEL ;
        }
      
        return result ;
    } 
}
