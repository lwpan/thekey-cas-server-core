package org.ccci.gto.cas.admin.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

/**
 * <b>AbstractBusinessServicePreparableAction</b> contains the common
 * functionality required by all concrete implementations of {@link Preparable}
 * for use with Struts2. It extends from {@link ActionSupport} to take advantage
 * of the convenience methods and implementations therein.
 * 
 * @author Greg Crider Jan 7, 2008 2:11:55 PM
 */
public abstract class AbstractPreparableAction extends ActionSupport implements
	Preparable {
    private static final long serialVersionUID = -8170778181711768639L;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Default prepare method does nothing. Override to change this behavior for
     * a specific, concrete implementation.
     * 
     * @throws Exception
     *             if a stupid error occurs.
     * 
     * @see com.opensymphony.xwork2.Preparable#prepare()
     */
    public void prepare() throws Exception {
	log.trace("***** Preparing action");
    }
}
