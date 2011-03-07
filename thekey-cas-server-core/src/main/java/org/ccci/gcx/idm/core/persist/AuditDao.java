package org.ccci.gcx.idm.core.persist;

import java.util.List;

import org.ccci.gcx.idm.common.persist.CrudDao;
import org.ccci.gcx.idm.core.model.impl.Audit;

/**
 * <b>AuditDao</b> defines the {@link CrudDao} for persisting and retrieving
 * {@link Audit} entities.
 *
 * @author Greg Crider  Oct 19, 2008  5:29:11 PM
 */
public interface AuditDao extends CrudDao
{

    /**
     * Find all {@link Audit} objects for the specified userid.
     * 
     * @param a_Userid Userid for lookup.
     * 
     * @return {@link List} of {@link Audit} objects for userid, or <tt>null</tt> if none
     * are found.
     */
    public List<Audit> findAllByUserid( String a_Userid ) ;
    
}
