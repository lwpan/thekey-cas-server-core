package org.ccci.gcx.idm.common.model ;

import java.io.Serializable ;
import java.util.Date ;


/**
 * <b>ModelObject</b> defines the basic, domain model entity. It defines
 * properties that are common to all domain model entities.
 * 
 * @author Greg Crider  Oct 11, 2006  2:37:43 PM
 */
public interface ModelObject
{

    /**
     * Get the unique id for the domain model entity.
     * 
     * @return Id.
     */
    public Serializable getId() ;

    
    /**
     * Set the creation date for the domain model entity.
     * 
     * @param a_CreateDate Create date.
     */
    public void setCreateDate( Date a_CreateDate ) ;
    /**
     * Get the creation date for the domain model entity.
     * 
     * @return Create date.
     */
    public Date getCreateDate() ;

    
    /**
     * Get the domain model entity version number.
     * 
     * @return Version number.
     */
    public Integer getVersion() ;
    
    
    /**
     * All properties that should be audited.
     * 
     * @return {@link String} array of property names.
     */
    public String[] getAuditProperties() ;

}
