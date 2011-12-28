package org.ccci.gcx.idm.common ;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>IdmException</b> is used to report one or more error messages within a single
 * unchecked {@link Exception}. In addition, certain meta data can be added in
 * order to allow the caller to have more post-processing information.
 *
 * @author Greg Crider  Aug 22, 2006  2:19:06 PM
 */
public class IdmException extends RuntimeException {
    private static final long serialVersionUID = 1900633246071420616L ;

    /** Hold list of individual exceptions */
    private List<String> m_ErrorList = null ;

    /** Meta data relevant to the underlying error */
    private Map<String,Object> m_MetaData = new HashMap<String,Object>() ;

    /** Original message of exception; does not include the enumerated error list */
    private String m_OriginalMessage = null ;

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
}
