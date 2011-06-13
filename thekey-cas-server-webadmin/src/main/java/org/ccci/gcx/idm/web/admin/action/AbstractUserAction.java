package org.ccci.gcx.idm.web.admin.action;

import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;


/**
 * <b>AbstractUserAction</b> contains the common functionality used by {@link Action}'s that
 * require {@link GcxUserService} related functions.
 *
 * @author Greg Crider  Nov 6, 2008  4:15:06 PM
 */
public abstract class AbstractUserAction extends AbstractAuditAction<GcxUser> {
    private static final long serialVersionUID = -7667374651441831492L ;

    
    /**
     * Convenience method to retrieve the {@link GcxUserService}.
     * 
     * @return {@link GcxUserService} implementation.
     */
    protected GcxUserService getGcxUserService()
    {
        return (GcxUserService)this.getService( Constants.BEAN_GCXUSER_SERVICE ) ;
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
        
        /*
         * Clear out any status messages that were previously set. That way an action implementation
         * doesn't have to worry about clearing them out upon entry.
         */
        this.getSession().remove( org.ccci.gcx.idm.web.admin.Constants.SESSION_STATUS_MESSAGE ) ;
    }

    
    /**
     * Convenience method to return {@link ModelObject} as {@link GcxUser} object.
     * 
     * @return Current model as {@link GcxUser}.
     */
    @Deprecated
    public GcxUser getGcxUser() {
	return (GcxUser) this.getModel();
    }

    /**
     * Convenience method to set the {@link ModelObject} as {@link GcxUser} object.
     * 
     * @param a_GcxUser {@link GcxUser} object to set for model.
     */
    @Deprecated
    public void setGcxUser( GcxUser a_GcxUser )
    {
        this.setModelObject( a_GcxUser ) ;
    }

}
