package org.ccci.gcx.idm.core.model.type.support.impl;

import org.ccci.gcx.idm.core.model.type.impl.ConfiguredTypeCode;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * <b>ConfiguredTypeCodeSupportDao</b> is a concrete implementation of {@link ConfiguredTypeCodeSupport}
 * using Dao based configuration values.
 *
 * @author Greg Crider  Dec 20, 2007  11:18:06 AM
 */
public class ConfiguredTypeCodeSupportDao extends AbstractConfiguredTypeCodeSupportDaoImpl implements InitializingBean
{
    protected static final Log log = LogFactory.getLog( ConfiguredTypeCodeSupportDao.class ) ;

    
    /**
     * After all context references have been resolved, this method is called and will load
     * the type code values
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
    {
        this.intitialize() ;
    }
    
    
    /**
     * Initialize the type code values.
     * 
     * @see org.ccci.gcx.idm.core.model.type.support.ConfiguredTypeCodeSupport#intitialize()
     */
    public void intitialize()
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "Loading values for ConfiguredTypeCode" ) ;
        
        List<ConfiguredTypeCode> list = this.getConfiguredTypeCodeDao().locateAll() ;
        
        Iterator<ConfiguredTypeCode> it = list.iterator() ;
        while( it.hasNext() ) {
            ConfiguredTypeCode ctc = it.next() ;
            ctc.cacheTypeCode() ;
            /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( ctc ) ;
        }
    }

}
