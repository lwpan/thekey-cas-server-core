package org.ccci.gto.cas.api.restlet;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.ccci.gto.cas.api.ApiController.Identity;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LinkedIdentitiesResource extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(LinkedIdentitiesResource.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.api.restlet.AbstractResource#init(org.restlet.Context,
     * org.restlet.data.Request, org.restlet.data.Response)
     */
    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);

        // add supported response variants
        this.getVariants().add(new Variant(MediaType.APPLICATION_XML));
        this.getVariants().add(new Variant(MediaType.TEXT_XML));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.restlet.resource.Resource#represent(org.restlet.resource.Variant)
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException {
        final Map<String, Identity> identities = this.getApiController().getLinkedIdentities(
                this.getRegisteredService(), this.getQueryParams());

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
