package org.ccci.gto.cas.oauth;

import javax.persistence.EntityManager;

import org.ccci.gto.cas.oauth.model.Grant;

public interface GrantManager {
    public Grant getGrantByAccessToken(final EntityManager em, final String accessToken);

    public Grant getGrantByAccessToken(final String accessToken);
}
