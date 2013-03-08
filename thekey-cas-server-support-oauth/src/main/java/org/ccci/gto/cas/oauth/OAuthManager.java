package org.ccci.gto.cas.oauth;

import java.util.Set;

import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.Grant;

public interface OAuthManager {
    public Code createAuthorizationCode(Client client, String guid, Set<String> scope);

    public Grant getGrantByAccessToken(String accessToken);

    public Client getClient(Long id);

    public Code getAuthorizationCode(String code);
}
