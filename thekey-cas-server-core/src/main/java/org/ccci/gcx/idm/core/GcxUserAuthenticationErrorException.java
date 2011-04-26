package org.ccci.gcx.idm.core;

/**
 * <b>GcxUserAuthenticationErrorException</b> is thrown when there is an error authenticating a user
 * because of an invalid password.
 *
 * @author Greg Crider  Nov 3, 2008  5:34:29 PM
 */
public class GcxUserAuthenticationErrorException extends GcxUserException
{
    private static final long serialVersionUID = -638727036159259466L ;


    /**
     * When only an error message is known.
     * 
     * @param a_Message Error message.
     */
    public GcxUserAuthenticationErrorException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * When caused by an underlying {@link Exception}.
     * 
     * @param a_Message Error message.
     * @param a_Exception Underlying {@link Exception}.
     */
    public GcxUserAuthenticationErrorException( String a_Message, Exception a_Exception ) 
    {
        super( a_Message, a_Exception ) ;
    }

}
