package org.ccci.gcx.idm.common ;

/**
 * <b>IdmException</b> is used to report one or more error messages within a single
 * unchecked {@link Exception}. In addition, certain meta data can be added in
 * order to allow the caller to have more post-processing information.
 *
 * @author Greg Crider  Aug 22, 2006  2:19:06 PM
 */
public class IdmException extends RuntimeException {
    private static final long serialVersionUID = 1900633246071420616L ;

    /**
     * Exception with message.
     *
     * @param a_Message Exception message.
     */
    public IdmException( String a_Message )
    {
        super( a_Message ) ;
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
    }
}
