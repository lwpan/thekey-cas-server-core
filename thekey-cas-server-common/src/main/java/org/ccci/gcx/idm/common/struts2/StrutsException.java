package org.ccci.gcx.idm.common.struts2;

import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>StrutsException</b> is the base {@link IdmException} for errors
 * occurring within a Struts2 based utility class.
 *
 * @author Greg Crider  Feb 27, 2008  12:24:08 PM
 */
public class StrutsException extends IdmException
{
    private static final long serialVersionUID = -2499481604617830141L ;


    /**
     * Exception with message.
     * 
     * @param a_Message Exception message.
     */
    public StrutsException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * Exception with external cause.
     * 
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public StrutsException( String a_Message, Throwable a_Cause )
    {
        super( a_Message, a_Cause ) ;
    }

}
