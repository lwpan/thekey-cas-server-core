package org.ccci.gcx.idm.core.persist;

import java.util.List;

import org.ccci.gcx.idm.common.persist.QueryDao;
import org.ccci.gcx.idm.core.model.type.impl.ConfiguredTypeCode;

/**
 * <b>ConfiguredTypeCodeDao</b> defines the read-only {@link QueryDao} for
 * acquired {@link ConfiguredTypeCode} objects.
 *
 * @author Greg Crider  Dec 19, 2007  3:16:13 PM
 */
public interface ConfiguredTypeCodeDao extends QueryDao
{

    /**
     * Return all of the {@link ConfiguredTypeCode} objects found in the backing store.
     * 
     * @return {@link List} of {@link ConfiguredTypeCode} objects, or <tt>null</tt>
     *         if none are found.
     */
    public List<ConfiguredTypeCode> locateAll() ;
    
}
