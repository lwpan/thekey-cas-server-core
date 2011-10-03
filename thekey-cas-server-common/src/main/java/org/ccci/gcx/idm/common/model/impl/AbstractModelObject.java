package org.ccci.gcx.idm.common.model.impl;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.NoSuchElementException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.ccci.gcx.idm.common.IdmException;
import org.ccci.gcx.idm.common.model.type.AbstractTypeCode;

/**
 * <b>AbstractModelObject</b> contains common functionality for all objects.
 * <p>
 * <b>Note:</b> Since the Commons Beanutils framework is being used here, care
 * must be taken when selecting property names. Introspection is used in some of
 * the utility methods here to determine properties. If a property has
 * overloaded methods, it can affect the behavior.
 * 
 * @author Greg Crider Mar 28, 2007 1:12:37 PM
 */
public abstract class AbstractModelObject implements Serializable {
    private static final long serialVersionUID = 268458483882896910L ;

    /** Unique id for the entity */
    private Serializable m_Id = null ;
    /** Date the entity was created (or persisted for the first time) */
    private Date m_CreateDate = null ;
    /** Version number of the specific entity; default to zero for newly created entities. */
    private Integer m_Version = new Integer( 0 ) ;

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
     * Get the {@link AbstractTypeCode} for the specified type and value.
     * 
     * @param a_Value Value to locate code.
     * @param a_Default Default code if value not found.
     * @param a_Type Type of code.
     * 
     * @return Corresponding {@link AbstractTypeCode}.
     */
    public AbstractTypeCode getAbstractTypeCode( String a_Value, AbstractTypeCode a_Default, String a_Type )
    {
        AbstractTypeCode result = a_Default ;
        
        try {
            result = (AbstractTypeCode)AbstractTypeCode.codeForValue( a_Type, a_Value ) ;
        } catch ( NoSuchElementException nsee ) {}
        
        return result ;
    }
    
    
    /**
     * Get the {@link AbstractTypeCode} for the specified type and value.
     * 
     * @param a_Value Value to locate code.
     * @param a_Default Default code if value not found.
     * @param a_TypeClass Type class of code.
     * 
     * @return Corresponding {@link AbstractTypeCode}.
     */
    public AbstractTypeCode getAbstractTypeCode( String a_Value, AbstractTypeCode a_Default, Class<?> a_TypeClass )
    {
        AbstractTypeCode result = a_Default ;
        
        try {
            result = (AbstractTypeCode)AbstractTypeCode.codeForValue( a_TypeClass, a_Value ) ;
        } catch ( NoSuchElementException nsee ) {}
        
        return result ;
    }

    /**
     * Deep clone the instantiated object. Deep cloning is done, so if this object
     * becomes more complex, a field-for-field copy does not need to be performed.
     * 
     * @return Deep clone of object.
     */
    @Override
    public Object clone()
    {
        Object result = null ;

        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream() ;
            ObjectOutputStream out = new ObjectOutputStream( b ) ;
            out.writeObject( this ) ;
            ByteArrayInputStream in = new ByteArrayInputStream( b.toByteArray() ) ;
            ObjectInputStream oi = new ObjectInputStream( in ) ;
            result = oi.readObject() ;
        } catch ( Exception e ) {
            throw new IdmException( "Unable to create deep clone", e ) ;
        }

        return result ;
    }
    
    
    /**
     * Create display ready version of this object.
     * 
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer() ;
        
        String name = this.getClass().getName() ;
        int pos = ( name.lastIndexOf( "." ) < 0 ) ? 0 : ( name.lastIndexOf( "." ) + 1 ) ;
        
        result.append( "<<" )
              .append( name.substring( pos ) )
              .append( ">>::[" ) ;
        PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors( this ) ;
        for( int i=0; i<desc.length; i++ ) {
            if ( ( desc[i].getPropertyType() != null ) && 
                 ( ! java.util.List.class.isAssignableFrom( desc[i].getPropertyType() ) ) &&
                 ( ! java.util.Set.class.isAssignableFrom( desc[i].getPropertyType() ) ) ) {
                if (
                        ( !desc[i].getName().equals( "class" ) ) &&
                        ( !desc[i].getName().equals( "createTime" ) ) &&
                        ( !desc[i].getName().equals( "auditProperties" ) )
                   ) {
                    Object value = new String( "?" ) ;
                    if ( desc[i].getName().toLowerCase().matches( "password" ) ) {
                        value = "**redacted**" ;
                    } else {
                        try { value = BeanUtils.getProperty( this, desc[i].getName() ) ; } catch ( Exception e ) {} ;
                    }
                    if ( !result.substring( result.length() -1 ).equals( "[" ) ) {
                        result.append(  "," ) ;
                    }
                    result.append( desc[i].getName().toLowerCase() ) 
                          .append( "=" )
                          .append( value ) ;
                }
            }
        }
        result.append( "]" ) ;
        
        return result.toString() ;
    }
   
}
