package org.ccci.gcx.idm.common.struts2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.common.model.ModelObject;

import com.opensymphony.xwork2.ModelDriven;

/**
 * <b>AbstractBusinessServiceModelDrivenAction</b> contains common functionality for Struts2
 * action classes using the {@link ModelDriven} interface. This interface allows a domain
 * model (of type {@link ModelObject} to be specified for use with the {@link Action} and 
 * corresponding paramters and properties.
 *
 * @author Greg Crider  Feb 4, 2008  2:23:12 PM
 */
public abstract class AbstractBusinessServiceModelDrivenAction<T extends ModelObject>
	extends AbstractBusinessServicePreparableAction implements
	ModelDriven<T> {
    private static final long serialVersionUID = -909630855578788723L ;

    protected static final Log log = LogFactory.getLog( AbstractBusinessServiceModelDrivenAction.class ) ;
    
    /** Default domain model entity */
    private T m_ModelObject = null;

    /**
     * Used by interceptor for access the domain model. Don't override this
     * method unless you know what you are doing.
     * 
     * @return {@link ModelObject} returned by concrete implementation. 
     * 
     * @see com.opensymphony.xwork2.ModelDriven#getModel()
     * @see AbstractBusinessServiceModelDrivenAction#getModelObject()
     */
    public T getModel() {
	return this.m_ModelObject;
    }

    /**
     * Set the {@link ModelObject} instance to be used as the domain model for the
     * {@link ModelDriven} interface.
     * 
     * @param a_ModelObject {@link ModelObject} to be used with the {@link Action}.
     */
    public void setModelObject(T a_ModelObject) {
	log.trace("***** Setting ModelObject");

	this.m_ModelObject = a_ModelObject;
    }
}
