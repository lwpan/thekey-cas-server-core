package org.ccci.gto.cas.admin.action;

import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

/**
 * <b>AbstractBusinessServiceModelDrivenSessionAwareAction</b> contains common
 * functionality for Struts2 action classes that want to use the session aware
 * interceptor. This interceptor will automatically inject session attributes
 * into the action at runtime. Any changes made inside the action will be
 * reflected back out to the underlying {@link HttpSessionRequest} object.
 * 
 * @author Greg Crider Feb 27, 2008 11:38:03 AM
 */
public abstract class AbstractPreparableModelDrivenSessionAwareAction<T>
	extends AbstractPreparableModelDrivenAction<T> implements SessionAware {
    private static final long serialVersionUID = -6685116458953674719L;

    private SessionMap<String, Object> session;

    /**
     * @return the session
     */
    public Map<String, Object> getSession() {
	return this.session;
    }

    /**
     * @param session
     *            the session to set
     */
    public void setSession(final Map<String, Object> session) {
	if (session instanceof SessionMap) {
	    this.session = (SessionMap<String, Object>) session;
	} else {
	    this.session = null;
	    this.log.error("invalid session map being set for this action");
	}
    }

    /**
     * Invalidate the underlying {@link HttpRequestSession} object.
     */
    protected void invalidateSession() {
	log.debug("Invalidating Session");
	if (this.session != null) {
	    this.session.invalidate();
	}
    }
}
