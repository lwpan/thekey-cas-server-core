package org.ccci.gto.persist.jpa;

import org.ccci.gto.persist.Dao;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import java.util.Collection;

public abstract class AbstractDao<T> extends JpaDaoSupport implements Dao<T> {
    @Override
    public void save(final T object) {
        getJpaTemplate().persist(object);
    }

    @Override
    public void update(final T object) {
        getJpaTemplate().merge(object);
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
