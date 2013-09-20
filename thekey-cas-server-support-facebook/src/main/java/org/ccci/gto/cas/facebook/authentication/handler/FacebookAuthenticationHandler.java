package org.ccci.gto.cas.facebook.authentication.handler;

import static org.ccci.gto.cas.facebook.Constants.STRENGTH_AUTOCREATE;
import static org.ccci.gto.cas.facebook.Constants.STRENGTH_MATCHINGEMAIL;

import javax.validation.constraints.NotNull;

import me.thekey.cas.service.UserManager;

import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.authentication.handler.FacebookIdAlreadyExistsAuthenticationException;
import org.ccci.gto.cas.authentication.handler.FacebookVivifyAuthenticationException;
import org.ccci.gto.cas.authentication.principal.FacebookCredentials;
import org.ccci.gto.cas.authentication.principal.OAuth2Credentials;
import org.ccci.gto.cas.facebook.restfb.FacebookClient;
import org.ccci.gto.cas.facebook.util.FacebookUtils;
import org.ccci.gto.cas.federation.FederationException;
import org.ccci.gto.cas.federation.FederationProcessor;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.User;

public class FacebookAuthenticationHandler extends OAuth2AuthenticationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FacebookAuthenticationHandler.class);

    @NotNull
    private UserManager userService;

    @NotNull
    private String appId;

    @NotNull
    private String secret;

    @NotNull
    private FederationProcessor federationProcessor;

    public void setUserService(final UserManager userService) {
        this.userService = userService;
    }

    /**
     * @param appId
     *            the appId to set
     */
    public void setAppId(final String appId) {
        this.appId = appId;
    }

    /**
     * @param secret
     *            the secret to set
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

    public void setFederationProcessor(final FederationProcessor federationProcessor) {
        this.federationProcessor = federationProcessor;
    }

    @Override
    protected boolean authenticateOAuth2Internal(final OAuth2Credentials rawCreds) throws AuthenticationException {
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
    protected void lookupUser(final OAuth2Credentials rawCredentials) throws AuthenticationException {
        final FacebookCredentials credentials = (FacebookCredentials) rawCredentials;
        final JsonObject data = FacebookUtils.getSignedData(credentials.getSignedRequest());
        final String facebookId = data.getString("user_id");

        // query facebook for the user's attributes to verify this is a
        // currently active session and not a replay attack
        final String accessToken = credentials.getAccessToken();
        final FacebookClient fbClient = new FacebookClient(accessToken);
        final User fbUser = fbClient.fetchObject("me", User.class,
                Parameter.with("fields", "id,first_name,last_name,email"));
        final String facebookId2 = fbUser.getId();

        // throw an error if facebook returns a different facebook id
        if (!facebookId.equals(facebookId2)) {
            throw new BadCredentialsAuthenticationException();
        }

        // store the fbUser object in the credentials
        credentials.setFbUser(fbUser);

        // lookup the user logging in
        credentials.setUser(this.userService.findUserByFacebookId(facebookId));

        // vivify the user if they don't exist yet
        if (credentials.getUser() == null && credentials.isVivify()) {
            try {
                if (this.federationProcessor.supports(credentials)) {
                    // see if a Key account already exists for this email
                    final GcxUser current = this.userService.findUserByEmail(fbUser.getEmail());
                    if (current != null) {
                        if (StringUtils.isNotBlank(current.getFacebookId())) {
                            LOG.error("{} already has another facebook account linked to it", current.getEmail());
                            throw FacebookIdAlreadyExistsAuthenticationException.ERROR;
                        }

                        if (!this.federationProcessor.linkIdentity(current, credentials, STRENGTH_MATCHINGEMAIL)) {
                            throw FacebookVivifyAuthenticationException.ERROR;
                        }
                    }
                    // account doesn't exist, create a new identity
                    else {
                        if (!this.federationProcessor.createIdentity(credentials, STRENGTH_AUTOCREATE)) {
                            throw FacebookVivifyAuthenticationException.ERROR;
                        }
                    }
                }
            } catch (final FederationException e) {
                throw FacebookVivifyAuthenticationException.ERROR;
            }

            // try looking up the account again
            credentials.setUser(this.userService.findUserByFacebookId(facebookId));
        }
    }
}
