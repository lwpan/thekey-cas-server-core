package org.ccci.gto.persist;

import java.io.Serializable;

import org.ccci.gcx.idm.common.model.ModelObject;

/*
 * @author Daniel Frett
 */
public abstract class AbstractQueryDao extends AbstractDao implements QueryDao {
    public abstract ModelObject get(final Serializable key);

    public abstract ModelObject load(final Serializable key);

    public abstract ModelObject initialize(final ModelObject object);

    public abstract boolean isInitialized(final ModelObject object);
}
