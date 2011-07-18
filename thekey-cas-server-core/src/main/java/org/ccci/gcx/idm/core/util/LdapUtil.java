package org.ccci.gcx.idm.core.util;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>LdapUtil</b> contains utility methods for use with LDAP related operations.
 *
 * @author Greg Crider  Nov 3, 2008  5:04:23 PM
 */
public class LdapUtil
{
    protected static final Log log = LogFactory.getLog( LdapUtil.class ) ;

    /**
     * Safely dump the specified {@link Attributes} to a display ready format, but
     * redact sensitive information so it won't appear in log files.
     * 
     * @param a_Attributes {@link Attributes} to be formatted.
     * 
     * @return Display ready format.
     */
    public static String attributesToString( Attributes a_Attributes )
    {
        StringBuffer result = new StringBuffer() ;
        
        result.append( "<<Attributes>>::[" ) ;
        
        if ( a_Attributes != null ) try {
            NamingEnumeration<String> ids = a_Attributes.getIDs() ;
            while( ids.hasMore() ) {
                String id = ids.next() ;
                String value = a_Attributes.get( id ).toString() ;
                if ( id.toLowerCase().matches( ".*password.*" ) ) {
                    value = "**redacted**" ;
                }
                if ( !result.substring( result.length() -1 ).equals( "[" ) ) {
                    result.append(  "," ) ;
                }
                result.append( id ) 
                      .append( "=" )
                      .append( value ) ;
            }
        } catch ( NamingException ne ) {
		log.error("Unable to convert attributes to a String; attempting to continue.");
        }
        
        result.append( "]" ) ;
        
        return result.toString() ;
    }
}
