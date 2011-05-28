package org.ccci.gto.persist;

import java.util.Collection;

import org.ccci.gcx.idm.common.model.ModelObject;
import org.springframework.util.Assert;

public abstract class AbstractCrudDao extends AbstractQueryDao implements
	CrudDao {
    public abstract void save(final ModelObject object);

    public abstract void saveOrUpdate(final ModelObject object);

    public abstract void update(final ModelObject object);

    public abstract void delete(final ModelObject object);

    public void saveAll(final Collection<? extends ModelObject> objects) {
	Assert.notEmpty(objects);
	for (ModelObject entry : objects) {
	    this.save(entry);
	}
    }
}
