package org.ccci.gto.cas.persist.jpa;

import java.io.Serializable;
import java.util.List;

import org.ccci.gto.cas.model.Audit;
import org.ccci.gto.cas.persist.AuditDao;
import org.ccci.gto.persist.jpa.AbstractCrudDao;

public class JpaAuditDao extends AbstractCrudDao<Audit> implements AuditDao {
    public Audit get(Serializable key) {
	throw new UnsupportedOperationException(
		"This method is not currently implemented.");
    }

    public Audit load(Serializable key) {
	throw new UnsupportedOperationException(
		"This method is not currently implemented.");
    }

    public List<Audit> findAllByUserid(String a_Userid) {
	throw new UnsupportedOperationException(
		"This method is not currently implemented.");
    }
}
