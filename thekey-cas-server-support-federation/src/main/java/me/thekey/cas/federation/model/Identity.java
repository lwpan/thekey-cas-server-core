package me.thekey.cas.federation.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * object stolen from identity linking service
 */
@Embeddable
public class Identity implements Serializable {
    private static final long serialVersionUID = -8930003468166938704L;

    public enum Type {
        RELAY, THEKEY, UNKNOWN;

        public static Type fromString(final String raw) {
            if (raw != null) {
                switch (raw.toUpperCase(Locale.US)) {
                    case "RELAY":
                        return RELAY;
                    case "THEKEY":
                        return THEKEY;
                }
            }

            return UNKNOWN;
        }
    }

    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(updatable = false, nullable = false)
    private String id;

    public Identity() {
        this((Type) null, null);
    }

    public Identity(final String type, final String id) {
        this(Type.fromString(type), id);
    }

    public Identity(final Type type, final String id) {
        this.type = type != null ? type : Type.UNKNOWN;
        this.id = id != null ? id : "";
    }

    public static Identity relay(final String guid) {
        return new Identity(Type.RELAY, guid);
    }

    public static Identity thekey(final String guid) {
        return new Identity(Type.THEKEY, guid);
    }

    public Type getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public boolean isRelay() {
        return this.type == Type.RELAY;
    }

    public boolean isTheKey() {
        return this.type == Type.THEKEY;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Identity)) { return false; }

        Identity identity = (Identity) o;

        return Objects.equals(this.id, identity.id) && this.type == identity.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
