package org.ccci.gcx.idm.core.service.impl;

import org.ccci.gcx.idm.common.service.impl.AbstractDataAccessService;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.service.AuditService;

/**
 * <b>AbstractAuditableService</b> contains common functionality for all {@link BusinessService}
 * implementations that require the ability to be audited.
 *
 * @author Greg Crider  Oct 21, 2008  1:11:36 PM
 */
public abstract class AbstractAuditableService extends AbstractDataAccessService
{

    
    /**
     * Convenience method to return the {@link AuditService}.
     * 
     * @return {@link AuditService}.
     */
    protected AuditService getAuditService()
    {
        return (AuditService)this.getService( Constants.BEAN_AUDIT_SERVICE ) ;
    }
}
