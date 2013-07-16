package org.ccci.gto.cas.oauth.api.restlet;

import org.restlet.data.ChallengeScheme;

public final class Constants {
    public static final ChallengeScheme HTTP_OAUTH_BEARER = new ChallengeScheme("HTTP_Bearer", "Bearer",
            "OAuth 2.0 Authorization Protocol: Bearer Tokens (RFC 6750)");
}
