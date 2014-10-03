package org.ccci.gto.cas.facebook.authentication.handler;

import static me.thekey.cas.authentication.principal.TheKeyCredentials.Lock.NULLUSER;

import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.User;
import me.thekey.cas.facebook.authentication.handler.OAuth2ClientAuthenticationHandler;
import me.thekey.cas.facebook.authentication.principal.OAuth2ClientCredentials;
import me.thekey.cas.service.UserManager;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.facebook.restfb.FacebookClient;
import org.ccci.gto.cas.facebook.util.FacebookUtils;
import org.ccci.gto.cas.federation.authentication.handler.UnknownIdentityAuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

public class FacebookAuthenticationHandler extends OAuth2ClientAuthenticationHandler {
    @Inject
    @NotNull
    private UserManager userManager;

    @NotNull
    private String appId;

    @NotNull
    private String secret;

    /**
     * @param appId the appId to set
     */
    public void setAppId(final String appId) {
        this.appId = appId;
    }

    /**
     * @param secret the secret to set
     */
    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public FacebookAuthenticationHandler() {
        super(FacebookCredentials.class, false);
    }

    public FacebookAuthenticationHandler(final Class<? extends FacebookCredentials> classToSupport,
                                         final boolean supportSubClasses) {
        super(classToSupport, supportSubClasses);
    }

    @Override
    protected boolean authenticateOAuth2Internal(final OAuth2ClientCredentials rawCreds) throws
            AuthenticationException {
        final FacebookCredentials credentials = (FacebookCredentials) rawCreds;

        // reject any invalid signed requests
        final String signedRequest = credentials.getSignedRequest();
        final JsonObject data = FacebookUtils.getSignedData(signedRequest);
        if (!FacebookUtils.validateSignedRequest(signedRequest, this.secret, data.optString("algorithm"))) {
            return false;
        }

        // exchange the authorization code for an access token if we haven't
        // already done it. Checking for an existing access token is necessary
        // for login after identity linking.
        String accessToken = credentials.getAccessToken();
        if (accessToken == null) {
            // exchange the authorization code for an access token
            accessToken = new FacebookClient().exchangeCodeForAccessToken(this.appId, this.secret,
                    credentials.getCode(), "");
            credentials.setAccessToken(accessToken);
        }

        // accept FacebookCredentials if they resolved to an accessToken
        return credentials.getAccessToken() != null;
    }

    @Override
    protected void lookupUser(final OAuth2ClientCredentials rawCredentials) throws AuthenticationException {
        final FacebookCredentials credentials = (FacebookCredentials) rawCredentials;
        final JsonObject data = FacebookUtils.getSignedData(credentials.getSignedRequest());
        final String facebookId = data.getString("user_id");

        // query facebook for the user's attributes to verify this is a
        // currently active session and not a replay attack
        final String accessToken = credentials.getAccessToken();
        final FacebookClient fbClient = new FacebookClient(accessToken);
        final User fbUser = fbClient.fetchObject("me", User.class, Parameter.with("fields", "id,first_name,last_name,email"));
        final String facebookId2 = fbUser.getId();

        // throw an error if facebook returns a different facebook id
        if (!facebookId.equals(facebookId2)) {
            throw new BadCredentialsAuthenticationException();
        }

        // store the fbUser object in the credentials
        credentials.setFbUser(fbUser);

        // lookup the user logging in
        final GcxUser user = this.userManager.findUserByFacebookId(facebookId);
        credentials.setUser(user);

        // throw an unknown identity exception if the user wasn't found
        if (credentials.observeLock(NULLUSER) && user == null) {
            throw UnknownIdentityAuthenticationException.ERROR;
        }
    }
}
