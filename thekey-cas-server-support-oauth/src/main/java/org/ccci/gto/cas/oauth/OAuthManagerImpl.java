package org.ccci.gto.cas.oauth;

import java.security.SecureRandom;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.RefreshToken;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public class OAuthManagerImpl implements OAuthManager {
    private static final String TICKET_PREFIX_CODE = "CODE";
    private static final String TICKET_PREFIX_ACCESS_TOKEN = "AT";
    private static final String TICKET_PREFIX_REFRESH_TOKEN = "RT";

    private static final long DEFAULT_LIFESPAN_CODE = 60 * 1000;
    private static final long DEFAULT_LIFESPAN_ACCESS_TOKEN = 30 * 24 * 60 * 60 * 1000;
    private static final long DEFAULT_LIFESPAN_REFRESH_TOKEN = 365 * 24 * 60 * 60 * 1000;

    @PersistenceContext
    private EntityManager em;

    @NotNull
    private UniqueTicketIdGenerator ticketIdGenerator;

    private SecureRandom clientIdGenerator = new SecureRandom();

    public void setTicketIdGenerator(final UniqueTicketIdGenerator ticketIdGenerator) {
        this.ticketIdGenerator = ticketIdGenerator;
    }

    @Override
    public void createAccessToken(final AccessToken token) {
        // generate a new token for this access_token
        token.setToken(this.ticketIdGenerator.getNewTicketId(TICKET_PREFIX_ACCESS_TOKEN));
        token.setExpirationTime(new Date(System.currentTimeMillis() + DEFAULT_LIFESPAN_ACCESS_TOKEN));

        // store the access_token
        this.em.persist(token);
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
        code.setCode(this.ticketIdGenerator.getNewTicketId(TICKET_PREFIX_CODE));
        code.setExpirationTime(new Date(System.currentTimeMillis() + DEFAULT_LIFESPAN_CODE));

        // store the code
        this.em.persist(code);
    }

    @Override
    public void createRefreshToken(final RefreshToken token) {
        // generate a new token for this refresh_token
        token.setToken(this.ticketIdGenerator.getNewTicketId(TICKET_PREFIX_REFRESH_TOKEN));
        token.setExpirationTime(new Date(System.currentTimeMillis() + DEFAULT_LIFESPAN_REFRESH_TOKEN));

        // store the refresh_token
        this.em.persist(token);
    }

    @Override
    public AccessToken getAccessToken(final String token) {
        try {
            return this.em.find(AccessToken.class, token);
        } catch (final IllegalArgumentException e) {
            return null;
        }
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
    public void removeCode(final Code code) {
        this.em.remove(code);
    }
}
