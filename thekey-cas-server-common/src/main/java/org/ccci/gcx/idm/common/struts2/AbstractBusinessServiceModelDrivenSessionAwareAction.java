package org.ccci.gcx.idm.common.struts2;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.ccci.gcx.idm.common.struts2.support.ActionUtils;

/**
 * <b>AbstractBusinessServiceModelDrivenSessionAwareAction</b> contains common functionality for
 * Struts2 action classes that want to use the session aware interceptor. This interceptor
 * will automatically inject session attributes into the action at runtime. Any changes made
 * inside the action will be reflected back out to the underlying {@link HttpSessionRequest}
 * object.
 *
 * @author Greg Crider  Feb 27, 2008  11:38:03 AM
 */
public abstract class AbstractBusinessServiceModelDrivenSessionAwareAction extends
        AbstractBusinessServiceModelDrivenAction implements SessionAware
{
    private static final long serialVersionUID = -6685116458953674719L ;


    protected static final Log log = LogFactory.getLog( AbstractBusinessServiceModelDrivenSessionAwareAction.class ) ;

    
    @SuppressWarnings("unchecked")
    private Map m_Session = null ;

    
    /**
     * @return the session
     */
    @SuppressWarnings("unchecked")
    public Map getSession()
    {
        return this.m_Session ;
    }
    /**
     * @param a_session the session to set
     */
    @SuppressWarnings("unchecked")
    public void setSession( Map a_session )
    {
        this.m_Session = a_session ;
    }
    
    
    /**
     * Invalidate the underlying {@link HttpRequestSession} object.
     * 
     * @exception ActionException is the unchecked {@link IdmException} if an error occurs
     *            while attempting to invalidate the session.
     */
    public void invalidate()
    {
        ActionUtils.invalidate( this.m_Session ) ;
    }

}