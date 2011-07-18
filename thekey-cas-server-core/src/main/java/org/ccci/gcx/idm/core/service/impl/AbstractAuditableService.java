package org.ccci.gcx.idm.core.service.impl;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.common.service.BusinessService;
import org.ccci.gcx.idm.common.service.impl.AbstractDataAccessService;
import org.ccci.gcx.idm.core.service.AuditService;

/**
 * <b>AbstractAuditableService</b> contains common functionality for all
 * {@link BusinessService} implementations that require the ability to be
 * audited.
 * 
 * @author Daniel Frett
 */
public abstract class AbstractAuditableService extends
	AbstractDataAccessService {
    @NotNull
    private AuditService auditService;

    /**
     * retrieve the {@link AuditService}.
     * 
     * @return {@link AuditService}.
     */
    protected AuditService getAuditService() {
	return this.auditService;
    }

    /**
     * @param auditService
     *            the auditService to use
     */
    public void setAuditService(final AuditService auditService) {
	this.auditService = auditService;
    }
}
