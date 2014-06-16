package me.thekey.cas.api.restlet;

import me.thekey.cas.api.ApiController;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractResource extends Resource {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractResource.class);

    @Autowired
    private ApiController apiController;

    private TheKeyRegisteredService service = null;

    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);

        // lookup the RegisteredService with the specified apiKey
        this.service = this.apiController.lookupServiceByApiKey((String) getRequest().getAttributes().get("apiKey"));

        // add supported response variants
        this.getVariants().add(new Variant(MediaType.APPLICATION_XML));
        this.getVariants().add(new Variant(MediaType.TEXT_XML));
    }

    protected Representation representAttributes(final Map<String, Object> attributes) throws ResourceException {
        // default to the xml variant
        try {
            final DomRepresentation representation = new DomRepresentation(MediaType.APPLICATION_XML);

            // generate xml DOM for the attributes
            final Document d = representation.getDocument();
            final Element attrs = d.createElement("attributes");
            d.appendChild(attrs);
            for (final Map.Entry<String, Object> attribute : attributes.entrySet()) {
                final Object value = attribute.getValue();

                // handle collections of values (i.e. additionalGuid)
                if (value instanceof Collection) {
                    for (final Object value2 : (Collection<?>) value) {
                        if (value2 instanceof String) {
                            final Element attr = d.createElement("attribute");
                            attr.setAttribute("name", attribute.getKey());
                            attr.setAttribute("value", (String) value2);
                            attrs.appendChild(attr);
                        }
                    }
                } else if (value != null) {
                    final Element attr = d.createElement("attribute");
                    attr.setAttribute("name", attribute.getKey());
                    attr.setAttribute("value", value.toString());
                    attrs.appendChild(attr);
                }
            }

            return representation;
        } catch (final IOException e) {
            LOG.error("error creating DomRepresentation of user attributes", e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    /**
     * @return the apiController
     */
    protected ApiController getApiController() {
        return apiController;
    }

    protected TheKeyRegisteredService getRegisteredService() {
        return this.service;
    }
}
