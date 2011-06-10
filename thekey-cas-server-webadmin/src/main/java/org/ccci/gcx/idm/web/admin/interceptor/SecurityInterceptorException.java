package org.ccci.gcx.idm.web.admin.interceptor;

import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>SecurityInterceptorException</b> is thrown when the {@link SecurityInterceptor} detects
 * an unauthorized access attempt.
 *
 * @author Greg Crider  Nov 6, 2008  3:43:08 PM
 */
public class SecurityInterceptorException extends IdmException
{
    private static final long serialVersionUID = -7583933434373639501L ;


    /**
     * Exception with message.
     * 
     * @param a_Message Exception message.
     */
    public SecurityInterceptorException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * Exception with external cause.
     * 
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public SecurityInterceptorException( String a_Message, Throwable a_Cause )
    {
        super( a_Message, a_Cause ) ;
    }

}
