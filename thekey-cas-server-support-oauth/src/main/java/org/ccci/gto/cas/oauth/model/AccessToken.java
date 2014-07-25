package org.ccci.gto.cas.oauth.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("access_token")
public class AccessToken extends Token {
    private static final long serialVersionUID = -3918284252587141172L;

    public AccessToken() {
        super();
    }

    public AccessToken(final Client client) {
        super(client);
    }

    public AccessToken(final Code code) {
        super(code);
    }

    public AccessToken(final RefreshToken token) {
        super(token);
    }
}
