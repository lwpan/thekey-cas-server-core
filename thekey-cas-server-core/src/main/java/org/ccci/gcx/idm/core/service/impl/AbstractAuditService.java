package org.ccci.gcx.idm.core.service.impl;

import org.ccci.gcx.idm.common.service.impl.AbstractDataAccessService;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.persist.AuditDao;
import org.ccci.gcx.idm.core.service.AuditService;

/**
 * <b>AbstractAuditService</b> contains the common functionality used by all concrete implementations
 * of {@link AuditService} and related services.
 *
 * @author Greg Crider  Oct 19, 2008  9:13:25 PM
 */
public abstract class AbstractAuditService extends AbstractDataAccessService
{

    
    /**
     * Convenience routine to return the {@link AuditDao}.
     * 
     * @return {@link AuditDao}.
     */
    protected AuditDao getAuditDao()
    {
        return (AuditDao)this.getDao( Constants.BEAN_AUDIT_DAO ) ;
    }
}
