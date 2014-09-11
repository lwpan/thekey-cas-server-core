package me.thekey.cas.federation.api;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import me.thekey.cas.federation.model.Identity;

import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

public class IdentityLinkingServiceApi implements AutoCloseable {
    @NotNull
    private String baseUri;

    @NotNull
    private String accessToken;

    private final Client client = ClientBuilder.newClient();

    public void setAccessToken(final String token) {
        this.accessToken = token;
    }

    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }

    public List<Identity> getLinkedIdentities(final Identity identity) throws CommunicationException {
        try {
            final List<JaxbIdentity> identities = newRequest(identity).get(new GenericType<List<JaxbIdentity>>() {});
            return Lists.transform(identities, new Function<JaxbIdentity, Identity>() {
                @Override
                public Identity apply(final JaxbIdentity input) {
                    return input.identity;
                }
            });
        } catch (final Exception e) {
            throw new CommunicationException(e);
        }
    }

    public void linkIdentities(final Identity id1, final Identity id2, final Double strength,
                               final String source) throws CommunicationException {
        try {
            newRequest(id1).post(Entity.xml(new JaxbIdentity(id2, strength, source)));
        } catch (final Exception e) {
            throw new CommunicationException(e);
        }
    }

    public void unlinkIdentities(final Identity id1, final Identity id2) throws CommunicationException {
        try {
            newRequest(id1, id2).delete();
        } catch (final Exception e) {
            throw new CommunicationException(e);
        }
    }

    private WebTarget newTarget(final Identity id1) {
        return this.client.target(this.baseUri).path(id1.getType().name() + "/" + id1.getId());
    }

    private WebTarget newTarget(final Identity id1, final Identity id2) {
        return newTarget(id1).path(id2.getType().name() + "/" + id2.getId());
    }

    private Invocation.Builder newRequest(final WebTarget target) {
        return target.request().header("Authorization", "Bearer " + this.accessToken);
    }

    private Invocation.Builder newRequest(final Identity id1) {
        return newRequest(newTarget(id1));
    }

    private Invocation.Builder newRequest(final Identity id1, final Identity id2) {
        return newRequest(newTarget(id1, id2));
    }

    public void close() {
        this.client.close();
    }

    @XmlRootElement(name = "identity")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class JaxbIdentity {
        @XmlTransient
        Identity identity;

        @XmlAttribute
        Double strength;

        @XmlAttribute
        String source;

        public JaxbIdentity() {
            this.identity = new Identity();
        }

        public JaxbIdentity(final Identity identity, final Double strength, final String source) {
            this.identity = identity;
            this.strength = strength;
            this.source = source;
        }

        @XmlAttribute(name = "type")
        public String getType() {
            return this.identity.getType().name();
        }

        public void setType(final String type) {
            this.identity = new Identity(type, this.identity.getId());
        }

        @XmlAttribute(name = "id")
        public String getId() {
            return this.identity.getId();
        }

        public void setId(final String id) {
            this.identity = new Identity(this.identity.getType(), id);
        }
    }
}
