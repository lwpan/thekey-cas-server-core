package org.ccci.gcx.idm.common.service;

import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>BusinessServiceException</b> is the base {@link IdmException} for errors
 * occurring within a concrete {@link BusinessService}.
 *
 * @author Greg Crider  Mar 12, 2007  3:08:53 PM
 */
public class BusinessServiceException extends IdmException
{
    private static final long serialVersionUID = 4748733035439984309L ;


    /**
     * Exception with message.
     * 
     * @param a_Message Exception message.
     */
    public BusinessServiceException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * Exception with external cause.
     * 
     * @param a_Message Exception message.
     * @param a_Cause External cause of exception.
     */
    public BusinessServiceException( String a_Message, Throwable a_Cause )
    {
        super( a_Message, a_Cause ) ;
    }

}
