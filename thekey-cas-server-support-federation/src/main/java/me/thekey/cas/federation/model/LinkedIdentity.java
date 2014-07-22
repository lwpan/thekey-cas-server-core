package me.thekey.cas.federation.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Objects;

@Entity
@NamedQueries({@NamedQuery(name = "LinkedIdentity.findByGuid", query = "SELECT l FROM LinkedIdentity l WHERE l.key" +
        ".guid = :guid"), @NamedQuery(name = "LinkedIdentity.findByGuidAndType",
        query = "SELECT l FROM LinkedIdentity l WHERE l.key.guid = :guid AND l.key.identity.type = :type"),
        @NamedQuery(name = "LinkedIdentity.findByIdentity",
        query = "SELECT l FROM LinkedIdentity l WHERE l.key.identity.type = :type AND l.key.identity.id = :id")})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"guid", "type"}), @UniqueConstraint(columnNames =
        {"type", "id"})})
public class LinkedIdentity implements Serializable {
    private static final long serialVersionUID = -826591904818006925L;

    @EmbeddedId
    private PrimaryKey key;

    public LinkedIdentity() {
        this(new PrimaryKey());
    }

    public LinkedIdentity(final String guid, final Identity identity) {
        this(new PrimaryKey(guid, identity));
    }

    public LinkedIdentity(final PrimaryKey key) {
        this.key = key;
    }

    public PrimaryKey getKey() {
        return this.key;
    }

    public String getGuid() {
        return this.key.getGuid();
    }

    public Identity getIdentity() {
        return this.key.identity;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof LinkedIdentity)) { return false; }

        final LinkedIdentity that = (LinkedIdentity) o;

        return Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key);
    }

    @Embeddable
    public static class PrimaryKey implements Serializable {
        private static final long serialVersionUID = -6846130733588543767L;

        @Column(length = 36, updatable = false, nullable = false)
        private String guid;

        @Embedded
        private Identity identity;

        public PrimaryKey() {
            this(null, new Identity());
        }

        public PrimaryKey(final String guid, final Identity identity) {
            this.guid = guid;
            this.identity = identity;
        }

        public String getGuid() {
            return this.guid;
        }

        public Identity getIdentity() {
            return this.identity;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (!(o instanceof PrimaryKey)) { return false; }

            final PrimaryKey that = (PrimaryKey) o;

            return Objects.equals(this.guid, that.guid) && Objects.equals(this.identity, that.identity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.guid, this.identity);
        }
    }
}
