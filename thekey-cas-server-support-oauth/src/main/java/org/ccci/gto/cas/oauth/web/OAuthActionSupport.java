package org.ccci.gto.cas.oauth.web;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.oauth.OAuthManager;
import org.ccci.gto.cas.oauth.model.Client;
import org.ccci.gto.cas.oauth.model.Code;
import org.ccci.gto.persistence.DeadLockRetry;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used by OAuthAction to implement several calls that require
 * spring aop proxies
 * 
 * @author Daniel Frett
 */
public class OAuthActionSupport implements OAuthAction.Support {
    @NotNull
    private OAuthManager oauthManager;

    public void setOAuthManager(final OAuthManager oauthManager) {
        this.oauthManager = oauthManager;
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Client getClient(final String id) {
        return this.oauthManager.getClient(id);
    }

    @Override
    @DeadLockRetry
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createCode(final Code code) {
        // create the new authorization code
        this.oauthManager.createCode(code);
    }
}
