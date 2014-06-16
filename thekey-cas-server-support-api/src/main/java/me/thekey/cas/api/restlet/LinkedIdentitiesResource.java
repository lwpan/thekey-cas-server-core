package me.thekey.cas.api.restlet;

import me.thekey.cas.api.ApiController.Identity;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class LinkedIdentitiesResource extends AbstractUserQueryResource {
    private static final Logger LOG = LoggerFactory.getLogger(LinkedIdentitiesResource.class);

    @Override
    public Representation represent(final Variant variant) throws ResourceException {
        final Map<String, Identity> identities = this.getApiController().getLinkedIdentities(this
                .getRegisteredService(), this.params);

        // default to the xml variant
        try {
            final DomRepresentation representation = new DomRepresentation(MediaType.APPLICATION_XML);

            // generate xml DOM for the attributes
            final Document d = representation.getDocument();
            final Element identitiesXml = d.createElement("identities");
            d.appendChild(identitiesXml);
            for (final Entry<String, Identity> identity : identities.entrySet()) {
                final Element identityXml = d.createElement("identity");
                identityXml.setAttribute("type", identity.getKey());
                identityXml.setAttribute("id", identity.getValue().id);
                identityXml.setAttribute("strength", identity.getValue().strength.toString());
                identitiesXml.appendChild(identityXml);
            }

            return representation;
        } catch (final IOException e) {
            LOG.error("error creating DomRepresentation of linked identities", e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }
}
