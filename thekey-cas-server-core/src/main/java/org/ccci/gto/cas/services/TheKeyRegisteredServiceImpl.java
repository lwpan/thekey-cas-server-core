package org.ccci.gto.cas.services;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Transient;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.RegisteredServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("thekey")
public class TheKeyRegisteredServiceImpl extends RegisteredServiceImpl
	implements TheKeyRegisteredService {
    private static final long serialVersionUID = 4827197520682265599L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(TheKeyRegisteredServiceImpl.class);

    private boolean legacyHeaders = false;

    private boolean legacyLogin = false;

    private boolean regex = false;

    private boolean apiEnabled = false;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @JoinTable(name = "rs_apis", joinColumns = @JoinColumn(name = "RegisteredServiceImpl_id"))
    @Column(name = "api", nullable = false)
    private Set<String> supportedApis = new HashSet<String>();

    private String apiKey;

    private String contactEmail;

    private String templateCssUrl;

    private String viewName;

    @Transient
    private Pattern serviceRegex = null;

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public String getContactEmail() {
	return this.contactEmail;
    }

    @Override
    public Set<String> getSupportedApis() {
        return this.supportedApis;
    }

    @Override
    public String getViewName() {
        return this.viewName;
    }

    @Override
    public boolean isApiEnabled() {
        return this.apiEnabled;
    }

    @Override
    public boolean isApiSupported(final String name) {
        return this.isApiEnabled() && this.supportedApis != null && this.supportedApis.contains(name);
    }

    @Override
    public boolean isLegacyHeaders() {
	return this.legacyHeaders;
    }

    @Override
    public boolean isLegacyLogin() {
	return this.legacyLogin;
    }

    @Override
    public boolean isRegex() {
	return regex;
    }

    public void setApiEnabled(final boolean apiEnabled) {
        this.apiEnabled = apiEnabled;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public void setContactEmail(final String email) {
	this.contactEmail = email;
    }

    /**
     * @param legacyHeaders
     *            the legacyHeaders to set
     */
    public void setLegacyHeaders(final boolean legacyHeaders) {
	this.legacyHeaders = legacyHeaders;
    }

    /**
     * @param legacyLogin
     *            the legacyLogin to set
     */
    public void setLegacyLogin(final boolean legacyLogin) {
	this.legacyLogin = legacyLogin;
    }

    /**
     * @param regex
     *            a flag indicating if the serviceId is a regular expression
     */
    public void setRegex(final boolean regex) {
	this.regex = regex;
    }

    public void setSupportedApis(final Set<String> supportedApis) {
        this.supportedApis = supportedApis;
    }

    /**
     * @param templateCssUrl
     *            the url of where to find the template css for this service
     */
    public void setTemplateCssUrl(final String templateCssUrl) {
	this.templateCssUrl = templateCssUrl;
    }

    public void setViewName(final String viewName) {
        this.viewName = viewName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jasig.cas.services.RegisteredServiceImpl#setServiceId(java.lang.String
     * )
     */
    @Override
    public void setServiceId(final String id) {
	super.setServiceId(id);
	serviceRegex = null;
    }

    /**
     * Compile and return a compiled regular expression for the current
     * serviceId
     * 
     * @param recompile
     *            flag indicating if the regular expression should be recompiled
     * @return a compiled Pattern for the current serviceId
     */
    private Pattern getServiceRegex(final boolean recompile) {
	if (serviceRegex == null || recompile) {
	    try {
		serviceRegex = Pattern.compile(this.getServiceId(),
			Pattern.CASE_INSENSITIVE);
	    } catch (final PatternSyntaxException e) {
		LOG.debug("error compiling regex", e);
		serviceRegex = null;
	    }
	}

	return serviceRegex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jasig.cas.services.RegisteredServiceImpl#matches(org.jasig.cas.
     * authentication.principal.Service)
     */
    @Override
    public boolean matches(final Service service) {
	// undefined services should never match a registered service
	if (service == null) {
	    return false;
	}

	// should regular expressions be used for matching
	if (this.isRegex()) {
	    try {
                // make sure there is a valid regex before
                final Pattern regex = this.getServiceRegex(false);
                if (regex == null) {
                    return false;
                }

                // check to see if the service matches the regex
                return regex.matcher(service.getId()).find();
	    } catch (final Exception e) {
		LOG.debug("error matching service", e);
		// assume false if there were any errors with the regular
		// expression
		return false;
	    }
	} else {
	    return super.matches(service);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jasig.cas.services.RegisteredServiceImpl#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
	if (this == o) return true;
	if (!(o instanceof TheKeyRegisteredServiceImpl)) return false;

	final TheKeyRegisteredServiceImpl that = (TheKeyRegisteredServiceImpl) o;
	if (contactEmail != null ? !contactEmail.equals(that.contactEmail) : that.contactEmail != null) return false;
	if (legacyHeaders != that.legacyHeaders) return false;
	if (legacyLogin != that.legacyLogin) return false;
	if (regex != that.regex) return false;
	if (templateCssUrl != null ? !templateCssUrl.equals(that.templateCssUrl) : that.templateCssUrl != null) return false;
        if (apiEnabled != that.apiEnabled) return false;
        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        if (supportedApis != null ? !supportedApis.equals(that.supportedApis) : that.supportedApis != null) return false;
        if (viewName != null ? !viewName.equals(that.viewName) : that.viewName != null) return false;

	return super.equals(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jasig.cas.services.RegisteredServiceImpl#hashCode()
     */
    @Override
    public int hashCode() {
	int result = super.hashCode();
	result = 31 * result + (contactEmail != null ? contactEmail.hashCode() : 0);
	result = 31 * result + (legacyHeaders ? 1 : 0);
	result = 31 * result + (legacyLogin ? 1 : 0);
	result = 31 * result + (regex ? 1 : 0);
	result = 31 * result + (templateCssUrl != null ? templateCssUrl.hashCode() : 0);
        result = 31 * result + (apiEnabled ? 1 : 0);
        result = 31 * result + (apiKey != null ? apiKey.hashCode() : 0);
        result = 31 * result + (supportedApis != null ? supportedApis.hashCode() : 0);
        result = 31 * result + (viewName != null ? viewName.hashCode() : 0);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jasig.cas.services.RegisteredServiceImpl#newInstance()
     */
    @Override
    protected TheKeyRegisteredServiceImpl newInstance() {
        return new TheKeyRegisteredServiceImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jasig.cas.services.AbstractRegisteredService#copyFrom(org.jasig.cas
     * .services.RegisteredService)
     */
    @Override
    public void copyFrom(final RegisteredService source) {
        // copy base properties
        super.copyFrom(source);

        // copy properties for TheKeyRegisteredService
        if (source instanceof TheKeyRegisteredService) {
            this.setContactEmail(((TheKeyRegisteredService) source).getContactEmail());
            this.setLegacyHeaders(((TheKeyRegisteredService) source).isLegacyHeaders());
            this.setLegacyLogin(((TheKeyRegisteredService) source).isLegacyLogin());
            this.setRegex(((TheKeyRegisteredService) source).isRegex());
            this.setTemplateCssUrl(((TheKeyRegisteredService) source).getTemplateCssUrl());
            this.setApiEnabled(((TheKeyRegisteredService) source).isApiEnabled());
            this.setSupportedApis(((TheKeyRegisteredService) source).getSupportedApis());
            this.setApiKey(((TheKeyRegisteredService) source).getApiKey());
            this.setViewName(((TheKeyRegisteredService) source).getViewName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ccci.gto.cas.services.TheKeyRegisteredService#getTemplateCssUrl()
     */
    @Override
    public String getTemplateCssUrl() {
	return this.templateCssUrl;
    }
}
