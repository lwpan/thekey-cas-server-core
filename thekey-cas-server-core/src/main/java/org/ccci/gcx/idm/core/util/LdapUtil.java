package org.ccci.gcx.idm.core.util;

import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>LdapUtil</b> contains utility methods for use with LDAP related operations.
 *
 * @author Greg Crider  Nov 3, 2008  5:04:23 PM
 */
public class LdapUtil
{
    protected static final Log log = LogFactory.getLog( LdapUtil.class ) ;

    
    /**
     * Generate and LDAP dn based on a substitution pattern, similar to that of message resources
     * used in web applications.
     * 
     * @param a_Object Data object whose properties are used for substitution in the pattern.
     * @param a_DNPattern Pattern used for generating dn.
     * @param a_SubstitutionProperties Properties found in <tt>a_Object</tt> in the order in
     *        which they should be substituted.
     *        
     * @return Generated LDAP dn.
     * <p>
     * To use this class method, take the following example assuming that <tt>a_Object</tt> is an
     * instance of {@link GcxUser}:
     * <p>
     * <pre>
     *    a_DNPattern = "cn={0},ou=sso,dc=mygcx,dc=org"
     *    a_SubstitutionProperties = [ "email" ]
     * </pre>
     * <p>
     * will return a string similar to:
     * <p>
     * <pre>
     *    cn=gcrider@me.my,ou=sso,dc=mygcx,dc=org
     * </pre>
     * <p>
     * <b>Notes:</b>
     * <ul>
     * <li> Substitution variables are indexed from 0 to <tt>n</tt>, or <tt>{0}</tt> .. <tt>{n}</tt>.
     * <li> An indexed substitution variable can only appear once in the pattern. If you need to use
     * a property more than once, create seperate indexed variables and include the substitution
     * property as many times as necessary.
     * </ul>
     */
    public static String generateModelDNFromPattern( Object a_Object, String a_DNPattern, List<String> a_SubstitutionProperties )
    {
        String result = a_DNPattern ;

        if ( ( a_SubstitutionProperties != null ) && ( a_SubstitutionProperties.size() > 0 ) ) {
            for( int i=0; i<a_SubstitutionProperties.size(); i++ ) {
                try {
                    result = StringUtils.replace( 
                                result, 
                                "{" + i + "}", 
                                BeanUtils.getProperty( a_Object, a_SubstitutionProperties.get( i ) ),
                                -1
                            ) ;
                } catch ( Exception e ) {
                    String error = "Unable to get property \"" + a_SubstitutionProperties.get( i ) + 
                                   "\" from object of class type \"" + a_Object.getClass().getName() + "\"." ;
                    /*= ERROR =*/ log.error( error, e ) ;
                    throw new IdmException( error, e ) ;
                }
            }
        }
        
        return result ;
    }
    
    
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
            /*= ERROR =*/ log.error( "Unable to convert attributes to a String; attempting to continue." ) ;
        }
        
        result.append( "]" ) ;
        
        return result.toString() ;
    }
    
}
