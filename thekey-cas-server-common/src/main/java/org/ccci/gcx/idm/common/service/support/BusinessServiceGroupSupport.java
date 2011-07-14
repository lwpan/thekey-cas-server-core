package org.ccci.gcx.idm.common.service.support;

import java.util.Map;

import org.ccci.gcx.idm.common.service.BusinessService;
import org.ccci.gcx.idm.common.service.BusinessServiceException;
import org.ccci.gcx.idm.common.service.BusinessServiceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>BusinessServiceGroupSupport</b> contains convenience methods for injecting and accessing
 * sets of {@link BusinessService} implementations.
 *
 * @author Greg Crider  Jan 31, 2008  5:54:00 PM
 */
public class BusinessServiceGroupSupport implements BusinessServiceGroup
{
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** Group of Services with their bean id/name as the key */
    private Map<String,BusinessService> m_ServiceGroup = null ;

    
    /**
     * @param a_ServiceGroup The {@link BusinessService} group to set. This is the collection
     * of {@link BusinessService}'s to be used internally by the service. The map entry key
     * is the bean or service name, and the element is a reference to the service bean
     * itself. If there is currently no defined group, this parameter is set. If a
     * group already exists, the specified group is added.
     */
    public void setServiceGroup( Map<String,BusinessService> a_ServiceGroup )
    {
        if ( this.m_ServiceGroup == null ) {
            this.m_ServiceGroup = a_ServiceGroup ;
        } else {
            this.m_ServiceGroup.putAll( a_ServiceGroup ) ;
        }
    }
    
    
    /**
     * Get the specified {@link BusinessService} object.
     * 
     * @param a_Name {@link BusinessService} name.
     * 
     * @return {@link BusinessService} object.
     * 
     * @exception BusinessServiceException if the specified {@link BusinessService} was not properly
     *         configured (i.e., it is not present).
     */
    public BusinessService getService( String a_Name )
    {
        BusinessService result = null ;
        
        // Make sure there are actually some Services defined
        if ( this.m_ServiceGroup == null ) {
            /*= ERROR =*/ log.error( "No Services were configured for the service" ) ;
            throw new BusinessServiceException( "No Services were configured for the service" ) ;
        // Make sure the specified Service is actually present
        } else if ( !this.m_ServiceGroup.containsKey( a_Name ) ) {
            String errorMsg = "The Service with Name(" + a_Name + ") is not defined" ;
            /*= ERROR =*/ log.error( errorMsg ) ;
            throw new BusinessServiceException( errorMsg ) ;
        }
        
        // Get the Service
        result = (BusinessService)this.m_ServiceGroup.get( a_Name ) ;
        
        return result ;
    }

}
