package org.ccci.gcx.idm.common.service;

import java.util.Map;

/**
 * <b>BusinessServiceGroup</b> defines the common interface for accessing groups
 * of {@link BusinessService}'s.
 *
 * @author Greg Crider  Feb 1, 2008  3:13:28 PM
 */
public interface BusinessServiceGroup
{
    
    /**
     * @param a_ServiceGroup The {@link BusinessService} group to set. This is the collection
     * of {@link BusinessService}'s to be used internally by the service. The map entry key
     * is the bean or service name, and the element is a reference to the service bean
     * itself. If there is currently no defined group, this parameter is set. If a
     * group already exists, the specified group is added.
     */
    public void setServiceGroup( Map<String,BusinessService> a_ServiceGroup ) ;
    
    
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
    public BusinessService getService( String a_Name ) ;

}
