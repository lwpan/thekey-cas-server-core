package org.ccci.gto.cas.oauth.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("refresh_token")
public class RefreshToken extends Token {
    public RefreshToken() {
        super();
    }

    public RefreshToken(final Client client) {
        super(client);
    }

    public RefreshToken(final Code code) {
        super(code);
    }

    public RefreshToken(final RefreshToken token) {
        super(token);
    }
}
