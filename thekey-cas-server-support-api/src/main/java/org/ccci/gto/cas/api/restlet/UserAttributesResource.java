package org.ccci.gto.cas.api.restlet;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_EMAIL;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FACEBOOKID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_FIRSTNAME;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_LASTNAME;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.services.TheKeyRegisteredService;
import org.restlet.Context;
import org.restlet.data.Form;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UserAttributesResource extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(UserAttributesResource.class);

    @Autowired
    private GcxUserService userService;

    private GcxUser user = null;

    private HashMap<String, Object> attributes = new HashMap<String, Object>();

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

        // lookup the requested user attributes
        final TheKeyRegisteredService service = this.getRegisteredService();
        if (service != null) {
            final Form query = request.getResourceRef().getQueryAsForm();

            // can we find a requested user?
            final String guid = query.getFirstValue("guid");
            final String email = query.getFirstValue("email");
            if (StringUtils.isNotBlank(guid) && StringUtils.isNotBlank(email)) {
                this.setError(new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST));
            } else if (StringUtils.isNotBlank(guid)) {
                this.user = this.userService.findUserByGuid(guid);
            } else if (StringUtils.isNotBlank(email)) {
                this.user = this.userService.findUserByEmail(email);
            } else {
                this.setAvailable(false);
            }

            // get all allowed attributes for the requested user
            if (this.user != null) {
                if (!service.isIgnoreAttributes()) {
                    for (final String name : service.getAllowedAttributes()) {
                        // get the value of the current attribute
                        final Object value;
                        if (PRINCIPAL_ATTR_GUID.equals(name)) {
                            value = this.user.getGUID();
                        } else if (PRINCIPAL_ATTR_ADDITIONALGUIDS.equals(name)) {
                            value = this.user.getGUIDAdditional();
                        } else if (PRINCIPAL_ATTR_FACEBOOKID.equals(name)) {
                            value = this.user.getFacebookId();
                        } else if (PRINCIPAL_ATTR_EMAIL.equals(name)) {
                            value = this.user.getEmail();
                        } else if (PRINCIPAL_ATTR_FIRSTNAME.equals(name)) {
                            value = this.user.getFirstName();
                        } else if (PRINCIPAL_ATTR_LASTNAME.equals(name)) {
                            value = this.user.getLastName();
                        } else {
                            value = null;
                        }

                        // only store attributes that exist
                        if (value != null) {
                            this.attributes.put(name, value);
                        }
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.restlet.resource.Resource#represent(org.restlet.resource.Variant)
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException {
        this.assertAuthorized();
        this.assertValidRequest();

        if (this.user != null) {
            // default to the xml variant
            try {
                final DomRepresentation representation = new DomRepresentation(MediaType.APPLICATION_XML);

                // generate xml DOM for the attributes
                final Document d = representation.getDocument();
                final Element attrs = d.createElement("attributes");
                d.appendChild(attrs);
                for (final Entry<String, Object> attribute : this.attributes.entrySet()) {
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

        return null;
    }
}
