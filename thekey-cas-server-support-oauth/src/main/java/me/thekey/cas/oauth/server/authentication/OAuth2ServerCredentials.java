package me.thekey.cas.oauth.server.authentication;

import me.thekey.cas.authentication.principal.AbstractTheKeyCredentials;
import org.ccci.gto.cas.oauth.model.AccessToken;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OAuth2ServerCredentials extends AbstractTheKeyCredentials {
    private static final long serialVersionUID = -7040708306258408954L;

    private String rawToken = null;
    private AccessToken accessToken = null;
    private final Set<String> requiredScope = new HashSet<String>();

    public OAuth2ServerCredentials() {
        this(true);
    }

    public OAuth2ServerCredentials(final boolean observeLocks) {
        super(observeLocks);
        this.setObserveLock(Lock.STALEPASSWORD, false);
        this.setObserveLock(Lock.VERIFIED, false);
    }

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
