package org.ccci.gcx.idm.common.struts2.support;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.SessionMap;
import org.ccci.gcx.idm.common.IdmException;
import org.ccci.gcx.idm.common.struts2.ActionException;

/**
 * <b>ActionUtils</b> contains useful methods and utilities relating to Struts2
 * action class implementations.
 *
 * @author Greg Crider  Feb 28, 2008  3:27:03 PM
 */
public class ActionUtils
{
    protected static final Log log = LogFactory.getLog( ActionUtils.class ) ;
    
    
    /**
     * Invalidate the underlying {@link HttpRequestSession} object.
     * 
     * @exception ActionException is the unchecked {@link IdmException} if an error occurs
     *            while attempting to invalidate the session.
     */
    public static void invalidate(Map<String, Object> a_Session) {
        if ( a_Session == null ) {
            /*= WARN =*/ log.warn( "Session object is not set; unable to complete request" ) ;
	} else if (a_Session instanceof SessionMap) {
            try {
		((SessionMap<String, Object>) a_Session).invalidate();
            } catch ( Exception e ) {
                /*= ERROR =*/ log.error( "Unable to invalidate session", e ) ;
                throw new ActionException( "Unable to invalidate session", e ) ;
            }
        } else {
            /*= ERROR =*/ log.error( "Internal session object is not SessionMap, but is \"" + a_Session.getClass().getName() + "\"." ) ;
            throw new ActionException( "Session Object is not a SessionMap" ) ;
        }
    }

}
