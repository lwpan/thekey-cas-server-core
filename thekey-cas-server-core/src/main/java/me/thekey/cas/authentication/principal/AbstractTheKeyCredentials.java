package me.thekey.cas.authentication.principal;

import static me.thekey.cas.Constants.CREDS_ATTR_KEYUSER;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTheKeyCredentials implements Credentials, TheKeyCredentials {
    private static final long serialVersionUID = 6994838710453288088L;

    private final BitSet locks = new BitSet();

    private final Map<String, Serializable> attributes = new HashMap<>();

    protected AbstractTheKeyCredentials() {
        this(true);
    }

    protected AbstractTheKeyCredentials(final boolean observeLocks) {
        // set the default administrative locks to observe
        for (final Lock lock : Lock.values()) {
            setObserveLock(lock, observeLocks);
        }
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
    public final Map<String, ? extends Serializable> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }
}
