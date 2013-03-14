package org.ccci.gto.cas.oauth.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
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

import org.ccci.gto.cas.oauth.util.OAuth2Util;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "OAuthCodes")
public class Code {
    @Id
    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId", referencedColumnName = "id", nullable = false, updatable = false)
    private Client client;

    @Column(length = 36, nullable = false)
    private String guid;

    @Column(name = "scope", nullable = false)
    private String rawScope;
    @Transient
    private final Set<String> scope = new HashSet<String>();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

    private String redirectUri;

    public Code() {
    }

    public Code(final Client client) {
        this.client = client;
    }

    public boolean isExpired() {
        return this.expirationTime == null || this.expirationTime.before(new Date());
    }

    public Client getClient() {
        return this.client;
    }

    public String getCode() {
        return this.code;
    }

    public String getGuid() {
        return this.guid;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public Set<String> getScope() {
        return this.scope;
    }

    public String getScopeString() {
        return StringUtils.collectionToDelimitedString(this.scope, " ");
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setRedirectUri(final String redirectUri) {
        this.redirectUri = redirectUri;
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
