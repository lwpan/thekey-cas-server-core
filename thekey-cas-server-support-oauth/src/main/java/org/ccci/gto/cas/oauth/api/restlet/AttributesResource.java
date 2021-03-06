package org.ccci.gto.cas.oauth.api.restlet;

import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_ADDITIONALGUIDS;
import static org.ccci.gto.cas.Constants.PRINCIPAL_ATTR_GUID;
import static org.ccci.gto.cas.oauth.Constants.ERROR_BEARER_INSUFFICIENT_SCOPE;
import static org.ccci.gto.cas.oauth.Constants.ERROR_BEARER_INVALID_TOKEN;
import static org.ccci.gto.cas.oauth.Constants.SCOPE_ATTRIBUTES;
import static org.ccci.gto.cas.oauth.Constants.SCOPE_FULLTICKET;

import com.google.common.collect.ListMultimap;
import me.thekey.cas.service.UserManager;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.oauth.OAuthManager;
import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.persistence.tx.ReadOnlyTransactionService;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class AttributesResource extends AbstractProtectedResource {
    private final static Logger LOG = LoggerFactory.getLogger(AttributesResource.class);

    @NotNull
    @Autowired
    private ReadOnlyTransactionService txService;

    @NotNull
    @Autowired
    private OAuthManager oauthManager;

    @NotNull
    @Autowired
    private UserManager userManager;

    public final void setTransactionService(final ReadOnlyTransactionService txService) {
        this.txService = txService;
    }

    public void setOAuthManager(final OAuthManager oauthManager) {
        this.oauthManager = oauthManager;
    }

    public void setUserManager(final UserManager manager) {
        this.userManager = manager;
    }

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
        // look up the requested access token
        final String rawToken = this.getAccessToken();
        final AccessToken token;
        try {
            token = this.txService.inReadOnlyTransaction(new Callable<AccessToken>() {
                @Override
                public AccessToken call() throws Exception {
                    return oauthManager.getToken(AccessToken.class, rawToken);
                }
            });
        } catch (final Exception e) {
            LOG.error("error loading access_token for get attributes", e);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "unexpected error", e);
        }

        // ensure the access_token is valid
        if (token == null || token.isExpired()) {
            sendChallengeRequest(ERROR_BEARER_INVALID_TOKEN, "Unable to authenticate using provided access_token");
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
        }

        // make sure we have a scope that is authorized to access attributes
        if (!(token.isScopeAuthorized(SCOPE_FULLTICKET) || token.isScopeAuthorized(SCOPE_ATTRIBUTES))) {
            sendChallengeRequest(ERROR_BEARER_INSUFFICIENT_SCOPE, "Unable to authenticate using provided access_token");
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
        }

        // look up the GcxUser object for the access_token
        final GcxUser user = this.userManager.findUserByGuid(token.getGuid());

        // throw an error if the user doesn't exist!?!?!
        if (user == null) {
            LOG.error("cannot find user {}", token.getGuid());
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "unexpected error");
        }

        // retrieve the attributes for the current user
        final ListMultimap<String, String> attrs = this.userManager.getUserAttributes(user);

        // generate the response
        final Map<Object, Object> resp = new HashMap<>();
        resp.put("ssoGuid", token.getGuid());
        for (final String key : attrs.keySet()) {
            switch (key) {
                // skip these attributes
                case PRINCIPAL_ATTR_ADDITIONALGUIDS:
                case PRINCIPAL_ATTR_GUID:
                    break;
                default:
                    final List<String> vals = attrs.get(key);
                    if (vals.size() > 0) {
                        resp.put(key, vals.get(0));
                    }
            }
        }
        return new JsonRepresentation(resp);
    }
}
