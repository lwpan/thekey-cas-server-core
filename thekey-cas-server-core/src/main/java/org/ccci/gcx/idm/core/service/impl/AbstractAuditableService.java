package org.ccci.gcx.idm.core.service.impl;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>AbstractAuditableService</b> contains common functionality for all
 * services that require the ability to audit actions.
 * 
 * @author Daniel Frett
 */
public abstract class AbstractAuditableService {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

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
