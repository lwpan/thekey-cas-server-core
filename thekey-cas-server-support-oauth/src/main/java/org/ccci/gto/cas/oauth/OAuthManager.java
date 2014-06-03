package org.ccci.gto.cas.oauth;

import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.RefreshToken;
import org.ccci.gto.cas.oauth.model.Token;

public interface OAuthManager {
    public AccessToken createAccessToken(AccessToken token);

    public void createClient(Client client);

    public void createCode(Code code);

    public RefreshToken createRefreshToken(RefreshToken token);

    public Client getClient(Long id);

    public Client getClient(String id);

    public Code getCode(String code);

    public <T extends Token> T getToken(Class<T> tokenClass, String token);

    public void removeCode(Code code);
}
