package org.ccci.gto.cas.federation;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.service.GcxUserService;

public abstract class AbstractFederationProcessor implements FederationProcessor {
    @NotNull
    private GcxUserService userService;

    protected GcxUserService getUserService() {
        return userService;
    }

    public void setUserService(final GcxUserService userService) {
        this.userService = userService;
    }
}
