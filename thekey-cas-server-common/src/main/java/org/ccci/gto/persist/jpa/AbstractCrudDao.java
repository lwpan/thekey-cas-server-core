package org.ccci.gto.persist.jpa;

import org.ccci.gto.persist.CrudDao;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import java.util.Collection;

public abstract class AbstractCrudDao<T> extends JpaDaoSupport implements CrudDao<T> {
    @Override
    public void save(final T object) {
	getJpaTemplate().persist(object);
    }

    @Override
    public void update(final T object) {
	getJpaTemplate().merge(object);
    }

    @Override
    public void delete(final T object) {
	final JpaTemplate jpa = getJpaTemplate();
	jpa.remove(jpa.contains(object) ? object : jpa.merge(object));
    }

    @Override
    public void saveAll(final Collection<? extends T> objects) {
	if (objects != null) {
	    for (final T entry : objects) {
		this.save(entry);
	    }
	}
    }
}
