package org.ccci.gto.cas.admin.action.edir;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.admin.Constants;
import org.ccci.gto.cas.admin.action.AbstractUserSearchAction;

/**
 * <b>EdirUserSearchAction</b> is used to perform eDirectory user lookups.
 *
 * @author Greg Crider  Nov 13, 2008  3:37:18 PM
 */
public class EdirUserSearchAction extends AbstractUserSearchAction
{
    private static final long serialVersionUID = -8294803337069082414L ;

    /**
     * Label to be used for the update button.
     * 
     * @return Label for update button.
     */
    public String getUpdateButtonLabel()
    {
        return "Update" ;
    }
    
    
    /**
     * Do the necessary steps to setup the user update action so a user's details can
     * be viewed and modified.
     */
    protected void updateCallback() 
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Request to update user \"" + this.getSelectedUserEmail() + "\"" ) ;
        GcxUser selectedUser = this.getGcxUserService().findUserByEmail( this.getSelectedUserEmail() ) ;
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "***** Recovered User: " + selectedUser ) ;
        this.getSearchControlParameters().setSelectedUser( selectedUser ) ;
        this.getSession().put( Constants.SESSION_SELECTED_USER, selectedUser ) ;
    }

}
