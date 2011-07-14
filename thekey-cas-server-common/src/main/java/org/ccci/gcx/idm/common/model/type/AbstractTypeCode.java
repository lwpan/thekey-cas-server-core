package org.ccci.gcx.idm.common.model.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * <b>AbstractTypeCode</b> is used to define a {@link TypeCode} that will be one of many
 * related {@link TypeCode}'s (as an enumeration of sorts). The collection of related types 
 * can subsequently be retrieved in a sorted order.
 *
 * @author Greg Crider  Mar 28, 2007  1:12:37 PM
 */
public abstract class AbstractTypeCode implements TypeCode
{
    private static final long serialVersionUID = -5886971478169021477L ;

    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Cache is a map of {@link TypeCodeCache} objects. Each class implementing {@link AbstractTypeCode}
     * will have its total collection of objects placed in a {@link TypeCodeCaches}, which is in turn
     * stored in this object. 
     */
    private static IterableMap Cache = null ;

    /** Underlying code value */
    private Object m_Code = null ;
    /** Label */
    private String m_Label = null ;
    /** Description */
    private String m_Description = null ;
    /** Type */
    private String m_Type = null ;
    
    /** Flag used to indicate that the instance has been cached */
    private boolean m_IsCached = false ;
    
    
    static {
        // Make sure the cache is only created once.
        synchronized( AbstractTypeCode.class ) {
            AbstractTypeCode.Cache = new HashedMap() ;
        }
    }
    
    
    /**
     * Put the specified {@link TypeCode} in its appropriate cached collection.
     * 
     * @param a_TypeCode
     */
    public static void putCode( TypeCode a_TypeCode )
    {
        // Get the collection cache for the specified type code
        TypeCodeCache codeCache = AbstractTypeCode.locateCodeCache( a_TypeCode.getType() ) ;
        // Add the type code to that cache
        codeCache.putCode( a_TypeCode ) ;
    }

    
    /**
     * Locate the cached collection for the specified {@link TypeCode} class.
     * 
     * @param a_Type Type of code.
     * 
     * @return Corresponding cache collection.
     */
    @SuppressWarnings("unchecked")
    private static TypeCodeCache locateCodeCache( String a_Type )
    {
        // Validate
        Assert.notNull( a_Type, "Type code class cannot be null." ) ;

        TypeCodeCache result = (TypeCodeCache)AbstractTypeCode.Cache.get( a_Type ) ;

        // If the type code collection doesn't yet exist, create it
        if ( result == null ) {
            // Create new collection
            result = new TypeCodeCache( a_Type ) ;
            // Cache the new collection
            AbstractTypeCode.Cache.put( a_Type, result ) ;
        }

        return result ;
    }
    
    
    /**
     * Implementing classes can override this function to alter the time when
     * a new {@link TypeCode} is placed into the cache. If it returns <tt>true</tt>
     * the implementation class is then responsible for pushing the finished
     * object into the cache.
     * 
     * @return <tt>True</tt> if cache placement should be delayed upon instantiation.
     */
    protected boolean delayCache()
    {
        return false ;
    }
    
    
    /**
     * Put this instance in the cache.
     */
    public void cacheTypeCode()
    {
        // Only cache this instance once
        if ( !this.m_IsCached ) {
            AbstractTypeCode.putCode( this ) ;
            this.m_IsCached = true ;
        }
    }

    
    /**
     * Get the {@link TypeCode} for the specified type and value.
     * 
     * @param a_Type Type of {@link TypeCode}.
     * @param a_Value Value to locate.
     * 
     * @return Corresponding {@link AbstractTypeCode}.
     */
    public static TypeCode codeForValue( String a_Type, Object a_Value )
    {
        return AbstractTypeCode.locateCodeCache( a_Type ).findCode( a_Value ) ;
    }
    
    
    /**
     * Get the {@link TypeCode} for the specified class (the class name is the type) and value.
     * 
     * @param a_TypeCodeClass Class of {@link TypeCode}.
     * @param a_Value Value to locate.
     * 
     * @return Corresponding {@link AbstractTypeCode}.
     */
    public static TypeCode codeForValue( Class<?> a_TypeCodeClass, Object a_Value )
    {
        return AbstractTypeCode.codeForValue( a_TypeCodeClass.getClass().getName(), a_Value ) ;
    }

    
    /**
     * Get the {@link TypeCode} values, sorted by code.
     * 
     * @param a_Type Type of {@link TypeCode}.
     * 
     * @return Sorted {@link TypeCode}'s.
     */
    public static TypeCode[] sortByCode( String a_Type )
    {
        return AbstractTypeCode.locateCodeCache( a_Type ).sortByCode() ;
    }

    
    /**
     * Get the {@link TypeCode} values, sorted by code.
     * 
     * @param a_TypeCodeClass Class of {@link TypeCode}.
     * 
     * @return Sorted {@link TypeCode}'s.
     */
    public static TypeCode[] sortByCode( Class<?> a_TypeCodeClass )
    {
        return AbstractTypeCode.sortByCode( a_TypeCodeClass.getName() ) ;
    }
    
    
    /**
     * Get the {@link TypeCode} values, sorted by label.
     * 
     * @param a_Type Type of {@link TypeCode}.
     * 
     * @return Sorted {@link TypeCode}'s.
     */
    public static TypeCode[] sortByLabel( String a_Type )
    {
        return AbstractTypeCode.locateCodeCache( a_Type ).sortByLabel() ;
    }
    
    
    /**
     * Get the {@link TypeCode} values, sorted by label.
     * 
     * @param a_TypeCodeClass Class of {@link TypeCode}.
     * 
     * @return Sorted {@link TypeCode}'s.
     */
    public static TypeCode[] sortByLabel( Class<?> a_TypeCodeClass )
    {
        return AbstractTypeCode.sortByLabel( a_TypeCodeClass.getName() ) ;
    }

    
    /**
     * Get the {@link TypeCode} values, sorted by description.
     * 
     * @param a_Type Type of {@link TypeCode}.
     * 
     * @return Sorted {@link TypeCode}'s.
     */
    public static TypeCode[] sortByDescription( String a_Type )
    {
        return AbstractTypeCode.locateCodeCache( a_Type ).sortByDescription() ;
    }

    
    /**
     * Get the {@link TypeCode} values, sorted by description.
     * 
     * @param a_TypeCodeClass Class of {@link TypeCode}.
     * 
     * @return Sorted {@link TypeCode}'s.
     */
    public static TypeCode[] sortByDescription( Class<?> a_TypeCodeClass )
    {
        return AbstractTypeCode.sortByDescription( a_TypeCodeClass.getName() ) ;
    }

    
    /**
     * Determine if the specified code is defined.
     * 
     * @param a_Type Type of {@link TypeCode}.
     * @param a_CodeValue Code to locate.
     * 
     * @return <tt>True</tt> if the code is defined.
     */
    public static boolean isDefined( String a_Type, Object a_CodeValue )
    {
        return AbstractTypeCode.locateCodeCache( a_Type ).contains( a_CodeValue ) ;
    }

    
    /**
     * Determine if the specified code is defined.
     * 
     * @param a_TypeCodeClass Class of {@link TypeCode}.
     * @param a_CodeValue Code to locate.
     * 
     * @return <tt>True</tt> if the code is defined.
     */
    public static boolean isDefined( Class<?> a_TypeCodeClass, Object a_CodeValue )
    {
        return AbstractTypeCode.isDefined( a_TypeCodeClass.getName(), a_CodeValue ) ;
    }
    

