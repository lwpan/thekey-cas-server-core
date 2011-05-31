package org.ccci.gcx.idm.core.persist.hibernate;

import java.util.List;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.common.persist.hibernate.AbstractCrudDao;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.model.impl.Audit;
import org.ccci.gto.cas.persist.AuditDao;

/**
 * <b>AuditDaoImpl</b> is the concrete implementation of {@link AuditDao}.
 *
 * @author Greg Crider  Oct 19, 2008  5:30:26 PM
 */
public class AuditDaoImpl extends AbstractCrudDao implements AuditDao
{
    /**
     * Find all {@link Audit} objects for the specified userid.
     * 
     * @param a_Userid Userid for lookup.
     * 
     * @return {@link List} of {@link Audit} objects for userid, or <tt>null</tt> if none
     * are found.
     */
    @SuppressWarnings("unchecked")
    public List<Audit> findAllByUserid( String a_Userid )
    {
        List<Audit> result = null ;
        
        result = (List<Audit>)this.findByNamedQueryAndNamedParam( 
                    Constants.QUERY_AUDIT_FINDALLBYUSERID, 
                    "userid", 
                    a_Userid
                ) ;
       
        return result ;
    }

    @Override
    protected Class<? extends ModelObject> getModelClass() {
	return Audit.class;
    }
}
