package org.ccci.gto.cas.authentication.principal;

import static me.thekey.cas.Constants.CREDS_ATTR_KEYUSER;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TheKeyUsernamePasswordCredentials extends UsernamePasswordCredentials implements Cloneable,
        TheKeyCredentials {
    private static final long serialVersionUID = 7041361585463100464L;

    private final Map<String, Serializable> attributes = new HashMap<>();

    private final BitSet locks = new BitSet();

    public TheKeyUsernamePasswordCredentials() {
        // set the default administrative locks to observe
        for (final Lock lock : Lock.values()) {
            setObserveLock(lock, true);
        }
    }

    protected TheKeyUsernamePasswordCredentials(final TheKeyUsernamePasswordCredentials credentials) {
        this.setAttributes(credentials.getAttributes());
        this.locks.clear();
        this.locks.or(credentials.locks);

        // clone inherited attributes (that aren't cloned in super constructors)
        this.setUsername(credentials.getUsername());
        this.setPassword(credentials.getPassword());
    }

    public void setObserveLock(final Lock lock, final boolean value) {
	synchronized (locks) {
	    locks.set(lock.index, !value);
	}
    }

    @Override
    public boolean observeLock(final Lock lock) {
	synchronized (locks) {
	    return !locks.get(lock.index);
	}
    }

    public void setUser(final GcxUser user) {
        this.setAttribute(CREDS_ATTR_KEYUSER, user);
    }

    @Override
    public GcxUser getUser() {
        return this.getAttribute(CREDS_ATTR_KEYUSER, GcxUser.class);
    }

    @Override
    public final Serializable setAttribute(final String key, final Serializable value) {
        return this.attributes.put(key, value);
    }

    @Override
    public final Serializable getAttribute(final String key) {
        return this.attributes.get(key);
    }

    @Override
    public final <T extends Serializable> T getAttribute(final String key, final Class<T> clazz) {
        return clazz.cast(this.getAttribute(key));
    }

    @Override
    public void setAttributes(final Map<String, ? extends Serializable> attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
    }

    @Override
    public Map<String, ? extends Serializable> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    @Override
    public TheKeyUsernamePasswordCredentials clone() {
        return new TheKeyUsernamePasswordCredentials(this);
    }
}
