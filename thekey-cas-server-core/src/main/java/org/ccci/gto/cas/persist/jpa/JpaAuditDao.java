package org.ccci.gto.cas.persist.jpa;

import org.ccci.gto.cas.model.Audit;
import org.ccci.gto.cas.persist.AuditDao;
import me.thekey.cas.persist.jpa.AbstractDao;

public class JpaAuditDao extends AbstractDao<Audit> implements AuditDao {}
