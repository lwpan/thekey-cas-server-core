package org.ccci.gto.cas.persist;

import java.util.List;

import org.ccci.gcx.idm.core.model.impl.Audit;
import org.ccci.gto.persist.CrudDao;

/**
 * <b>AuditDao</b> defines the {@link CrudDao} for persisting and retrieving
 * {@link Audit} entities.
 * 
 * @author Daniel Frett
 */
public interface AuditDao extends CrudDao {
    /**
     * Find all {@link Audit} objects for the specified userid.
     * 
     * @param a_Userid
     *            Userid for lookup.
     * 
     * @return {@link List} of {@link Audit} objects for userid, or
     *         <tt>null</tt> if none are found.
     */
    public List<Audit> findAllByUserid(String a_Userid);
}
