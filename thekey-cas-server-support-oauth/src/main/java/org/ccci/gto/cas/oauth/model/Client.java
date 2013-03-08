package org.ccci.gto.cas.oauth.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "OAuthClients")
public class Client implements Serializable {
    private static final long serialVersionUID = 7468644728125124915L;

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    private String description;

    private String redirectUri;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client", cascade = { CascadeType.REMOVE })
    private Set<Code> codes;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public void setRedirectUri(final String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public boolean isValidRedirectUri(final URI uri) {
        // TODO: implement this logic
        return true;
    }
}
