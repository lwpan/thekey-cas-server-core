package org.ccci.gto.cas.federation.authentication;

import static org.ccci.gto.cas.federation.Constants.AUTH_ATTR_PROXYPROVIDER;
import static org.ccci.gto.cas.federation.Constants.AUTH_ATTR_REQUIREPROXYVALIDATION;

import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationMetaDataPopulator;
import org.jasig.cas.authentication.principal.Credentials;

public class FederatedAuthenticationMetaDataPopulator implements AuthenticationMetaDataPopulator {
    /** attributes that specify what Credentials this MetaDataPopulator supports */
    private final Class<? extends Credentials> classToSupport;
    private final boolean supportSubClasses;

    private boolean requireProxyValidation = false;

    private String proxyUri;

    public FederatedAuthenticationMetaDataPopulator() {
        this.classToSupport = Credentials.class;
        this.supportSubClasses = true;
    }

    public FederatedAuthenticationMetaDataPopulator(final Class<? extends Credentials> classToSupport,
            final boolean supportSubClasses) {
        this.classToSupport = classToSupport;
        this.supportSubClasses = supportSubClasses;
    }

    public Authentication populateAttributes(Authentication authentication, final Credentials credentials) {
        // only process if the provided credentials are supported
        if (this.supports(credentials)) {
            // make sure the authentication object is mutable
            authentication = AuthenticationUtil.makeMutable(authentication);

            // set the proxy uri auth meta-data attributes
            authentication.getAttributes().put(AUTH_ATTR_PROXYPROVIDER, this.getProxyUri(credentials));
            authentication.getAttributes().put(AUTH_ATTR_REQUIREPROXYVALIDATION, this.requireProxyValidation);
        }

        // return the authentication object
        return authentication;
    }

    /**
     * @return true if the credentials are not null and the credentials class is
     *         an implementation of Credentials.
     */
    public boolean supports(final Credentials credentials) {
        return credentials != null
                && (this.classToSupport.equals(credentials.getClass()) || (this.classToSupport
                        .isAssignableFrom(credentials.getClass()) && this.supportSubClasses));
    }

    protected String getProxyUri(final Credentials credentials) {
        return this.proxyUri;
    }

    public void setRequireProxyValidation(final boolean requireProxyValidation) {
        this.requireProxyValidation = requireProxyValidation;
    }

    /**
     * @param proxyUri
     *            the proxyUri to set
     */
    public void setProxyUri(final String proxyUri) {
        this.proxyUri = proxyUri;
    }
}
