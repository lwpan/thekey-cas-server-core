package org.ccci.gcx.idm.common.struts2;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.service.BusinessService;
import org.ccci.gcx.idm.common.service.BusinessServiceException;
import org.ccci.gcx.idm.common.service.BusinessServiceGroup;
import org.ccci.gcx.idm.common.service.support.BusinessServiceGroupSupport;


import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

/**
 * <b>AbstractBusinessServicePreparableAction</b> contains the common functionality required by all
 * concrete implementations of {@link Preparable} for use with Struts2. It extends from
 * {@link ActionSupport} to take advantage of the convenience methods and implementations therein.
 *
 * @author Greg Crider  Jan 7, 2008  2:11:55 PM
 */
public abstract class AbstractBusinessServicePreparableAction 
    extends ActionSupport implements Preparable, BusinessServiceGroup
{
    private static final long serialVersionUID = -8170778181711768639L ;

    protected static final Log log = LogFactory.getLog( AbstractBusinessServicePreparableAction.class ) ;

    private BusinessServiceGroup m_BusinessServiceGroup = new BusinessServiceGroupSupport() ;
    
    
    /**
     * @param a_ServiceGroup The {@link BusinessService} group to set. This is the collection
     * of {@link BusinessService}'s to be used internally by the service. The map entry key
     * is the bean or service name, and the element is a reference to the service bean
     * itself. If there is currently no defined group, this parameter is set. If a
     * group already exists, the specified group is added.
     */
    public void setServiceGroup( Map<String,BusinessService> a_ServiceGroup ) 
    {
        this.m_BusinessServiceGroup.setServiceGroup( a_ServiceGroup ) ;
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
        return this.m_BusinessServiceGroup.getService( a_Name ) ;
    }

    
    /**
     * Default prepare method does nothing. Override to change this behavior
     * for a specific, concrete implementation.
     * 
     * @throws Exception if a stupid error occurs.
     * 
     * @see com.opensymphony.xwork2.Preparable#prepare()
     */
    public void prepare() throws Exception
    {
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Preparing action" ) ;
    }

}
