package org.ccci.gto.persist.jpa;

import java.util.Collection;

import org.ccci.gto.persist.CrudDao;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.Assert;

public abstract class AbstractCrudDao<T> extends AbstractQueryDao<T> implements
	CrudDao<T> {
    public void save(final T object) {
	getJpaTemplate().persist(object);
    }

    public void saveOrUpdate(final T object) {
	getJpaTemplate().merge(object);
    }

    public void update(final T object) {
	getJpaTemplate().merge(object);
    }

    public void delete(final T object) {
	final JpaTemplate jpa = getJpaTemplate();
	jpa.remove(jpa.contains(object) ? object : jpa.merge(object));
    }

    public void saveAll(final Collection<? extends T> objects) {
	Assert.notEmpty(objects);
	for (final T entry : objects) {
	    this.save(entry);
	}
    }
}
