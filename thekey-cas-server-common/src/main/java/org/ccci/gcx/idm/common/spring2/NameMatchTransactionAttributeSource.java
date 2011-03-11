package org.ccci.gcx.idm.common.spring2 ;

import java.io.Serializable ;
import java.lang.reflect.Method ;
import java.util.Iterator ;
import java.util.Map ;
import java.util.Properties ;

import org.apache.commons.collections.OrderedMap ;
import org.apache.commons.collections.OrderedMapIterator ;
import org.apache.commons.collections.map.ListOrderedMap ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource ;
import org.springframework.transaction.interceptor.TransactionAttribute ;
import org.springframework.transaction.interceptor.TransactionAttributeEditor ;
import org.springframework.transaction.interceptor.TransactionAttributeSource ;

/**
 * <b>NameMatchTransactionAttributeSource</b> is an implementation of
 * {@link TransactionAttributeSource}. Attributes are defined by a method name.
 * The method name can be an exact match of the form
 * <code>ClassName.MethodName</code>, or it may involve pattern matching.
 * 
 * This attribute source carries the notion of a global source which may serve
 * as a default setting. When creating a new attribute source, you may set the
 * <code>globalSource</code> property with the bean serving as its parent. The
 * new attribute source will always place its attributes before those found in
 * the global source, so they take precedence.
 * 
 * When attempting to retrieve a specific attribute, the following lookup steps
 * are taken.
 * 
 * <ol>
 * <li> Check for an exact match based on the <code>ClassName.MethodName</code>,
 * <li> Check for a best match, based on a pattern.
 * <li> Return <code>null</code> if there are no matches.
 * </ol>
 * 
 * TODO: This should be changed so that when a global source is specified, the
 * new properties are not added to it. When looking for an attribute, the new
 * properties should be checked, and then call the global source to check its
 * properties, and recurse back to the top, mac-daddy global source.
 * 
 * @see #isMatch
 * @see MethodMapTransactionAttributeSource
 * 
 * @author Greg Crider Oct 12, 2006 2:36:36 PM
 */
public class NameMatchTransactionAttributeSource implements TransactionAttributeSource, Serializable
{
    private static final long serialVersionUID = 8725894757778551891L ;

    /** Static for optimal serialization */
    protected static final Log log = LogFactory.getLog( NameMatchTransactionAttributeSource.class ) ;

    /**
     * Keys are method names; values are TransactionAttributes; use OrderedMap
     * to keep order of attributes as they are added.
     */
    private ListOrderedMap m_Namemap = new ListOrderedMap() ;

    /** Hold the attributes from the global source parent */
    private TransactionAttributeSource m_GlobalSource = null ;

    
    /**
     * Set a name/attribute map, consisting of method names (e.g. "myMethod")
     * and TransactionAttribute instances. These name/attributes are placed
     * before any existing ones.
     * 
     * @param a_NameMap Map of named methods
     * 
     * @see TransactionAttribute
     * @see TransactionAttributeEditor
     */
    protected void setNameMap( OrderedMap a_NameMap )
    {
        OrderedMapIterator it = a_NameMap.orderedMapIterator() ;

        while ( it.hasNext() ) {
            it.next() ;
            String methodName = (String)it.getKey() ;
            Map.Entry entry = (Map.Entry)it.getValue() ;

            // Check whether we need to convert from String to
            // TransactionAttribute.
            TransactionAttribute attr = null ;
            if ( entry.getValue() instanceof TransactionAttribute ) {
                attr = (TransactionAttribute)entry.getValue() ;
            } else {
                TransactionAttributeEditor editor = new TransactionAttributeEditor() ;
                editor.setAsText( entry.getValue().toString() ) ;
                attr = (TransactionAttribute)editor.getValue() ;
            }

            // Put local, injected methods first for pattern matching lookup
            this.addTransactionalMethod( methodName, attr, false ) ;
        }
    }
    

    /**
     * Parses the given properties into a name/attribute map. Expects method
     * names as keys and String attributes definitions as values, parsable into
     * TransactionAttribute instances via TransactionAttributeEditor.
     * 
     * @param a_TransactionAttributes Transaction attribute properties.
     * 
     * @see #setNameMap
     * @see TransactionAttributeEditor
     */
    public void setProperties( Properties a_TransactionAttributes )
    {
        TransactionAttributeEditor tae = new TransactionAttributeEditor() ;
        
        for( Iterator<Object> it = a_TransactionAttributes.keySet().iterator(); it.hasNext(); ) {
            String methodName = (String)it.next() ;
            String value = a_TransactionAttributes.getProperty( methodName ) ;
            tae.setAsText( value ) ;
            TransactionAttribute attr = (TransactionAttribute)tae.getValue() ;
            this.addTransactionalMethod( methodName, attr ) ;
        }
    }

    
    /**
     * Add an attribute for a transactional method. Method names can end with
     * "*" for matching multiple methods.
     * 
     * @param a_MethodName The name of the method
     * @param a_Attr Attribute associated with the method
     * @param a_Append If <tt>true</tt> then add to the end of the ordered map.
     */
    public void addTransactionalMethod( String a_MethodName, TransactionAttribute a_Attr, boolean a_Append )
    {
        /*= DEBUG =*/ if ( log.isDebugEnabled() ) log.debug( "Adding transactional method [" + a_MethodName + "] with attribute [" + a_Attr + "]" ) ;

        if ( a_Append ) {
            this.m_Namemap.put( a_MethodName, a_Attr ) ;
        } else {
            this.m_Namemap.put( 0, a_MethodName, a_Attr ) ;
        }

        if ( ( this.getGlobalSource() != null ) && ( this.getGlobalSource() instanceof NameMatchTransactionAttributeSource ) ) {
            // Inject the name mapping into the global source; make sure they
            // get added before the existing globals
            ( (NameMatchTransactionAttributeSource)this.getGlobalSource() ).addTransactionalMethod( a_MethodName, a_Attr, false ) ;
        }
    }

    
    /**
     * Add an attribute for a transactional method.
     * 
     * @param a_MethodName The name of the method.
     * @param a_Attr Attribute associated with the method.
     */
    public void addTransactionalMethod( String a_MethodName, TransactionAttribute a_Attr )
    {
        this.addTransactionalMethod( a_MethodName, a_Attr, false ) ;
    }

