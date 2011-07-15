package org.ccci.gcx.idm.common.persist.hibernate ;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.ccci.gto.persist.Dao;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;


/**
 * <b>AbstractDao</b> contains common functionality required by all {@link Dao}
 * concrete implementations.
 *
 * @author Greg Crider  Oct 12, 2006  2:36:36 PM
 */
public abstract class AbstractDao<T> extends HibernateDaoSupport implements
	Dao<T> {
    /**
     * Get the domain model class.
     * 
     * @return Domain model class.
     */
    protected abstract Class<? extends T> getModelClass();

    /**
     * Test the specified object to see if it is of the right class for the
     * current Dao class.
     * 
     * @param object
     *            {@link ModelObject} to be tested.
     */
    protected void assertModelObject(final T object) {
	Assert.notNull(object, "No ModelObject was provided");
	Assert.isAssignable(this.getModelClass(), object.getClass());
    }
}
