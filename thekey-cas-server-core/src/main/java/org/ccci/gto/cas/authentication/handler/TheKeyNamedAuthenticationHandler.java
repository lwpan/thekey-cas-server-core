package org.ccci.gto.cas.authentication.handler;

import javax.validation.constraints.NotNull;

import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.handler.NamedAuthenticationHandler;

public final class TheKeyNamedAuthenticationHandler extends TheKeyAuthenticationHandler implements
        NamedAuthenticationHandler {
    @NotNull
    private NamedAuthenticationHandler handler;

    @Override
    public void setHandler(final AuthenticationHandler handler) {
        if (handler instanceof NamedAuthenticationHandler) {
            this.handler = (NamedAuthenticationHandler) handler;
            super.setHandler(handler);
        } else {
            throw new RuntimeException(handler.toString() + " is not a NamedAuthenticationHandler");
        }
    }

    @Override
    public String getName() {
        return this.handler.getName();
    }
}
