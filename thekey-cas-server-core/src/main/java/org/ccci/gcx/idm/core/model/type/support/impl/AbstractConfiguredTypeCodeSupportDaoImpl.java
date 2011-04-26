package org.ccci.gcx.idm.core.model.type.support.impl;

import org.ccci.gcx.idm.core.model.type.support.ConfiguredTypeCodeSupport;
import org.ccci.gcx.idm.core.persist.ConfiguredTypeCodeDao;

/**
 * <b>AbstractConfiguredTypeCodeSupportDaoImpl</b> contains the common functionality used
 * by Dao based concrete implementations of {@link ConfiguredTypeCodeSupport}.
 *
 * @author Greg Crider  Dec 20, 2007  11:15:55 AM
 */
public abstract class AbstractConfiguredTypeCodeSupportDaoImpl implements ConfiguredTypeCodeSupport
{

    /** Dao used to acquire type code values */
    private ConfiguredTypeCodeDao m_ConfiguredTypeCodeDao = null ;

    
    /**
     * @return the configuredTypeCodeDao
     */
    public ConfiguredTypeCodeDao getConfiguredTypeCodeDao()
    {
        return this.m_ConfiguredTypeCodeDao ;
    }
    /**
     * @param a_configuredTypeCodeDao the configuredTypeCodeDao to set
     */
    public void setConfiguredTypeCodeDao( ConfiguredTypeCodeDao a_configuredTypeCodeDao )
    {
        this.m_ConfiguredTypeCodeDao = a_configuredTypeCodeDao ;
    }
    
}
