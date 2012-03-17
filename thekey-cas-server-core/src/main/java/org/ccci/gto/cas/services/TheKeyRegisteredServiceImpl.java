package org.ccci.gto.cas.services;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("thekey")
public class TheKeyRegisteredServiceImpl extends RegisteredServiceImpl
	implements TheKeyRegisteredService {
    private static final long serialVersionUID = -5380645036555088396L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(TheKeyRegisteredServiceImpl.class);

    private boolean legacyHeaders = false;

    private boolean legacyLogin = false;

    private boolean regex = false;

    private String contactEmail;

    private String templateCssUrl;

    @Transient
    private Pattern serviceRegex = null;

    @Override
    public String getContactEmail() {
	return this.contactEmail;
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

    /**
     * @param templateCssUrl
     *            the url of where to find the template css for this service
     */
    public void setTemplateCssUrl(final String templateCssUrl) {
	this.templateCssUrl = templateCssUrl;
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
		return this.getServiceRegex(false).matcher(service.getId())
			.find();
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
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jasig.cas.services.RegisteredServiceImpl#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
	final TheKeyRegisteredServiceImpl o = new TheKeyRegisteredServiceImpl();

	// TODO: utilize super.clone() once parent clone method supports
	// subclasses
	o.setAllowedAttributes(this.getAllowedAttributes());
	o.setAllowedToProxy(this.isAllowedToProxy());
	o.setDescription(this.getDescription());
	o.setEnabled(this.isEnabled());
	o.setId(this.getId());
	o.setName(this.getName());
	o.setServiceId(this.getServiceId());
	o.setSsoEnabled(this.isSsoEnabled());
	o.setTheme(this.getTheme());
	o.setAnonymousAccess(this.isAnonymousAccess());
	o.setIgnoreAttributes(this.isIgnoreAttributes());
	o.setEvaluationOrder(this.getEvaluationOrder());
	o.setContactEmail(this.contactEmail);
	o.setLegacyHeaders(this.legacyHeaders);
	o.setLegacyLogin(this.legacyLogin);
	o.setRegex(this.regex);
	o.setTemplateCssUrl(this.templateCssUrl);

	return o;
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
