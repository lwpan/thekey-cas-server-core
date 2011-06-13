package org.ccci.gcx.idm.web.admin.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.util.StrutsTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>ListMultiLineTextConverter</b> is used to convert {@link List<String>} to and
 * from a multiline text block. This can be used to convert {@link List<String>} for textarea
 * tags within the view.
 *
 * @author Greg Crider  Nov 26, 2008  5:38:34 PM
 */
public class ListMultiLineTextConverter extends StrutsTypeConverter
{
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    
    /**
     * Convert {@link String} objects into their corresponding {@link UserRoleTypeCode} objects.
     * 
     * @param a_Context The action context
     * @param a_Values Submitted values to be converted
     * @param a_ToClass Class to convert to
     * 
     * @return Converted object
     * 
     * @see org.apache.struts2.util.StrutsTypeConverter#convertFromString(java.util.Map, java.lang.String[], java.lang.Class)
     */
    @Override
    public Object convertFromString( Map a_Context, String[] a_Values, Class a_ToClass )
    {
        List<String> result = null ;
        
        if ( StringUtils.isNotBlank( a_Values[0] ) ) {
            String value = StringUtils.remove( a_Values[0], "\n" ) ;
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Converting String: (" + value + ")" ) ;
            result = Arrays.asList( StringUtils.split( value, "\r" ) ) ;
        } else {
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Nothing to convert" ) ;
        }
        
        return result ;
    }

    
    /**
     * Convert {@link UserRoleTypeCode} into a {@link String}.
     * 
     * @param a_Context The action context
     * @param a_ValueToConvert Value to be converted into a string
     * 
     * @return String value of converted object
     * 
     * @see org.apache.struts2.util.StrutsTypeConverter#convertToString(java.util.Map, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String convertToString( Map a_Context, Object a_ValueToConvert )
    {
        String result = "" ;
        List<String> values = (List<String>)a_ValueToConvert ;
        
        if ( ( values != null ) && ( values.size() > 0 ) ) {
            result = StringUtils.join( values.toArray(), "\r" ) ;
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Converted To: (" + result + ")" ) ;
        } else {
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Nothing to convert" ) ;
        }
        
        return result ;
    }

}
