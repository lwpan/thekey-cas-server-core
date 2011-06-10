package org.ccci.gcx.idm.web.admin.action.edir;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.admin.Constants;
import org.ccci.gcx.idm.web.admin.action.AbstractUserSearchAction;

/**
 * <b>EdirMergeSearchAction</b> is used to perform merge search lookups.
 *
 * @author Greg Crider  Dec 4, 2008  7:05:42 PM
 */
public class EdirMergeSearchAction extends AbstractUserSearchAction
{
    private static final long serialVersionUID = -2361647729743441489L ;
    
    
    protected static final Log log = LogFactory.getLog( EdirMergeSearchAction.class ) ;
    
    
    /**
     * Label to be used for the update button.
     * 
     * @return Label for update button.
     */
    public String getUpdateButtonLabel()
    {
        return "View" ;
    }
    
    
    /**
     * Do the necessary steps to setup the user update action so a user's details can
     * be viewed and modified.
     */
    @SuppressWarnings("unchecked")
    protected void updateCallback() 
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Request to view user \"" + this.getSelectedUserEmail() + "\"" ) ;
        GcxUser selectedUser = this.getGcxUserService().findUserByEmail( this.getSelectedUserEmail() ) ;
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Recovered User: " + selectedUser ) ;
        this.getSearchControlParameters().setSelectedUser( selectedUser ) ;
        this.getSession().put( Constants.SESSION_SELECTED_USER, selectedUser ) ;
    }

}
