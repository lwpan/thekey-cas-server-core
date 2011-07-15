package org.ccci.gto.persist;

import java.util.Collection;

import org.springframework.util.Assert;

public abstract class AbstractCrudDao<T> extends AbstractQueryDao<T> implements
	CrudDao<T> {
    public abstract void save(final T object);

    public abstract void saveOrUpdate(final T object);

    public abstract void update(final T object);

    public abstract void delete(final T object);

    public void saveAll(final Collection<? extends T> objects) {
	Assert.notEmpty(objects);
	for (final T entry : objects) {
	    this.save(entry);
	}
    }
}
