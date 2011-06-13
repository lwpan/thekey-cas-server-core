package org.ccci.gcx.idm.core.model.type.impl;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.IdmException;
import org.springframework.util.Assert;

/**
 * <b>ConfiguredTypeCodeWrapper</b> are used as a wrapper class for
 * {@link ConfiguredTypeCode} objects. This is necessary for {@link TypeCode}'s that
 * must be deterministically known to the application, but still maintain some of the
 * convenience of being derived from configuration (to modify labels and descriptions
 * outside of the application). 
 * <p>
 * <b>Note:</b> 
 * <ol>
 * <li> {@link ConfiguredTypeCode} objects use delayed caching. Concrete
 * implementations of this class should never attempt to place the object in the
 * cache, since it is in fact being derived out of the cache.
 * <li> Since the wrapped class is really a convenience mechanism, and the original, underlying
 * {@link ConfiguredTypeCode} is what is required for the persistence layer, this original
 * {@link ConfiguredTypeCode} can be retained in the class instance. When using the <tt>transer</tt>
 * class method, this original type code is retained automatically.
 * </ol>
 *
 * @author Greg Crider  Dec 20, 2007  2:57:03 PM
 */
public class ConfiguredTypeCodeWrapper extends ConfiguredTypeCode
{
    private static final long serialVersionUID = 8975895492570385627L ;

    protected static final Log log = LogFactory.getLog( ConfiguredTypeCodeWrapper.class ) ;

    /**
     * Fields that are transferrable from a parent {@link ConfiguredTypeCode} into
     * a new, wrapped instance.
     */
    private static final String[] TransferrableAttributes = new String[] {
        "id",
        "version",
        "createDate",
        "type",
        "code",
        "label",
        "description"
    } ;
    
    private ConfiguredTypeCode m_OriginalConfiguredTypeCode = null ;
    
    
    static {
        synchronized( ConfiguredTypeCodeWrapper.class ) {
            // Register beanutil converters
            ConvertUtils.register( new DateConverter(), java.util.Date.class ) ;
        }
    }
    
    
    /**
     * Create a new instance of the specified class, and populate it with the values found in
     * the specified root object.
     * 
     * @param a_Class Target class of the newly created instance.
     * @param a_TypeCode {@link ConfiguredTypeCode} to be transferred into the new instance of the
     *        type specified by <tt>a_Class</tt>.
     *        
     * @return Newly created instance of type <tt>a_Class</tt>.
     */
    public static ConfiguredTypeCodeWrapper transfer( Class<?> a_Class, ConfiguredTypeCode a_TypeCode )
    {
        ConfiguredTypeCodeWrapper result = null ;
        
        Assert.isAssignable( ConfiguredTypeCode.class, a_Class, "Target class must be assignable to \"" + ConfiguredTypeCode.class.getName() + "\"." ) ;
        
        try {
            // Create new instance of target class
            Constructor<?> con = a_Class.getConstructor( new Class[]{} ) ;
            result = (ConfiguredTypeCodeWrapper)con.newInstance( new Object[]{} ) ;
            // Transfer the contents of the specified bean to the new target instance
            Map<String, String> attributes = new HashMap<String,String>() ;
            for( int i=0; i<ConfiguredTypeCodeWrapper.TransferrableAttributes.length; i++ ) {
                String name = ConfiguredTypeCodeWrapper.TransferrableAttributes[i] ; 
                String value = BeanUtils.getProperty( a_TypeCode, name ) ;
                attributes.put( name, value ) ;
            }
            BeanUtils.populate( result, attributes ) ;
        } catch ( Exception e ) {
            throw new IdmException( "Unable to transfer to class \"" + a_Class.getName() + "\" from " + a_TypeCode, e ) ;
        }
        
        // Save the original type code, in case it is needed
        result.setOriginalConfiguredTypeCode( a_TypeCode ) ;
        
        return result ;
    }

    
    /**
     * @return the originalConfiguredTypeCode
     */
    public ConfiguredTypeCode getOriginalConfiguredTypeCode()
    {
        return this.m_OriginalConfiguredTypeCode ;
    }
    /**
     * @param a_originalConfiguredTypeCode the originalConfiguredTypeCode to set
     */
    public void setOriginalConfiguredTypeCode( ConfiguredTypeCode a_originalConfiguredTypeCode )
    {
        this.m_OriginalConfiguredTypeCode = a_originalConfiguredTypeCode ;
    }
    
    
    /*
     * We need to publically expose these setters in order for beanutils to be 
     * able to transfer the attributes.
     */


    public void setType( String a_Type ) 
    {
        super.setType( a_Type ) ;
    }
    public void setCode( Object a_Code )
    {
        super.setCode( a_Code ) ;
    }
    public void setLabel( String a_Label )
    {
        super.setLabel( a_Label ) ;
    }
    public void setDescription( String a_Description )
    {
        super.setDescription( a_Description ) ;
    }
   

    /**
     * <b>DateConverter</b> is a beanutils {@link Converter} to convert Strings into
     * {@link java.util.Date} objects.
     *
     * @author Greg Crider  Dec 20, 2007  4:48:22 PM
     */
    public static class DateConverter implements Converter
    {
        private static final SimpleDateFormat ConverterFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" ) ;
        
        
        /**
         * Convert the object, which is a formatted date string, into a {@link java.util.Date}
         * object. The assumed format that beanutils produces is "yyyy-MM-dd HH:mm:ss.S" (for
         * example "2007-12-20 00:00:00.0").
         */
	@SuppressWarnings("rawtypes")
        public Object convert( Class a_Class, Object a_Object )
        {
            Date result = null ;
            
            if ( !String.class.isAssignableFrom( a_Object.getClass() ) ) {
                throw new IdmException( "Conversion object is of class type \"" + a_Object.getClass().getName() + "\" and not String" ) ;
            }
            
            try {
                result = DateConverter.ConverterFormat.parse( (String)a_Object ) ;
            } catch ( ParseException pe ) {
                throw new IdmException( "Unable to parse date \"" + a_Object + "\".", pe ) ;
            }
            
            return result ;
        }
    }
    
}
