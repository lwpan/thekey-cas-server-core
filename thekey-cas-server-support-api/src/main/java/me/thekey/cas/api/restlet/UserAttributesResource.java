package me.thekey.cas.api.restlet;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class UserAttributesResource extends AbstractUserQueryResource {
    @Override
    public Representation represent(final Variant variant) throws ResourceException {
        return this.representAttributes(this.getApiController().getUserAttributes(this.getRegisteredService(),
                this.params));
    }
}
