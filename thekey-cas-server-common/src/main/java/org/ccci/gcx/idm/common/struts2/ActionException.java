package org.ccci.gcx.idm.common.struts2;

/**
 * <b>ActionException</b> is raised when an error occurs within a base
 * action class.
 *
 * @author Greg Crider  Feb 27, 2008  12:27:06 PM
 */
public class ActionException extends StrutsException
{
    private static final long serialVersionUID = -1386733337060212085L ;


    /**
     * Exception with message.
     * 
     * @param a_Message Exception message.
     */
    public ActionException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * Exception with external cause.
     * 
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public ActionException( String a_Message, Throwable a_Cause )
    {
        super( a_Message, a_Cause ) ;
    }

}
