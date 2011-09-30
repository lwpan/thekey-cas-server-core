package org.ccci.gto.persist;

import java.io.Serializable;

/*
 * @author Daniel Frett
 */
public abstract class AbstractQueryDao<T> extends AbstractDao<T> implements
	QueryDao<T> {
    public abstract T get(final Serializable key);

    public abstract T load(final Serializable key);
}
