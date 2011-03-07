package org.ccci.gcx.idm.common.persist ;

import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>StaleObjectStateException</b> is used to report that a persisted object
 * could not be updated because another session has changed the underlying
 * data.
 *
 * @author Greg Crider  Nov 3, 2006  9:37:12 AM
 */
public class StaleObjectStateException extends IdmException
{
    private static final long serialVersionUID = 2430711060722767731L ;

    
    /**
     * Exception with message.
     * 
     * @param a_Message Exception message.
     */
    public StaleObjectStateException( String a_Message ) 
    {
        super( a_Message ) ;
    }

    
    /**
     * Exception with external cause.
     * 
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public StaleObjectStateException( String a_Message, Throwable a_Cause ) 
    {
        super( a_Message, a_Cause ) ;
    }

}
