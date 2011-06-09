package org.ccci.gto.cas.authentication.principal;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class TheKeyUsernamePasswordCredentials extends
	UsernamePasswordCredentials {
    /** Unique ID for serialization. */
    private static final long serialVersionUID = -9122802431823292586L;

    /** flag indicating if administrative locks should be observed */
    private boolean locks = true;

    /**
     * This method sets whether or not administrative locks should be observed
     * when validating these credentials
     * 
     * @param observe
     *            flag indicating if locks should be observed
     */
    public void setObserveLocks(final boolean observe) {
	this.locks = observe;
    }

    /**
     * @return the administrativeLocks
     */
    public boolean observeLocks() {
	return locks;
    }
}
