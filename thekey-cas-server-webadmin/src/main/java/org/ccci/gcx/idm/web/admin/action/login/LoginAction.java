package org.ccci.gcx.idm.web.admin.action.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.GcxUserAccountLockedException;
import org.ccci.gcx.idm.core.GcxUserException;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.admin.Constants;
import org.ccci.gcx.idm.web.admin.action.AbstractUserAction;

/**
 * <b>LoginAction</b> handles requests related to the login and logout process.
 *
 * @author Greg Crider  Nov 6, 2008  6:22:35 PM
 */
public class LoginAction extends AbstractUserAction
{
    private static final long serialVersionUID = 279177251355422724L ;
    
    protected static final Log log = LogFactory.getLog( LoginAction.class ) ;
    
    
    /**
     * Prepare the login prompt for accepting the user credentials.
     * 
     * @return Result name.
     */
    public String prompt()
    {
        String result = LoginAction.SUCCESS ;

        // Invalidate the session
        this.invalidate() ;
        
        return result ;
    }
    
    
    /**
     * Handle a login request.
     * 
     * @return Result name.
     */
    public String login()
    {
        String result = LoginAction.SUCCESS ;

	if (log.isDebugEnabled()) {
	    log.debug("Requested user: (" + this.getModel().getEmail() + ")");
	}
        
        try {
            // Authenticate user
	    this.getGcxUserService().authenticate(this.getModel());
            
            // Invalidate the session
            this.invalidate() ;
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** New HTTP session started" ) ;

            // If we made it this far, retrieve the user object
	    final GcxUser user = this.getGcxUserService().findUserByEmail(
		    this.getModel().getEmail());
            // Test to see if the user is authorized as an admin
            if ( !this.getGcxUserService().isUserInAdminGroup( user ) ) {
                this.addActionError( this.getText( "login.error.action.notadmin" ) ) ;
                result = LoginAction.ERROR ;
                /*= ERROR =*/ log.error( "User is not authorized to use the admin application" ) ;
            }
            // Save the user in the session
            this.getSession().put( Constants.SESSION_AUTHENTICATED_USER, user ) ;
        } catch ( GcxUserAccountLockedException guale ) {
            /*= ERROR =*/ log.error( "User has been administratively locked ... bye, bye!" ) ;
            throw guale ;
        } catch ( GcxUserException gue ) {
            this.addActionError( this.getText( "login.error.action.missinguseridpassword" ) ) ;
            this.addFieldError( "userid", this.getText( "login.error.missing.userid" ) ) ;
            this.addFieldError( "passwd", this.getText( "login.error.missing.password" ) ) ;
            result = LoginAction.ERROR ;
            /*= ERROR =*/ log.error( "User authentication failed: " + gue.getMessage() ) ;
        }
        
        return result ;
    }
    
    
    /**
     * Handle a logout request.
     * 
     * @return Result name.
     */
    public String logout()
    {
        String result = LoginAction.SUCCESS ;

        GcxUser user = (GcxUser)this.getSession().get( Constants.SESSION_AUTHENTICATED_USER ) ;

        if ( user == null ) {
            /*= ERROR =*/ log.error( "There is no user object in the current session" ) ;
        } else {
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "Logging off user: " + user.getEmail() ) ;
        }
        
        // Invalidate the session
        this.invalidate() ;
        
        return result ;
    }
    
    
    /**
     * We can override the prepare step if there are some resources that need to be
     * setup prior to the action running.
     * 
     * @throws Exception If an error occurs.
     */
    public void prepare() throws Exception
    {
        super.prepare() ;
    }
}
