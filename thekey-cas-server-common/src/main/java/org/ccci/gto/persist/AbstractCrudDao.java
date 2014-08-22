package org.ccci.gto.persist;

import java.util.Collection;

public abstract class AbstractCrudDao<T> extends AbstractQueryDao<T> implements
	CrudDao<T> {
    @Override
    public abstract void save(final T object);

    @Override
    public abstract void update(final T object);

    @Override
    public abstract void delete(final T object);

    @Override
    public void saveAll(final Collection<? extends T> objects) {
	if (objects != null) {
	    for (final T entry : objects) {
		this.save(entry);
	    }
	}
    }
}
