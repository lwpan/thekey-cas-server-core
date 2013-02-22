package org.ccci.gto.cas.oauth.authentication;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.ccci.gto.cas.authentication.principal.AbstractTheKeyCredentials;
import org.ccci.gto.cas.oauth.model.Grant;

public class OAuth2Credentials extends AbstractTheKeyCredentials {
    private static final long serialVersionUID = -8428728835683232972L;

    private String accessToken = null;
    private Grant grant = null;
    private final Set<String> requiredScope = new HashSet<String>();

    public String getAccessToken() {
        return this.accessToken;
    }

    public Grant getGrant() {
        return this.grant;
    }

    public Set<String> getRequiredScope() {
        return Collections.unmodifiableSet(this.requiredScope);
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public void setGrant(final Grant grant) {
        this.grant = grant;
    }

    public void addRequiredScope(final String scope) {
        requiredScope.add(scope);
    }
}
