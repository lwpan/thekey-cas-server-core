package me.thekey.cas.persist.jpa;

import org.ccci.gto.persist.Dao;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

public abstract class AbstractDao<T> implements Dao<T> {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = false)
    public void save(final T object) {
        em.persist(object);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(final T object) {
        em.merge(object);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveAll(final Collection<? extends T> objects) {
        if (objects != null) {
            for (final T entry : objects) {
                this.save(entry);
            }
        }
    }
}
