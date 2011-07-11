package org.ccci.gto.cas.admin.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.ccci.gcx.idm.common.IdmException;
import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.common.struts2.ActionException;
import org.ccci.gcx.idm.common.struts2.support.ActionUtils;

/**
 * <b>AbstractBusinessServiceModelDrivenSessionAwareAction</b> contains common
 * functionality for Struts2 action classes that want to use the session aware
 * interceptor. This interceptor will automatically inject session attributes
 * into the action at runtime. Any changes made inside the action will be
 * reflected back out to the underlying {@link HttpSessionRequest} object.
 * 
 * @author Greg Crider Feb 27, 2008 11:38:03 AM
 */
public abstract class AbstractPreparableModelDrivenSessionAwareAction<T extends ModelObject>
	extends AbstractPreparableModelDrivenAction<T> implements SessionAware {
    private static final long serialVersionUID = -6685116458953674719L;

    private Map<String, Object> session = new HashMap<String, Object>();

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
	this.session = session;
    }

    /**
     * Invalidate the underlying {@link HttpRequestSession} object.
     * 
     * @exception ActionException
     *                is the unchecked {@link IdmException} if an error occurs
     *                while attempting to invalidate the session.
     */
    public void invalidate() {
	ActionUtils.invalidate(this.session);
    }
}
