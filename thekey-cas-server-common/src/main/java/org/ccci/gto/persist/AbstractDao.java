package org.ccci.gto.persist;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.springframework.util.Assert;

/*
 * @author Daniel Frett
 */
public abstract class AbstractDao implements Dao {
    protected abstract Class<? extends ModelObject> getModelClass();

    /**
     * Test the specified {@link ModelObject} to see if it is of the right class
     * for the current Dao class.
     * 
     * @param object
     *            {@link ModelObject} to be tested.
     */
    protected void assertModelObject(final ModelObject object) {
	Assert.notNull(object, "No ModelObject was provided");
	Assert.isAssignable(this.getModelClass(), object.getClass());
    }
}
