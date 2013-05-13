package org.ccci.gto.cas.oauth.api.restlet;

import static org.ccci.gto.cas.Constants.PARAM_SERVICE;
import static org.ccci.gto.cas.oauth.Constants.SCOPE_FULLTICKET;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.oauth.authentication.OAuth2Credentials;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.util.HttpClient;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class TicketResource extends AbstractProtectedResource {
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    private HttpClient httpClient;

    @Override
    public void init(final Context context, final Request request, final Response response) {
        super.init(context, request, response);

        // add supported response variants
        this.getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    }

    @Override
    public boolean isModifiable() {
        return false;
    }

    @Override
    public Representation represent(final Variant variant) throws ResourceException {
        final OAuth2Credentials credentials = this.getCredentials();
        credentials.addRequiredScope(SCOPE_FULLTICKET);

        // retrieve the service from the request
        final Service service = new SimpleWebApplicationServiceImpl(this.getQuery().getFirstValue(PARAM_SERVICE),
                httpClient);
        if (service.getId() == null) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "no service specified to issue ticket for");
        }

        // validate the OAuth2 credentials and issue a ticket
        final String ticket;
        try {
            final String tgtId = this.centralAuthenticationService.createTicketGrantingTicket(credentials);
            ticket = this.centralAuthenticationService.grantServiceTicket(tgtId, service);
        } catch (final TicketException e) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,
                    "Unable to authenticate using provided access_token", e);
        } catch (final UnauthorizedServiceException e) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,
                    "Specified service is not authorized to use CAS", e);
        } catch (final Exception e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "unexpected error", e);
        }

        // generate the response
        final Map<Object, Object> resp = new HashMap<Object, Object>();
        resp.put("ticket", ticket);
        return new JsonRepresentation(resp);
    }

    public void setCentralAuthenticationService(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