    /**
     * Constructor when all elements are known.
     * 
     * @param a_Type Type value.
     * @param a_Code Code value.
     * @param a_Label Label value.
     * @param a_Description Description value.
     */
    public AbstractTypeCode( String a_Type, Object a_Code, String a_Label, String a_Description )
    {
        this.setType( a_Type ) ;
        this.setCode( a_Code ) ;
        this.setLabel( a_Label ) ;
        this.setDescription( a_Description ) ;
        
        if ( !this.delayCache() ) {
            AbstractTypeCode.putCode( this ) ;
            this.m_IsCached = true ;
        }
    }

    
    /**
     * Constructor when only the code and label are known for the {@link TypeCode}.
     * 
     * @param a_Type Type value.
     * @param a_Code Code value.
     * @param a_Label Label value.
     */
    protected AbstractTypeCode( String a_Type, Object a_Code, String a_Label )
    {
        this( a_Type, a_Code, a_Label, a_Label ) ;
    }
    
    
    /**
     * Constructor when all elements are known for the {@link TypeCode}, and
     * the type is based on class name.
     * 
     * @param a_TypeClass {@link TypeCode} class.
     * @param a_Code Code value.
     * @param a_Label Label value.
     * @param a_Description Description value.
     */
    protected AbstractTypeCode( Class<?> a_TypeClass, Object a_Code, String a_Label, String a_Description )
    {
        this( a_TypeClass.getName(), a_Code, a_Label, a_Description ) ;
    }
    
    
    /**
     * Constructor when only the code and label are known for the {@link TypeCode},
     * and the type is based on class name.
     * 
     * @param a_TypeClass
     * @param a_Code
     * @param a_Label
     */
    protected AbstractTypeCode( Class<?> a_TypeClass, Object a_Code, String a_Label ) 
    {
        this( a_TypeClass.getName(), a_Code, a_Label, a_Label ) ;
    }
    
    
    /**
     * @return Return the type.
     */
    public String getType()
    {
        return this.m_Type ;
    }
    
    
    /**
     * @param a_Type The type to set.
     */
    protected void setType( String a_Type )
    {
        this.m_Type = a_Type ;
    }

    
    /**
     * @return Returns the code.
     */
    public Object getCode()
    {
        return this.m_Code ;
    }


