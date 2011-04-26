package org.ccci.gcx.idm.core.model.type.impl;

import java.io.Serializable;
import java.util.Date;

import org.ccci.gcx.idm.common.model.type.AbstractAlphaNumericTypeCode;
import org.ccci.gcx.idm.common.model.type.TypeCode;

/**
 * <b>ConfiguredTypeCode</b> returns {@link TypeCode} objects that are based on
 * some external configuration.
 *
 * @author Greg Crider  Dec 18, 2007  6:14:48 PM
 */
public class ConfiguredTypeCode extends AbstractAlphaNumericTypeCode
{
    private static final long serialVersionUID = 6948482179089923246L ;
    
    /** Unique id for the entity */
    private Serializable m_Id = null ;
    /** Date the entity was created (or persisted for the first time) */
    private Date m_CreateDate = null ;
    /** Version number of the specific entity */
    private Integer m_Version = null ;
    
    
    /**
     * @return the createDate
     */
    public Date getCreateDate()
    {
        return this.m_CreateDate ;
    }
    /**
     * @param a_createDate the createDate to set
     */
    public void setCreateDate( Date a_createDate )
    {
        this.m_CreateDate = a_createDate ;
    }
    
    
    /**
     * @return the id
     */
    public Serializable getId()
    {
        return this.m_Id ;
    }
    /**
     * @param a_id the id to set
     */
    public void setId( Serializable a_id )
    {
        this.m_Id = a_id ;
    }
    
    
    /**
     * @return the version
     */
    public Integer getVersion()
    {
        return this.m_Version ;
    }
    /**
     * @param a_version the version to set
     */
    public void setVersion( Integer a_version )
    {
        this.m_Version = a_version ;
    }

    
    /**
     * Delay the caching of a new instance. This is because the created instance may be hollow.
     * The values need to be set first (e.g., Hibernate will use property editors to set values),
     * and then the completed instance can be cached.
     * 
     * @return <tt>True</tt> if cache placement should be delayed upon instantiation.
     */
    protected boolean delayCache()
    {
        return true ;
    }
    
    
    /**
     * Create a configuration based {@link TypeCode} with all known parameters.
     * 
     * @param a_Type {@link TypeCode} type.
     * @param a_Code Code value.
     * @param a_Label Label value.
     * @param a_Description {@link TypeCode} description.
     */
    protected ConfiguredTypeCode( String a_Type, String a_Code, String a_Label, String a_Description )
    {
        super( a_Type, a_Code, a_Label, a_Description ) ;
    }
    
    
    /**
     * Default, empty constructor for use with frameworks that use such things as
     * property editors to populate an otherwise hollow instance.
     */
    public ConfiguredTypeCode()
    {
        super( "", "", "", "" ) ;
    }
    
}
