package org.ccci.gto.cas.oauth.model;

import org.ccci.gto.cas.oauth.util.OAuth2Util;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "OAuthTokens")
@DiscriminatorColumn(name = "type")
public abstract class Token {
    @Id
    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId", referencedColumnName = "id", nullable = false, updatable = false)
    private Client client;

    @Column(nullable = false, updatable = false)
    private boolean confidential = false;

    @Column(nullable = false, length = 36)
    private String guid;

    @Column(name = "scope", nullable = false)
    private String rawScope;
    @Transient
    private final Set<String> scope = new HashSet<>();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

    public Token() {
    }

    public Token(final Client client) {
        this.client = client;
    }

    public Token(final Code code) {
        this(code.getClient());
        this.setGuid(code.getGuid());
        this.setScope(code.getScope());
    }

    public Token(final Token token) {
        this(token.getClient());
        this.setGuid(token.getGuid());
        this.setScope(token.getScope());
    }

    public boolean isExpired() {
        return this.expirationTime == null || this.expirationTime.before(new Date());
    }

    public boolean isScopeAuthorized(final String scope) {
        return this.scope.contains(scope);
    }

    public Client getClient() {
        return this.client;
    }

    public boolean isConfidential() {
        return confidential;
    }

    public void setConfidential(final boolean confidential) {
        this.confidential = confidential;
    }

    public String getToken() {
        return this.token;
    }

    public String getGuid() {
        return this.guid;
    }

    public Date getExpirationTime() {
        return this.expirationTime;
    }

    public Set<String> getScope() {
        return this.scope;
    }

    public String getScopeString() {
        return StringUtils.collectionToDelimitedString(this.scope, " ");
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public void setScope(final Set<String> scope) {
        this.scope.clear();
        if (scope != null) {
            this.scope.addAll(scope);
        }
    }

    @PrePersist
    @PreUpdate
    private void marshal() {
        // marshal the scope
        {
            this.rawScope = this.getScopeString();
        }
    }

    @PostLoad
    private void unmarshal() {
        // unmarshal the scope
        {
            this.scope.clear();
            this.scope.addAll(OAuth2Util.parseScope(this.rawScope));
        }
    }
}
