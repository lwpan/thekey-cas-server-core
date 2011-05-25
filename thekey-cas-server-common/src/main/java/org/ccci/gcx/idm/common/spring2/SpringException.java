package org.ccci.gcx.idm.common.spring2;

import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>SpringException</b> is the base {@link IdmException} for errors
 * occurring within a Spring based utility class.
 *
 * @author Greg Crider  Mar 12, 2007  3:12:31 PM
 */
public class SpringException extends IdmException
{
    private static final long serialVersionUID = 4131696450647736486L ;


    /**
     * Exception with message.
     * 
     * @param a_Message Exception message.
     */
    public SpringException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * Exception with external cause.
     * 
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public SpringException( String a_Message, Throwable a_Cause )
    {
        super( a_Message, a_Cause ) ;
    }
    
}
