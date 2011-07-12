package org.ccci.gto.cas.admin.action;

import org.ccci.gcx.idm.common.model.ModelObject;

import com.opensymphony.xwork2.ModelDriven;

/**
 * <b>AbstractBusinessServiceModelDrivenAction</b> contains common functionality
 * for Struts2 action classes using the {@link ModelDriven} interface. This
 * interface allows a domain model (of type {@link ModelObject} to be specified
 * for use with the {@link Action} and corresponding parameters and properties.
 * 
 * @author Greg Crider Feb 4, 2008 2:23:12 PM
 */
public abstract class AbstractPreparableModelDrivenAction<T> extends
	AbstractPreparableAction implements ModelDriven<T> {
    private static final long serialVersionUID = -909630855578788723L;

    /** Default domain model entity */
    private T model;

    /**
     * Used by interceptor for access the domain model. Don't override this
     * method unless you know what you are doing.
     * 
     * @return {@link ModelObject} returned by concrete implementation.
     * 
     * @see com.opensymphony.xwork2.ModelDriven#getModel()
     * @see AbstractPreparableModelDrivenAction#getModelObject()
     */
    public T getModel() {
	return this.model;
    }

    /**
     * Set the {@link ModelObject} instance to be used as the domain model for
     * the {@link ModelDriven} interface.
     * 
     * @param model
     *            {@link ModelObject} to be used with the {@link Action}.
     */
    public void setModel(final T model) {
	log.trace("***** Setting Model Object");
	this.model = model;
    }
}
