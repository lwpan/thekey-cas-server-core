package org.ccci.gto.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/*
 * @author Daniel Frett
 */
public abstract class AbstractDao<T> implements Dao<T> {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Test the specified object to see if it is valid for the current Dao
     * 
     * @param object
     *            object to be tested.
     */
    protected void assertValidObject(final T object) {
	Assert.notNull(object, "No object was provided");
    }
}
