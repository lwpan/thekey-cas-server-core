package org.ccci.gto.persist.jpa;

import java.io.Serializable;

import org.ccci.gto.persist.QueryDao;

public abstract class AbstractQueryDao<T> extends AbstractDao<T> implements
	QueryDao<T> {
    public abstract T get(final Serializable key);

    public abstract T load(final Serializable key);
}
