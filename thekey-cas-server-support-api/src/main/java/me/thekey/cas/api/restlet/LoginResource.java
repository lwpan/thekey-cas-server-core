package me.thekey.cas.api.restlet;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import org.ccci.gto.cas.authentication.principal.TheKeyUsernamePasswordCredentials;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;

import java.util.Map;

public final class LoginResource extends AbstractResource {
    @Override
    public void acceptRepresentation(final Representation entity) throws ResourceException {
        final Form form = getRequest().getEntityAsForm();
        final TheKeyUsernamePasswordCredentials credentials = new TheKeyUsernamePasswordCredentials();
        credentials.setUsername(form.getFirstValue("username"));
        credentials.setPassword(form.getFirstValue("password"));
        if (form.getFirstValue("disableFederation") != null) {
            credentials.setObserveLock(TheKeyCredentials.Lock.FEDERATIONALLOWED, false);
        }

        // authenticate & fetch attributes
        final Map<String, Object> attrs = this.getApiController().federatedLogin(this.getRegisteredService(),
                credentials);

        // output attributes if we have any
        getResponse().setStatus(Status.SUCCESS_OK);
        getResponse().setEntity(this.representAttributes(attrs));
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean allowPost() {
        return true;
    }
}
