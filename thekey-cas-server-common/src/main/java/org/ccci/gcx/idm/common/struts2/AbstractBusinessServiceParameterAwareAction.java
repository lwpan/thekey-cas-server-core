package org.ccci.gcx.idm.common.struts2;

import java.util.Date;
import java.util.Map;

import org.apache.struts2.interceptor.ParameterAware;

/**
 * <b>AbstractBusinessServiceParameterAwareAction</b> contains common functionality for Struts2
 * action classes using the {@link ParameterAware} interface to map all request parameters
 * to a {@link Map}.
 *
 * @author Greg Crider  Feb 1, 2008  12:04:29 PM
 */
public abstract class AbstractBusinessServiceParameterAwareAction 
    extends AbstractBusinessServicePreparableAction implements ParameterAware
{
    private static final long serialVersionUID = -8801358146302773086L ;

    private Map m_Parameters = null ;

    
    /**
     * @param a_parameters the parameters to set
     */
    public void setParameters( Map a_parameters )
    {
        this.m_Parameters = a_parameters ;
    }
    
    
    protected Object getParameter( String a_Name )
    {
        return this.m_Parameters.get( a_Name ) ;
    }
    protected Integer getParameterInteger( String a_Name )
    {
        return (Integer)this.m_Parameters.get( a_Name ) ;
    }
    protected Long getParamaterLong( String a_Name ) 
    {
        return (Long)this.m_Parameters.get( a_Name ) ;
    }
    protected Date getParameterDate( String a_Name )
    {
        return (Date)this.m_Parameters.get( a_Name ) ;
    }
    protected String getParameterString( String a_Name ) 
    {
        return (String)this.m_Parameters.get( a_Name ) ;
    }
    
}
