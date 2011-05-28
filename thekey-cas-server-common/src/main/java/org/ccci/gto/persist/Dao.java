package org.ccci.gto.persist;

import org.ccci.gcx.idm.common.model.ModelObject;

/**
 * <b>Dao</b> is the marker interface for all data access objects (DAO).
 * 
 * @author Daniel Frett
 */
public interface Dao {
    /**
     * Get the concrete ModelObject class for the implemented Dao object
     * 
     * @return concrete ModelObject class
     */
    public Class<? extends ModelObject> getModelClass();

    /**
     * Test the specified {@link ModelObject} to see if it is of the right class
     * for the current Dao class.
     * 
     * @param object
     *            {@link ModelObject} to be tested.
     */
    public void assertModelObject(final ModelObject object);
}