    /**
     * @param a_code The code to set.
     */
    protected void setCode( Object a_Code )
    {
        Assert.notNull( a_Code, "Code value cannot be null" ) ;
        
        this.m_Code = a_Code ;
    }


    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return this.m_Description ;
    }


    /**
     * @param a_description The description to set.
     */
    protected void setDescription( String a_Description )
    {
        Assert.notNull( a_Description, "Description cannot be null" ) ;
        
        this.m_Description = a_Description ;
    }


    /**
     * @return Returns the label.
     */
    public String getLabel()
    {
        return this.m_Label ;
    }


    /**
     * @param a_label The label to set.
     */
    protected void setLabel( String a_Label )
    {
        Assert.notNull( a_Label, "Label cannot be null" ) ;
        
        this.m_Label = a_Label ;
    }
    
    
    /*
     * Compare to another {@link AbstractTypeCode} instance.
     */
    @SuppressWarnings("unchecked")
    public int compareTo( Object a_AbstractTypeCode )
    {
        return ( (Comparable<Object>)this.getCode() ).compareTo( ( (AbstractTypeCode)a_AbstractTypeCode ).getCode() ) ;
    }


    /**
     * Make equals final so concrete implementations can't override and disrupt the
     * contract with this abstract.
     * 
     * A {@link TypeCode} is considered equal if the type and code attributes are
     * equal. This allows for different typecasts of the same {@link TypeCode} to
     * be equal.
     */
    public final boolean equals( Object a_Obj ) 
    {
        boolean result = false ;
        
        /*
         * Make sure this instance is properly defined (in case of delayed caching),
         * and that the passed in object is of the right class. 
         */
        if ( ( this.getType() != null ) && 
             ( this.getCode() != null ) &&
             ( AbstractTypeCode.class.isAssignableFrom( a_Obj.getClass() ) )
           ) {
            result = ( this.getType().equals( ((AbstractTypeCode)a_Obj).getType() ) ) &&
                     ( this.getCode().equals( ((AbstractTypeCode)a_Obj).getCode() ) ) ;
        }
        
        return result ;
    }

    
    /**
     * Make hashCode final so concrete implementations can't override and disrupt the
     * contract with this abstract.
     */
    public final int hashCode() 
    {
        return super.hashCode() ;
    }
    
    
    /**
     * Return display ready version of instance.
     */
    public String toString() 
    {
        StringBuffer result = new StringBuffer() ;
        String sep = System.getProperty( "line.separator" ) ;
        
        result.append( "<<" )
              .append( this.getClass().getName().substring( this.getClass().getPackage().getName().length() + 1 ) )
              .append( ">>::" )
              .append( sep )
              .append( "\t[type=" )
              .append( this.getType() )
              .append( ",code=" )
              .append( this.getCode() )
              .append( ",label=" )
              .append( this.getLabel() )
              .append( ",description=" )
              .append( this.getDescription() )
              .append( "]" )
              ;
        
        return result.toString() ;
    }

    
    /**
     * <b>TypeCodeCache</b> is used to hold a collection of {@link TypeCode} objects. It contains
     * utility methods for locating members and sorting the collection.
     * 
     * @author gcrider
     */
    public static class TypeCodeCache
    {
	/** Instance of logging for subclasses. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

        /** Map to hold type codes */
        private IterableMap m_Codes = null ;
        /** Unique name of this cache */
        private String m_CacheType = null ;

        
        /**
         * Constructor for a unique, type.
         * 
         * @param a_Type Type of cache.
         */
        public TypeCodeCache( String a_Type )
        {
            this.m_CacheType = a_Type ;
            this.m_Codes = new HashedMap() ;
        }

        
        /**
         * Located the {@link TypeCode} for the specified value.
         * 
         * @param a_CodeValue Value used for lookup.
         * 
         * @return Corresponding {@link TypeCode}.
         */
        public TypeCode findCode( Object a_CodeValue )
        {
            TypeCode result = (TypeCode)this.m_Codes.get( a_CodeValue ) ;

            if ( result == null ) {
		final String error = "Unable to locate TypeCode("
			+ this.m_CacheType + ") with CodeValue(" + a_CodeValue
			+ ")";
		log.error(error);
		throw new NoSuchElementException(error);
            }

            return result ;
        }

        
        /**
         * Does the specifed value exist?
         * 
         * @param a_CodeValue Value used for lookup.
         * 
         * @return <tt>True</tt> if found.
         */
        public boolean contains( Object a_CodeValue )
        {
            return this.m_Codes.containsKey( a_CodeValue ) ;
        }

        
        /**
         * Put the {@link TypeCode} in the cache. Duplicates are not permitted.
         * 
         * @param a_TypeCode {@link TypeCode} to be added.
         */
        @SuppressWarnings("unchecked")
        public synchronized void putCode( TypeCode a_TypeCode )
        {
            // Can only put the type code in once
            if ( this.m_Codes.containsKey( a_TypeCode.getCode() ) ) {
		final String error = "The value(" + a_TypeCode.getCode()
			+ ") already exists for TypeCode("
			+ a_TypeCode.getType() + ")";
		log.error(error);
		throw new IllegalArgumentException(error);
            }

            this.m_Codes.put( a_TypeCode.getCode(), a_TypeCode ) ;
        }

        
        /**
         * Perform a sort on the known {@link TypeCode}'s.
         * 
         * @param a_Comparator Comparator used in sort. If <tt>null</tt>, the natural order
         *        is used in the sort.
         *        
         * @return Sorted array of known {@link TypeCode}'s.
         */
        @SuppressWarnings("unchecked")
        private TypeCode[] sort( Comparator<Object> a_Comparator )
        {
            List<TypeCode> codeList = new ArrayList<TypeCode>( this.m_Codes.values() ) ;

            Collections.sort( codeList, a_Comparator ) ;

            return (TypeCode[])codeList.toArray( new TypeCode[codeList.size()] ) ;
        }

        
        /**
         * Return a {@link TypeCode} array, sorted by the code value.
         * 
         * @return Code value sorted array.
         */
        public TypeCode[] sortByCode()
        {
            return this.sort( null ) ;
        }

        
        /**
         * Return a {@link TypeCode} array, sorted by the label.
         * 
         * @return Label sorted array.
         */
        public TypeCode[] sortByLabel()
        {
            return this.sort( new Comparator<Object>() {
                public int compare( Object a_First, Object a_Second )
                {
                    String firstLabel = ( (TypeCode)a_First ).getLabel() ;
                    String secondLabel = ( (TypeCode)a_Second ).getLabel() ;
                    return firstLabel.compareTo( secondLabel ) ;
                }
            } ) ;
        }

        
        /**
         * Return a {@link TypeCode} array, sorted by the description.
         * 
         * @return Description sorted array.
         */
        public TypeCode[] sortByDescription()
        {
            return this.sort( new Comparator<Object>() {
                public int compare( Object a_First, Object a_Second )
                {
                    String firstDesc = ( (TypeCode)a_First ).getDescription() ;
                    String secondDesc = ( (TypeCode)a_Second ).getDescription() ;
                    return firstDesc.compareTo( secondDesc ) ;
                }
            } ) ;
        }

        
        /**
         * Return a display ready version of this instance.
         */
        public String toString()
        {
            StringBuffer result = new StringBuffer() ;
            String sep = System.getProperty( "line.separator" ) ;

            result.append( "TypeCodeCache::[type=" )
                  .append( this.m_CacheType )
                  .append( sep )
                  ;

            MapIterator it = this.m_Codes.mapIterator() ;
            while ( it.hasNext() ) {
                it.next() ;
                TypeCode tc = (TypeCode)it.getValue() ;
                result.append( "    <type=" )
                      .append( tc.getType() )
                      .append( ",)code=" )
                      .append( tc.getCode() )
                      .append( ",label=" )
                      .append( tc.getLabel() )
                      .append( ",description=" )
                      .append( tc.getDescription() )
                      .append( ">" )
                      .append( sep ) 
                      ;
            }

            result.append( "]" ) ;

            return result.toString() ;
        }

    }

}
