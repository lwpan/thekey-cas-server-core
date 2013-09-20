package me.thekey.cas.selfservice.web.flow;

import java.io.Serializable;

public final class SelfServiceModel implements Serializable {
    private static final long serialVersionUID = 3028158746057916753L;

    private String key;

    public final String getKey() {
        return this.key;
    }

    public final void setKey(final String key) {
        this.key = key;
    }
}
