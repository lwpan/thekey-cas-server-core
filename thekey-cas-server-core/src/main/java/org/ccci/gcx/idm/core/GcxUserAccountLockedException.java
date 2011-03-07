package org.ccci.gcx.idm.core;

/**
 * <b>GcxUserAccountLockedException</b> is thrown when attempting to authenticate a user whose
 * account is locked or disabled.
 *
 * @author Greg Crider  Nov 3, 2008  5:36:23 PM
 */
public class GcxUserAccountLockedException extends GcxUserException
{
    private static final long serialVersionUID = -4649772629910228481L ;


    /**
     * When only an error message is known.
     * 
     * @param a_Message Error message.
     */
    public GcxUserAccountLockedException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * When caused by an underlying {@link Exception}.
     * 
     * @param a_Message Error message.
     * @param a_Exception Underlying {@link Exception}.
     */
    public GcxUserAccountLockedException( String a_Message, Exception a_Exception ) 
    {
        super( a_Message, a_Exception ) ;
    }

}
