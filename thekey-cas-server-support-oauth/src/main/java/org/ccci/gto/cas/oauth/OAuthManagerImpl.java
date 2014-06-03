package org.ccci.gto.cas.oauth;

import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.RefreshToken;
import org.ccci.gto.cas.oauth.model.Token;
import org.jasig.cas.util.RandomStringGenerator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.util.Date;

@Transactional(propagation = Propagation.MANDATORY)
public class OAuthManagerImpl implements OAuthManager {
    private static final long DEFAULT_LIFESPAN_CODE = 30;
    private static final long DEFAULT_LIFESPAN_ACCESS_TOKEN = 60 * 60;
    private static final long DEFAULT_LIFESPAN_REFRESH_TOKEN = 365 * 24 * 60 * 60;

    @PersistenceContext
    private EntityManager em;

    @NotNull
    private RandomStringGenerator randomStringGenerator;

    private SecureRandom clientIdGenerator = new SecureRandom();

    public void setRandomStringGenerator(final RandomStringGenerator randomStringGenerator) {
        this.randomStringGenerator = randomStringGenerator;
    }

    @Override
    public AccessToken createAccessToken(final AccessToken token) {
        return this.createToken(token, DEFAULT_LIFESPAN_ACCESS_TOKEN);
    }

    @Override
    public void createClient(final Client client) {
        // generate a new client id for this client that is at least 10 digits
        // long
        long id = -1;
        while (id < 1000000000) {
            id = clientIdGenerator.nextLong();
        }
        client.setId(id);

        this.em.persist(client);
    }

    @Override
    public void createCode(final Code code) {
        // generate a new code and set an expiration timer
        code.setCode(this.randomStringGenerator.getNewString());
        code.setExpirationTime(new Date(System.currentTimeMillis() + (DEFAULT_LIFESPAN_CODE * 1000)));

        // store the code
        this.em.persist(code);
    }

    @Override
    public RefreshToken createRefreshToken(final RefreshToken token) {
        return createToken(token, DEFAULT_LIFESPAN_REFRESH_TOKEN);
    }

    @Override
    public Code getCode(final String code) {
        try {
            return this.em.find(Code.class, code);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Client getClient(final Long id) {
        try {
            return this.em.find(Client.class, id);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Client getClient(final String id) {
        try {
            return this.getClient(Long.parseLong(id));
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    @Override
    public <T extends Token> T getToken(final Class<T> tokenClass, final String token) {
        try {
            return this.em.find(tokenClass, token);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void removeCode(final Code code) {
        this.em.remove(code);
    }

    private <T extends Token> T createToken(final T token, final long lifeSpanSecs) {
        // generate a new token
        token.setToken(this.randomStringGenerator.getNewString());
        token.setExpirationTime(new Date(System.currentTimeMillis() + (lifeSpanSecs * 1000)));

        // store the refresh_token
        this.em.persist(token);

        // return the token
        return token;
    }
}
