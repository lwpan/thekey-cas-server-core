package org.ccci.gto.cas.oauth.authentication;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.ccci.gto.cas.authentication.principal.AbstractTheKeyCredentials;
import org.ccci.gto.cas.oauth.model.AccessToken;

public class OAuth2Credentials extends AbstractTheKeyCredentials {
    private static final long serialVersionUID = -8428728835683232972L;

    private String rawToken = null;
    private AccessToken accessToken = null;
    private final Set<String> requiredScope = new HashSet<String>();

    public String getRawToken() {
        return this.rawToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public Set<String> getRequiredScope() {
        return Collections.unmodifiableSet(this.requiredScope);
    }

    public void setRawToken(final String rawToken) {
        this.rawToken = rawToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void addRequiredScope(final String scope) {
        requiredScope.add(scope);
    }
}
