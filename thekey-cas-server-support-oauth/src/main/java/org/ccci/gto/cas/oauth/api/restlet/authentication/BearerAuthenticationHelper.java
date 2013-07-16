package org.ccci.gto.cas.oauth.api.restlet.authentication;

import static org.ccci.gto.cas.oauth.Constants.PARAM_ERROR;
import static org.ccci.gto.cas.oauth.Constants.PARAM_ERROR_DESCRIPTION;
import static org.ccci.gto.cas.oauth.Constants.PARAM_SCOPE;
import static org.ccci.gto.cas.oauth.api.restlet.Constants.HTTP_OAUTH_BEARER;

import java.util.HashSet;
import java.util.Set;

import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Parameter;
import org.restlet.data.Request;
import org.restlet.util.Series;

import com.noelios.restlet.authentication.AuthenticationHelper;

public class BearerAuthenticationHelper extends AuthenticationHelper {
    private static final Set<String> SUPPORTED_PARAMS = new HashSet<String>();
    static {
        SUPPORTED_PARAMS.add(PARAM_ERROR);
        SUPPORTED_PARAMS.add(PARAM_ERROR_DESCRIPTION);
        SUPPORTED_PARAMS.add(PARAM_SCOPE);
    }

    public BearerAuthenticationHelper() {
        super(HTTP_OAUTH_BEARER, false, true);
    }

    @Override
    public void formatParameters(final StringBuilder sb, final Series<Parameter> parameters,
            final ChallengeRequest request) {
        boolean hasParams = request.getRealm() != null;

        // process any supported parameters
        for (final Parameter p : parameters) {
            if (SUPPORTED_PARAMS.contains(p.getName())) {
                sb.append(hasParams ? "," : " ");
                sb.append(p.getName());
                sb.append("=\"");
                sb.append(p.getValue());
                sb.append("\"");
                hasParams = true;
            }
        }
    }

    @Override
    public void formatCredentials(final StringBuilder sb, final ChallengeResponse challenge, final Request request,
            final Series<Parameter> httpHeaders) {
        // TODO: should we implement this?
    }
}
