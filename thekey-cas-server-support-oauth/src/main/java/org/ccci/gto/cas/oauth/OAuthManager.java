package org.ccci.gto.cas.oauth;

import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.RefreshToken;

public interface OAuthManager {
    public void createAccessToken(AccessToken token);

    public void createClient(Client client);

    public void createCode(Code code);

    public void createRefreshToken(RefreshToken token);

    public AccessToken getAccessToken(String token);

    public Client getClient(Long id);

    public Client getClient(String id);

    public Code getCode(String code);

    public void removeCode(Code code);
}
