package me.thekey.cas.selfservice.web.flow;

import java.io.Serializable;

public class SelfServiceModel implements Serializable {
    private static final long serialVersionUID = 3681503503116682018L;

    private String email;

    private String password;
    private String retypePassword;

    private String key;

    public final String getEmail() {
        return this.email;
    }

    public final String getPassword() {
        return this.password;
    }

    public final String getRetypePassword() {
        return this.retypePassword;
    }

    public final void setEmail(final String email) {
        this.email = email;
    }

    public final void setPassword(final String password) {
        this.password = password;
    }

    public final void setRetypePassword(final String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public final String getKey() {
        return this.key;
    }

    public final void setKey(final String key) {
        this.key = key;
    }
}
