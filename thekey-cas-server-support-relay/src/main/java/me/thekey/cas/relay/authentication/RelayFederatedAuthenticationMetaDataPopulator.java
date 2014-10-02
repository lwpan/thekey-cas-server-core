package me.thekey.cas.relay.authentication;

import static org.jasig.cas.authentication.SamlAuthenticationMetaDataPopulator.ATTRIBUTE_AUTHENTICATION_METHOD;

import me.thekey.cas.authentication.principal.TheKeyCredentials;
import me.thekey.cas.relay.authentication.util.RelayAuthenticationUtil;
import org.ccci.gto.cas.federation.authentication.FederatedAuthenticationMetaDataPopulator;
import me.thekey.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Credentials;

import javax.validation.constraints.NotNull;

public class RelayFederatedAuthenticationMetaDataPopulator extends FederatedAuthenticationMetaDataPopulator {
    @NotNull
    private String authenticationMethod;

    public RelayFederatedAuthenticationMetaDataPopulator() {
        super(TheKeyCredentials.class, true);
    }

    public void setAuthenticationMethod(final String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    @Override
    public boolean supports(final Credentials credentials) {
        return super.supports(credentials) && RelayAuthenticationUtil.getAssertion((TheKeyCredentials) credentials)
                != null;
    }

    @Override
    public Authentication populateAttributes(Authentication authentication, final Credentials credentials) {
        authentication = super.populateAttributes(authentication, credentials);

        // only process if the provided credentials are supported
        if (this.supports(credentials)) {
            // make sure the authentication object is mutable
            authentication = AuthenticationUtil.makeMutable(authentication);

            // override the SAML authentication method
            authentication.getAttributes().put(ATTRIBUTE_AUTHENTICATION_METHOD, this.authenticationMethod);
        }

        return authentication;
    }
}
