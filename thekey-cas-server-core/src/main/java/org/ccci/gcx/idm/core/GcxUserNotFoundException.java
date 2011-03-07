package org.ccci.gcx.idm.core;

/**
 * <b>GcxUserNotFoundException</b> is thrown when attempting to authenticate a non-existent user.
 *
 * @author Greg Crider  Nov 10, 2008  4:36:46 PM
 */
public class GcxUserNotFoundException extends GcxUserException
{
    private static final long serialVersionUID = -4446423208648383247L ;


    /**
     * When only an error message is known.
     * 
     * @param a_Message Error message.
     */
    public GcxUserNotFoundException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * When caused by an underlying {@link Exception}.
     * 
     * @param a_Message Error message.
     * @param a_Exception Underlying {@link Exception}.
     */
    public GcxUserNotFoundException( String a_Message, Exception a_Exception ) 
    {
        super( a_Message, a_Exception ) ;
    }

}
