package org.ccci.gcx.idm.core.service;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.common.service.DataAccessService;
import org.ccci.gcx.idm.core.model.impl.GcxUser;

/**
 * <b>AuditService</b> defines the available functionality for performing audit
 * related services.
 *
 * @author Greg Crider  Oct 19, 2008  8:59:26 PM
 */
public interface AuditService extends DataAccessService
{

    
    /**
     * Create a new audit based on the specified, generic information.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Entity New entity that is being audited.
     */
    public void create( String a_Source, String a_ChangedBy, String a_Userid, String a_Description, Object a_Entity ) ;
    
    
    /**
     * Audit an object that is being deleted.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Entity New entity that is being audited.
     */
    public void delete( String a_Source, String a_ChangedBy, String a_Userid, String a_Description, Object a_Entity ) ;
    
    
    /**
     * Audit an object that was updated.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Original Original version of object before change was made.
     * @param a_Current Current version of object with new changes to it.
     */
    public void update( String a_Source, String a_ChangedBy, String a_Userid, String a_Description, ModelObject a_Original, ModelObject a_Current ) ;
    
    
    /**
     * Audit an individual property, by name, that was changed.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Entity Updated entity that is being audited.
     * @param a_PropertyName Name of property on entity that was updated.
     */
    public void updateProperty( String a_Source, String a_ChangedBy, String a_Userid, String a_Description, Object a_Entity, String a_PropertyName ) ;
    
    
    /**
     * Audit a user being merged into another user.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_PrimaryUser Primary {@link GcxUser}.
     * @param a_UserBeingMerged {@link GcxUser} being merged into primary.
     * @param a_Description Description of change.
     */
    public void merge( String a_Source, String a_ChangedBy, GcxUser a_PrimaryUser, GcxUser a_UserBeingMerged, String a_Description ) ;
    
}
