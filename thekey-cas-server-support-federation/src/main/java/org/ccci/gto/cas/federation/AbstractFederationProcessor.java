package org.ccci.gto.cas.federation;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

public abstract class AbstractFederationProcessor implements FederationProcessor {
    @NotNull
    private UserManager userService;

    protected UserManager getUserService() {
        return userService;
    }

    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }
}
