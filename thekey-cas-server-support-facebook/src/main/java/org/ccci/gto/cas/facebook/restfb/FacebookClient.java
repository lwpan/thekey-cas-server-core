package org.ccci.gto.cas.facebook.restfb;

import java.net.URLDecoder;

import com.restfb.DefaultFacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.WebRequestor;

public class FacebookClient extends DefaultFacebookClient {
    public FacebookClient() {
	super();
    }

    public FacebookClient(final String accessToken) {
	super(accessToken);
    }

    public FacebookClient(final String accessToken,
	    final WebRequestor webRequestor, final JsonMapper jsonMapper) {
	super(accessToken, webRequestor, jsonMapper);
    }

    public String exchangeCodeForAccessToken(final String appId,
	    final String secretKey, final String code, final String redirectUri) {
	verifyParameterPresence("appId", appId);
	verifyParameterPresence("secretKey", secretKey);

	final String response = makeRequest("/oauth/access_token",
		Parameter.with("client_id", appId),
		Parameter.with("client_secret", secretKey),
		Parameter.with("code", code),
		Parameter.with("redirect_uri", redirectUri));

	for (final String param : response.split("&")) {
	    try {
		final String[] parts = param.split("=");
		if (parts[0].equals("access_token")) {
		    return URLDecoder.decode(parts[1], "UTF-8");
		}
	    } catch (final Exception e) {
	    }
	}

	return null;
    }
}
