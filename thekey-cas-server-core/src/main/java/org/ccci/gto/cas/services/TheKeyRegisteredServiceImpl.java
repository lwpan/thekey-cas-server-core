package org.ccci.gto.cas.services;

import javax.persistence.Entity;

import org.jasig.cas.services.RegisteredServiceImpl;

@Entity
public class TheKeyRegisteredServiceImpl extends RegisteredServiceImpl
	implements TheKeyRegisteredService {
    private static final long serialVersionUID = -5380645036555088396L;

    private boolean legacyHeaders = false;

    private boolean legacyLogin = false;

    private String contactEmail;

    public String getContactEmail() {
	return this.contactEmail;
    }

    public boolean isLegacyHeaders() {
	return this.legacyHeaders;
    }

    public boolean isLegacyLogin() {
	return this.legacyLogin;
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

	return o;
    }
}