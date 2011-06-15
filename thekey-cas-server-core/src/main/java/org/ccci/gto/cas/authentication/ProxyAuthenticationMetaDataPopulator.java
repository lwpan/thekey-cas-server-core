package org.ccci.gto.cas.authentication;

import static org.ccci.gto.cas.Constants.AUTH_ATTR_PROXYPROVIDER;

import org.ccci.gto.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationMetaDataPopulator;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyAuthenticationMetaDataPopulator implements
	AuthenticationMetaDataPopulator {
    /** Instance of logging for subclasses. */
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /** attributes that specify what Credentials this MetaDataPopulator supports */
    private final Class<? extends Credentials> classToSupport;
    private final boolean supportSubClasses;

    private String proxyUri;

    public ProxyAuthenticationMetaDataPopulator() {
	this.classToSupport = Credentials.class;
	this.supportSubClasses = true;
    }

    public ProxyAuthenticationMetaDataPopulator(
	    final Class<? extends Credentials> classToSupport,
	    final boolean supportSubClasses) {
	this.classToSupport = classToSupport;
	this.supportSubClasses = supportSubClasses;
    }

    public Authentication populateAttributes(Authentication authentication,
	    final Credentials credentials) throws AuthenticationException {
	// only process if the provided credentials are supported
	if (this.supports(credentials)) {
	    // make sure the authentication object is mutable
	    authentication = AuthenticationUtil.makeMutable(authentication);

	    // set the proxy uri
	    authentication.getAttributes().put(AUTH_ATTR_PROXYPROVIDER,
		    this.proxyUri);
	}

	// return the authentication object
	return authentication;
    }

    /**
     * @return true if the credentials are not null and the credentials class is
     *         an implementation of Credentials.
     */
    public final boolean supports(final Credentials credentials) {
	return credentials != null
		&& (this.classToSupport.equals(credentials.getClass()) || (this.classToSupport
			.isAssignableFrom(credentials.getClass()) && this.supportSubClasses));
    }

    /**
     * @param proxyUri
     *            the proxyUri to set
     */
    public void setProxyUri(final String proxyUri) {
	this.proxyUri = proxyUri;
    }
}
