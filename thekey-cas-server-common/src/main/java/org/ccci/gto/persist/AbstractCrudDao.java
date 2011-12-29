package org.ccci.gto.persist;

import java.util.Collection;

public abstract class AbstractCrudDao<T> extends AbstractQueryDao<T> implements
	CrudDao<T> {
    public abstract void save(final T object);

    public abstract void saveOrUpdate(final T object);

    public abstract void update(final T object);

    public abstract void delete(final T object);

    public void saveAll(final Collection<? extends T> objects) {
	if (objects != null) {
	    for (final T entry : objects) {
		this.save(entry);
	    }
	}
    }
}
