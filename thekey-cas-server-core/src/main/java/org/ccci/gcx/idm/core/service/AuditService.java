package org.ccci.gcx.idm.core.service;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.model.Auditable;
import org.ccci.gto.cas.service.audit.AuditException;

/**
 * <b>AuditService</b> defines the available functionality for performing audit
 * related services.
 *
 * @author Greg Crider  Oct 19, 2008  8:59:26 PM
 * @deprecated Audits should utilize Inspektr
 */
@Deprecated
public interface AuditService {

    
    /**
     * Create a new audit based on the specified, generic information.
     * 
     * @param a_Source Source that lead to the audit.
     * @param a_ChangedBy Who made change that lead to audit.
     * @param a_Userid Userid of person affected by change.
     * @param a_Description Description of change.
     * @param a_Entity New entity that is being audited.
     */
    public void create(final String a_Source, final String a_ChangedBy,
	    final String a_Userid, final String a_Description,
	    final Auditable a_Entity);

    /**
     * Audit an object that was updated.
     * 
     * @param source
     *            Source that lead to the audit.
     * @param changedBy
     *            Who made change that lead to audit.
     * @param userId
     *            Userid of person affected by change.
     * @param description
     *            Description of change.
     * @param original
     *            Original version of object before change was made.
     * @param current
     *            Current version of object with new changes to it.
     * @throws AuditException
     */
    public void update(final String source, final String changedBy,
	    final String userId, final String description,
	    final Auditable original, final Auditable current)
	    throws AuditException;

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
    public void updateProperty(final String a_Source, final String a_ChangedBy,
	    final String a_Userid, final String a_Description,
	    final Auditable a_Entity, final String a_PropertyName);
    
    
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
