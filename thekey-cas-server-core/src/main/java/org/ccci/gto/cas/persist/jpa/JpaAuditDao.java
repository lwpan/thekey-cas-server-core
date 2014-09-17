package org.ccci.gto.cas.persist.jpa;

import org.ccci.gto.cas.model.Audit;
import org.ccci.gto.cas.persist.AuditDao;
import org.ccci.gto.persist.jpa.AbstractCrudDao;

public class JpaAuditDao extends AbstractCrudDao<Audit> implements AuditDao {}
