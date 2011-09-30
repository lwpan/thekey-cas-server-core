package org.ccci.gto.persist.jpa;

import org.ccci.gto.persist.Dao;
import org.springframework.orm.jpa.support.JpaDaoSupport;

public abstract class AbstractDao<T> extends JpaDaoSupport implements Dao<T> {
}
