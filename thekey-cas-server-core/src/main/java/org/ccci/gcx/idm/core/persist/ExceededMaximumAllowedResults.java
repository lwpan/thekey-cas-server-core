package org.ccci.gcx.idm.core.persist;

import org.ccci.gcx.idm.common.IdmException;

/**
 * <b>ExceededMaximumAllowedResults</b> is thrown whenever a search exceeds the
 * maximum allowed results.
 *
 * @author Greg Crider  Nov 19, 2008  7:17:21 PM
 */
public class ExceededMaximumAllowedResults extends IdmException
{
    private static final long serialVersionUID = -4906869593643904120L ;

    
    /**
     * When only an error message is known.
     * 
     * @param a_Message Error message.
     */
    public ExceededMaximumAllowedResults( String a_Message )
    {
        super( a_Message ) ;
    }

    
    /**
     * When caused by an underlying {@link Exception}.
     * 
     * @param a_Message Error message.
     * @param a_Exception Underlying {@link Exception}.
     */
    public ExceededMaximumAllowedResults( String a_Message, Exception a_Exception ) 
    {
        super( a_Message, a_Exception ) ;
    }

}
