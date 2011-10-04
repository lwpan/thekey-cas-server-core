package org.ccci.gto.cas.persist.jpa;

import static org.ccci.gcx.idm.core.Constants.SEARCH_NO_LIMIT;

import java.io.Serializable;
import java.util.List;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.persist.GcxUserDao;
import org.ccci.gto.persist.jpa.AbstractCrudDao;
import org.springframework.orm.jpa.JpaTemplate;

public class JpaKeyUserDao extends AbstractCrudDao<GcxUser> implements
	GcxUserDao {
    public int getMaxSearchResults() {
	return SEARCH_NO_LIMIT;
    }

    public void delete(final GcxUser user) {
	final JpaTemplate jpa = getJpaTemplate();
	jpa.remove(jpa.contains(user) ? user : jpa.merge(user));
    }

    public GcxUser findByEmail(final String email) {
	for (final Object user : getJpaTemplate().find(
		"select u from GcxUser u where u.email = ?1", email)) {
	    if (user instanceof GcxUser) {
		return (GcxUser) user;
	    }
	}
	return null;
    }

    public GcxUser findByGUID(final String guid) {
	for (final Object user : getJpaTemplate().find(
		"select u from GcxUser u where u.guid = ?1", guid)) {
	    if (user instanceof GcxUser) {
		return (GcxUser) user;
	    }
	}
	return null;
    }

    public GcxUser get(Serializable key) {
	throw new UnsupportedOperationException();
    }

    public GcxUser load(final Serializable key) {
	throw new UnsupportedOperationException();
    }

    public void save(final GcxUser object) {
	throw new UnsupportedOperationException();
    }

    public void saveOrUpdate(final GcxUser object) {
	throw new UnsupportedOperationException();
    }

    public void update(final GcxUser object) {
	throw new UnsupportedOperationException();
    }

    public GcxUser findByFacebookId(final String facebookId) {
	throw new UnsupportedOperationException(
		"searching for a user based on their facebook id is unsupported for legacy transitional users");
    }

    public List<GcxUser> findAllByFirstName(final String pattern) {
	throw new UnsupportedOperationException();
    }

    public List<GcxUser> findAllByLastName(final String pattern) {
	throw new UnsupportedOperationException();
    }

    public List<GcxUser> findAllByEmail(final String pattern) {
	throw new UnsupportedOperationException();
    }

    public List<GcxUser> findAllByUserid(final String pattern,
	    final boolean includeDeactivated) {
	throw new UnsupportedOperationException();
    }

    public void update(final GcxUser original, final GcxUser object) {
	throw new UnsupportedOperationException();
    }
}
