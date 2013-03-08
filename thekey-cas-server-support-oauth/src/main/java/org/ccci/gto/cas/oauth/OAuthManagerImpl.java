package org.ccci.gto.cas.oauth;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.cas.oauth.model.Grant;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public class OAuthManagerImpl implements OAuthManager {
    private static final String TICKET_PREFIX_CODE = "CODE";
    private static final String TICKET_PREFIX_ACCESS_TOKEN = "AT";
    private static final String TICKET_PREFIX_REFRESH_TOKEN = "RT";

    @PersistenceContext
    private EntityManager em;

    @NotNull
    private UniqueTicketIdGenerator ticketIdGenerator;

    public void setTicketIdGenerator(final UniqueTicketIdGenerator ticketIdGenerator) {
        this.ticketIdGenerator = ticketIdGenerator;
    }

    @Override
    public Code createAuthorizationCode(final Client client, final String guid, final Set<String> scope) {
        final Code code = new Code(client, this.ticketIdGenerator.getNewTicketId(TICKET_PREFIX_CODE));
        code.setGuid(guid);
        this.em.persist(code);
        return code;
    }

    @Override
    public Client getClient(final Long id) {
        return this.em.find(Client.class, id);
    }

    @Override
    public Code getAuthorizationCode(final String code) {
        return this.em.find(Code.class, code);
    }

    @Override
    public Grant getGrantByAccessToken(final String accessToken) {
        try {
            return this.em.createNamedQuery("Grant.findByAccessToken", Grant.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        } catch (final NoResultException e) {
            // Suppress no result exceptions by return null
            return null;
        }
    }
}
