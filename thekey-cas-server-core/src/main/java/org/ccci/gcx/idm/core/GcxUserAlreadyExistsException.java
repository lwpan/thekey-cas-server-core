package org.ccci.gcx.idm.core;

/**
 * <b>GcxUserAlreadyExistsException</b> is thrown when attempting to create a new
 * user that already exists.
 *
 * @author Greg Crider  Oct 29, 2008  9:48:32 AM
 */
public class GcxUserAlreadyExistsException extends GcxUserException
{
    private static final long serialVersionUID = 7228335305824410038L ;


    /**
     * When only an error message is known.
     * 
     * @param a_Message Error message.
     */
    public GcxUserAlreadyExistsException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * When caused by an underlying {@link Exception}.
     * 
     * @param a_Message Error message.
     * @param a_Exception Underlying {@link Exception}.
     */
    public GcxUserAlreadyExistsException( String a_Message, Exception a_Exception ) 
    {
        super( a_Message, a_Exception ) ;
    }

}
