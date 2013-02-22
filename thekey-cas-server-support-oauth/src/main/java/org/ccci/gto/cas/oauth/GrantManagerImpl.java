package org.ccci.gto.cas.oauth;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.oauth.model.Grant;

public class GrantManagerImpl implements GrantManager {
    @PersistenceUnit
    @NotNull
    private EntityManagerFactory emf;

    @Override
    public Grant getGrantByAccessToken(final String accessToken) {
        return this.getGrantByAccessToken(this.getEntityManager(), accessToken);
    }

    @Override
    public Grant getGrantByAccessToken(final EntityManager em, final String accessToken) {
        return em.createNamedQuery("Grant.findByAccessToken", Grant.class).setParameter("accessToken", accessToken)
                .getSingleResult();
    }

    public void setEntityManagerFactory(final EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager getEntityManager() {
        return this.emf.createEntityManager();
    }
}
