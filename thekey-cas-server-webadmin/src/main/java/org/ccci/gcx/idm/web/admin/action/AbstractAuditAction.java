package org.ccci.gcx.idm.web.admin.action;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gcx.idm.common.struts2.AbstractBusinessServiceModelDrivenSessionAwareAction;

/**
 * <b>AbstractAuditAction</b> contains common functionality used by all {@link Action}'s that
 * require auditing.
 *
 * @author Greg Crider  Nov 25, 2008  8:13:42 PM
 */
public abstract class AbstractAuditAction<T extends ModelObject> extends
	AbstractBusinessServiceModelDrivenSessionAwareAction<T> {
    private static final long serialVersionUID = 2741173112253232382L ;

    /** Application source passed into the auditing service to identify the application that made a change. */
    private String m_ApplicationSource = "" ;


    /**
     * @return the applicationSource
     */
    public String getApplicationSource()
    {
        return this.m_ApplicationSource ;
    }
    /**
     * @param a_applicationSource the applicationSource to set
     */
    public void setApplicationSource( String a_applicationSource )
    {
        this.m_ApplicationSource = a_applicationSource ;
    }
    
}
