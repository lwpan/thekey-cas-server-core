package org.ccci.gto.persist;

import org.springframework.util.Assert;

/*
 * @author Daniel Frett
 */
public abstract class AbstractDao<T> implements Dao<T> {
    /**
     * Test the specified object to see if it is valid for the current Dao
     *
     * @param object object to be tested.
     */
    protected void assertValidObject(final T object) {
        Assert.notNull(object, "No object was provided");
    }
}
