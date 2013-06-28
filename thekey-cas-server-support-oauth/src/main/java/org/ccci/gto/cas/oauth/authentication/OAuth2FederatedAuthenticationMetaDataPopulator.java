package org.ccci.gto.cas.oauth.authentication;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.federation.authentication.FederatedAuthenticationMetaDataPopulator;
import org.ccci.gto.cas.oauth.model.AccessToken;
import org.ccci.gto.cas.oauth.model.Client;
import org.jasig.cas.authentication.principal.Credentials;

public class OAuth2FederatedAuthenticationMetaDataPopulator extends FederatedAuthenticationMetaDataPopulator {
    @NotNull
    private String baseUri;

    public OAuth2FederatedAuthenticationMetaDataPopulator() {
    }

    public OAuth2FederatedAuthenticationMetaDataPopulator(final Class<? extends OAuth2Credentials> classToSupport,
            final boolean supportSubClasses) {
        super(classToSupport, supportSubClasses);
    }

    @Override
    protected String getProxyUri(final Credentials credentials) {
        if (credentials instanceof OAuth2Credentials) {
            final AccessToken token = ((OAuth2Credentials) credentials).getAccessToken();
            if (token != null) {
                final Client client = token.getClient();
                if (client != null) {
                    return this.baseUri + client.getId();
                }
            }
        }

        // return an error oauth uri
        return this.baseUri + "error";
    }

    public String getBaseUri() {
        return this.baseUri;
    }

    public void setBaseUri(final String baseUri) {
        this.baseUri = baseUri;
    }
}
