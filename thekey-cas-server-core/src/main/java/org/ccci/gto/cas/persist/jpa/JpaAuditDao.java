package org.ccci.gto.cas.persist.jpa;

import org.ccci.gto.cas.model.Audit;
import org.ccci.gto.cas.persist.AuditDao;
import org.ccci.gto.persist.jpa.AbstractCrudDao;

import java.io.Serializable;

public class JpaAuditDao extends AbstractCrudDao<Audit> implements AuditDao {
    @Override
    public Audit get(Serializable key) {
	throw new UnsupportedOperationException(
		"This method is not currently implemented.");
    }

    @Override
    public Audit load(Serializable key) {
	throw new UnsupportedOperationException(
		"This method is not currently implemented.");
    }
}
