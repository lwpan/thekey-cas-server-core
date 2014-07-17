package org.ccci.gcx.idm.core.service.impl;

import org.ccci.gcx.idm.core.service.AuditService;

import javax.validation.constraints.NotNull;

/**
 * <b>AbstractAuditableService</b> contains common functionality for all
 * services that require the ability to audit actions.
 *
 * @author Daniel Frett
 */
public abstract class AbstractAuditableService {
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
     * @param auditService the auditService to use
     */
    public void setAuditService(final AuditService auditService) {
        this.auditService = auditService;
    }
}
