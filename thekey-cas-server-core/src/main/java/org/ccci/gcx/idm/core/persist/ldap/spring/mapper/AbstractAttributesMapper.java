package org.ccci.gcx.idm.core.persist.ldap.spring.mapper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.util.GeneralizedTime;
import org.springframework.ldap.core.AttributesMapper;

/**
 * <b>AbstractAttributesMapper</b>
 *
 * @author Greg Crider  Oct 28, 2008  2:35:41 PM
 */
public abstract class AbstractAttributesMapper implements AttributesMapper
{
    protected static final Log log = LogFactory.getLog( AbstractAttributesMapper.class ) ;

    /**
     * Get the specified attribute safely, in case it isn't present, and would
     * otherwise throw a {@link NullPointerException}.
     * 
     * @param a_Attributes {@link Attributes} returned from the LDAP server.
     * @param a_Name Name of {@link Attribute} to retrieve.
     * 
     * @return Recovered {@link Attribute} or <tt>null</tt> if not present.
     * 
     * @throws NamingException if the specified attribute name is not found
     */
    public Object getAttributeSafely( Attributes a_Attributes, String a_Name ) throws NamingException
    {
        Object result = null ;
        
        if ( a_Attributes.get( a_Name ) != null ) {
            result = a_Attributes.get( a_Name ).get() ;
        }
        
        return result ;
    }
    
    
    /**
     * Get the specified generalized time attribute safely, in case it isn't present, and would
     * otherwise throw a {@link NullPointerException}. If the underlying timestamp found in the
     * {@link Attributes} is invalid, this method will simply return a <tt>null</tt>
     * 
     * @param a_Attributes {@link Attributes} returned from the LDAP server.
     * @param a_Name Name of {@link Attribute} to retrieve.
     * 
     * @return {@link Date} version of specified {@link Attribute}.
     * 
     * @throws NamingException if the specified attribute name is not found
     */
    public Date getGeneralizedTimeAttributeSafely( Attributes a_Attributes, String a_Name ) throws NamingException
    {
        Date result = null ;
        
        String loginTime = (String)this.getAttributeSafely( a_Attributes, a_Name ) ;
        
        if ( StringUtils.isNotBlank( loginTime ) ) {
            try {
                GeneralizedTime gt = new GeneralizedTime( loginTime ) ;
                result = gt.getCalendar().getTime() ;
            } catch ( ParseException pe ) {
                /*= WARN =*/ if ( log.isWarnEnabled() ) log.warn( "Unable to convert generalized time \"" + loginTime + "\"." ) ;
            }
        }
        
        return result ;
    }
    
    
    /**
     * Get the specified attribute lsit safely, in case it isn't present, and would
     * otherwise throw a {@link NullPointerException}.
     * 
     * @param a_Attributes {@link Attributes} returned from the LDAP server.
     * @param a_Name Name of {@link Attribute} list to retrieve.
     * 
     * @return Recovered {@link Attribute} list or <tt>null</tt> if not present.
     * 
     * @throws NamingException if the specified attribute name is not found
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getAllAttributeStringSafely( Attributes a_Attributes, String a_Name ) throws NamingException
    {
        ArrayList<String> result = null ;
        
        if ( a_Attributes.get( a_Name ) != null ) {
            result = (ArrayList<String>)Collections.list( a_Attributes.get( a_Name ).getAll() ) ;
        }
        
        return result ;
    }
    
    
    /**
     * Map the specified {@link Attributes} to the underlying domain model.
     * 
     * @param a_Attributes {@link Attributes} returned from the LDAP server.
     * 
     * @return Bound domain model based on specified {@link Attributes}.
     * 
     * @throws NamingException if a directory naming error occurs.
     * 
     * @see org.springframework.ldap.core.AttributesMapper#mapFromAttributes(javax.naming.directory.Attributes)
     */
    public abstract Object mapFromAttributes( Attributes a_Attributes ) throws NamingException ;

}
