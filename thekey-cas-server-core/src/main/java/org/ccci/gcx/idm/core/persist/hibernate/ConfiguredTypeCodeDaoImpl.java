package org.ccci.gcx.idm.core.persist.hibernate;

import java.util.List;

import org.ccci.gcx.idm.common.IdmException;
import org.ccci.gcx.idm.common.persist.hibernate.AbstractQueryDao;
import org.ccci.gcx.idm.core.model.type.impl.ConfiguredTypeCode;
import org.ccci.gcx.idm.core.persist.ConfiguredTypeCodeDao;

/**
 * <b>ConfiguredTypeCodeDaoImpl</b> is the concrete implementation of {@link ConfiguredTypeCodeDao}.
 *
 * @author Greg Crider  Dec 19, 2007  3:19:15 PM
 */
public class ConfiguredTypeCodeDaoImpl extends AbstractQueryDao implements ConfiguredTypeCodeDao
{

    /**
     * Return all of the {@link ConfiguredTypeCode} objects found in the backing store.
     * 
     * @return {@link List} of {@link ConfiguredTypeCode} objects, or <tt>null</tt>
     *         if none are found.
     *         
     * @see org.ccci.gcx.idm.ctm.core.persist.ConfiguredTypeCodeDao#locateAll()
     */
    public List<ConfiguredTypeCode> locateAll()
    {
        throw new IdmException( "This DAO is not yet implemented" ) ;
    }

}
