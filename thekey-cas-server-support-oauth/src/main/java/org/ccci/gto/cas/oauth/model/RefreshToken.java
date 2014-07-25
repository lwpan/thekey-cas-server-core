package org.ccci.gto.cas.oauth.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("refresh_token")
public class RefreshToken extends Token {
    private static final long serialVersionUID = 133521728798249278L;

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
