package org.ccci.gto.cas.oauth.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "OAuthCodes")
public class Code {
    private static final long MAX_AGE = 60 * 1000; // 60 seconds

    @Id
    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId", referencedColumnName = "id", nullable = false, updatable = false)
    private Client client;

    @Column(length = 36, nullable = false)
    private String guid;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

    public Code() {
    }

    public Code(final Client client, final String code) {
        this.client = client;
        this.code = code;
        this.expirationTime = new Date(System.currentTimeMillis() + MAX_AGE);
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

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public boolean isExpired() {
        return this.expirationTime == null || this.expirationTime.before(new Date());
    }
}
