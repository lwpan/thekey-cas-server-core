package org.ccci.gto.cas.federation;

import me.thekey.cas.service.UserManager;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Credentials;

import javax.validation.constraints.NotNull;

public abstract class AbstractFederationProcessor<C extends Credentials> implements FederationProcessor {
    @NotNull
    private final Class<C> clazz;

    @NotNull
    private UserManager userService;

    protected AbstractFederationProcessor(final Class<C> clazz) {
        this.clazz = clazz;
    }

    protected UserManager getUserService() {
        return this.userService;
    }

    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }

    @Override
    public final boolean supports(final Credentials credentials) {
        return this.clazz.isInstance(credentials) && this.supportsInternal(this.clazz.cast(credentials));
    }

    protected boolean supportsInternal(final C credentials) {
        return true;
    }

    @Override
    public final boolean createIdentity(final Credentials credentials, final Number strength) throws
            FederationException {
        if (this.supports(credentials)) {
            return this.createIdentityInternal(this.clazz.cast(credentials), strength);
        }

        return false;
    }

    protected abstract boolean createIdentityInternal(C credentials, Number strength) throws FederationException;

    @Override
    public boolean linkIdentity(final GcxUser user, final Credentials credentials,
                                final Number strength) throws FederationException {
        if (this.supports(credentials)) {
            return this.linkIdentityInternal(user, this.clazz.cast(credentials), strength);
        }
        return false;
    }

    protected abstract boolean linkIdentityInternal(GcxUser user, C credentials,
                                                    Number strength) throws FederationException;
}
