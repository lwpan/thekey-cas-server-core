package org.ccci.gcx.idm.common.service;

import java.util.List;

/**
 * <b>BusinessResponse</b> defines a response returned by any implementation
 * of a {@link BusinessService}.
 *
 * @author Greg Crider  Oct 20, 2006  1:05:22 PM
 */
public class BusinessResponse 
{
    private List<Object> m_Errors = null ;

    /**
     * Does the response contain errors?
     * 
     * @return <tt>True</tt> if there are errors.
     */
    public boolean hasErrors()
    {
        return ( ( this.m_Errors != null ) && ( this.m_Errors.size() > 0 ) ) ;
    }

    
    /**
     * @return the errors
     */
    public List<Object> getErrors()
    {
        return this.m_Errors ;
    }

    /**
     * @param a_errors the errors to set
     */
    public void setErrors( List<Object> a_errors )
    {
        this.m_Errors = a_errors ;
    }

}