    /**
     * Get the {@link TransactionAttribute} for the specified method and class.
     * 
     * @param a_Method Method whose transaction attribute is required.
     * @param a_TargetClass Class where the method is to be found.
     * 
     * @return Corresponding {@link TransactionAttribute}.
     * 
     * @see org.springframework.transaction.interceptor.TransactionAttributeSource#getTransactionAttribute(java.lang.reflect.Method, java.lang.Class)
     */
    public TransactionAttribute getTransactionAttribute(Method a_Method,
	    Class<?> a_TargetClass)
    {
        TransactionAttribute result = null ;
        
        // look for direct name match
        String methodName = a_TargetClass.getName() + "." + a_Method.getName() ;
        result = (TransactionAttribute)this.m_Namemap.get( methodName ) ;

        if ( result == null ) {
            // Look for the best name match by checking the current sources
            // internal mapping
            // using the original order specified in the context
            String bestNameMatch = null ;
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "NameMap: size(" + this.m_Namemap.size() + ")" ) ;
            // Check map in the specified order
            for( OrderedMapIterator it = this.m_Namemap.orderedMapIterator(); it.hasNext(); ) {
                it.next() ;
                String mappedName = (String)it.getKey() ;
                if ( ( this.isMatch( methodName, mappedName ) )
                        && ( bestNameMatch == null || bestNameMatch.length() <= mappedName.length() ) ) {
                    result = (TransactionAttribute)it.getValue() ;
                    bestNameMatch = mappedName ;
                }
            }
            // If nothing is found, check against the global source, if there is
            // one
            if ( ( bestNameMatch == null ) && ( this.getGlobalSource() != null )
                    && ( this.getGlobalSource() instanceof NameMatchTransactionAttributeSource )
                    && ( ( (NameMatchTransactionAttributeSource)this.getGlobalSource() ).getNameMap() != null ) ) {
                /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "Nothing found in the current, internal best name match; checking global source" ) ;
                ListOrderedMap globalNameMap = ( (NameMatchTransactionAttributeSource)this.getGlobalSource() )
                        .getNameMap() ;
                /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "GlobalSource.NameMap: size(" + globalNameMap.size() + ")" ) ;
                for( OrderedMapIterator it = globalNameMap.orderedMapIterator(); it.hasNext(); ) {
                    it.next() ;
                    String mappedName = (String)it.getKey() ;
                    if ( this.isMatch( methodName, mappedName )
                            && ( bestNameMatch == null || bestNameMatch.length() <= mappedName.length() ) ) {
                        result = (TransactionAttribute)it.getValue() ;
                        bestNameMatch = mappedName ;
                    }
                }
            }
        }

        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "Method " + methodName + " is matched with transactional attribute " + result ) ;

        return result ;
    }

    /**
     * Return if the given method name matches the mapped name. The default
     * implementation checks for "xxx*" and "*xxx" matches. Can be overridden in
     * subclasses.
     * 
     * @param a_MethodName The method name of the class
     * @param a_MappedName The name in the descriptor
     * 
     * @return if the names match
     */
    protected boolean isMatch( String a_MethodName, String a_MappedName )
    {
        return a_MethodName.matches( a_MappedName ) ;
    }

    
    /**
     * @return Returns the globalSource.
     */
    public TransactionAttributeSource getGlobalSource()
    {
        return this.m_GlobalSource ;
    }

    
    /**
     * @param a_globalSource The globalSource to set.
     */
    public void setGlobalSource( TransactionAttributeSource a_globalSource )
    {
        this.m_GlobalSource = a_globalSource ;
        if ( getNameMap() != null && a_globalSource instanceof NameMatchTransactionAttributeSource ) {
            // Inject the map into the global source
            ( (NameMatchTransactionAttributeSource)a_globalSource ).setNameMap( getNameMap() ) ;
        }
    }

    
    /**
     * @return Returns the nameMap.
     */
    protected ListOrderedMap getNameMap()
    {
        return this.m_Namemap ;
    }

}
