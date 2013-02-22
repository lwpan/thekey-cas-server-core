package org.ccci.gto.cas.oauth.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Grants")
@NamedQuery(name = "Grant.findByAccessToken", query = "SELECT g FROM Grant g WHERE g.accessToken = :accessToken")
public class Grant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 36)
    private String guid;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

    @Column(name = "scope", nullable = false)
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @JoinTable(name = "Grants_scope", joinColumns = @JoinColumn(name = "Grant_id"))
    private Set<String> scope = new HashSet<String>();

    public boolean isScopeAuthorized(final String scope) {
        return this.scope != null && this.scope.contains(scope);
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public Date getExpirationTime() {
        return this.expirationTime;
    }

    public String getGuid() {
        return this.guid;
    }

    public Set<String> getScope() {
        return this.scope;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public void setScope(final Set<String> scope) {
        this.scope = scope;
    }
}
