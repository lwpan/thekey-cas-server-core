package org.ccci.gcx.idm.common ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <b>IdmException</b> is used to report one or more error messages within a single
 * unchecked {@link Exception}. In addition, certain meta data can be added in
 * order to allow the caller to have more post-processing information.
 *
 * @author Greg Crider  Aug 22, 2006  2:19:06 PM
 */
public class IdmException extends RuntimeException
{
    private static final long serialVersionUID = 1900633246071420616L ;

    private static String LINE_SEPARATOR = System.getProperty( "line.separator" ) ;

    /** Hold list of individual exceptions */
    private List<String> m_ErrorList = null ;

    /** Meta data relevant to the underlying error */
    private Map<String,Object> m_MetaData = new HashMap<String,Object>() ;

    /** Original message of exception; does not include the enumerated error list */
    private String m_OriginalMessage = null ;


    /**
     * Create a message containing the original message text and the subsequent validation
     * errors.
     *
     * @param a_Message Original message text.
     * @param a_Errors {@link List} of errors.
     *
     * @return Combined message.
     */
    private static String createErrorMessage( String a_Message, List<String> a_Errors )
    {
        StringBuffer result = new StringBuffer() ;

        result.append( a_Message )
              .append( IdmException.LINE_SEPARATOR )
              .append( "The following errors were encountered:" )
              ;

        Iterator<String> it = a_Errors.iterator() ;
        for( int i=0; it.hasNext(); i++ ) {
            result.append( IdmException.LINE_SEPARATOR )
                  .append( "[" )
                  .append( i )
                  .append( "] - ")
                  .append( it.next() )
                  ;
        }

        result.append( IdmException.LINE_SEPARATOR ) ;

        return result.toString() ;
    }


    /**
     * Get the list of errors.
     *
     * @return {@link List} of error messages.
     */
    public List<String> getErrors()
    {
        return this.m_ErrorList ;
    }


    /**
     * Are there any enumerated errors?
     *
     * @return <tt>True</tt> if there is a list of errors available.
     */
    public boolean hasErrors()
    {
        return ( ( this.m_ErrorList != null ) && ( this.m_ErrorList.size() > 0 ) ) ;
    }


    /**
     * Return the meta data.
     *
     * @return {@link Map} of meta data.
     */
    public Map<String,Object> getMetaData()
    {
        return this.m_MetaData ;
    }


    /**
     * Add a new meta data value.
     *
     * @param a_Key Key to meta data.
     * @param a_Value Value of meta data.
     */
    public void addMetaData( String a_Key, Object a_Value )
    {
        this.m_MetaData.put( a_Key, a_Value ) ;
    }


    /**
     * Is there any meta data?
     *
     * @return <tt>True</tt> if there is meta data present.
     */
    public boolean hasMetaData()
    {
        return ( this.m_MetaData.size() > 0 ) ;
    }


    /**
     * Return the original message passed into the exception.
     *
     * @return Original message.
     */
    public String getOriginalMessage()
    {
        return this.m_OriginalMessage ;
    }


    /**
     * Exception with message.
     *
     * @param a_Message Exception message.
     */
    public IdmException( String a_Message )
    {
        super( a_Message ) ;

        this.m_OriginalMessage = a_Message ;
    }


    /**
     * Exception with external cause.
     *
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public IdmException( String a_Message, Throwable a_Cause )
    {
        super( a_Message, a_Cause ) ;

        this.m_OriginalMessage = a_Message ;
    }


    /**
     * Exception with external cause.
     *
     * @param a_Message Exception message.
     * @param a_Errors {@link List} of validation error messages.
     */
    public IdmException( String a_Message, List<String> a_Errors )
    {
        super( IdmException.createErrorMessage( a_Message, a_Errors ) ) ;

        this.m_ErrorList = a_Errors ;

        this.m_OriginalMessage = a_Message ;
    }


    /**
     * Exception with external cause.
     *
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     * @param a_Errors {@link List} of validation error messages.
     */
    public IdmException( String a_Message, Throwable a_Cause, List<String> a_Errors )
    {
        super( IdmException.createErrorMessage( a_Message, a_Errors ), a_Cause ) ;

        this.m_ErrorList = a_Errors ;
        this.m_OriginalMessage = a_Message ;
    }

}
