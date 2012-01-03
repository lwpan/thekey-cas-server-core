package org.ccci.gcx.idm.core;

/**
 * <b>GcxUserException</b> is thrown whenever an error occurs with a GUID or E-mail address. This is primarily
 * a constraint violation when the same GUID/E-mail value is attempted to be used for two different
 * users.
 *
 * @author Greg Crider  Oct 22, 2008  6:12:37 PM
 */
public class GcxUserException extends Exception {
    private static final long serialVersionUID = -4906869593643904120L ;

    /**
     * When only an error message is known.
     * 
     * @param a_Message Error message.
     */
    public GcxUserException( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * When caused by an underlying {@link Exception}.
     * 
     * @param a_Message Error message.
     * @param a_Exception Underlying {@link Exception}.
     */
    public GcxUserException( String a_Message, Exception a_Exception ) 
    {
        super( a_Message, a_Exception ) ;
    }
}
