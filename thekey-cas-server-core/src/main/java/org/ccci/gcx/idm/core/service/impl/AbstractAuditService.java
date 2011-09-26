package org.ccci.gcx.idm.core.service.impl;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.service.AuditService;
import org.ccci.gto.cas.persist.AuditDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>AbstractAuditService</b> contains the common functionality used by all
 * concrete implementations of {@link AuditService} and related services.
 * 
 * @author Daniel Frett
 */
public abstract class AbstractAuditService implements AuditService {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @NotNull
    private AuditDao auditDao;

    /**
     * Convenience routine to return the {@link AuditDao}.
     * 
     * @return {@link AuditDao}.
     */
    protected AuditDao getAuditDao() {
	return this.auditDao;
    }

    /**
     * @param auditDao
     *            the auditDao to set
     */
    public void setAuditDao(final AuditDao auditDao) {
	this.auditDao = auditDao;
    }
}
