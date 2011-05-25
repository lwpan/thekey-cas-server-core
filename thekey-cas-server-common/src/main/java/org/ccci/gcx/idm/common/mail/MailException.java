package org.ccci.gcx.idm.common.mail;

import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>MailException</b> is used to indicated an error condition within
 * any of the mail related classes.
 *
 * @author Greg Crider  December 1, 2008  1:03:57 PM
 */
public class MailException extends IdmException
{
    private static final long serialVersionUID = -7008424304887974904L ;


    /**
     * Exception with message.
     * 
     * @param a_Message Exception message.
     */
    public MailException( String a_Message ) 
    {
        super( a_Message ) ;
    }

    
    /**
     * Exception with external cause.
     * 
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public MailException( String a_Message, Throwable a_Cause ) 
    {
        super( a_Message, a_Cause ) ;
    }

}
