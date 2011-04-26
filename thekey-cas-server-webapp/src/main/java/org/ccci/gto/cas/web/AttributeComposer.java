package org.ccci.gto.cas.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.config.XmlConfigurator;

/**
 * composes attributes that we'll insert into a validation result. A list of
 * servers must be provided (via an xml configuration file, as implemented) who
 * are authorized to receive "extended attributes".
 * 
 * @author Ken Burcham
 * @author Daniel Frett
 */
public class AttributeComposer {
    private static final Log log = LogFactory.getLog(AttributeComposer.class);

    private static final String CONFIG_SERVERELEMENT = "server";
    private static final String DEFAULT_WHITELIST = "classpath:config/whitelist.xml";

    /** Constant representing the guid in the attributes. */
    private static final String ATTRIBUTES_GUID = "ssoGuid";

    /** Constant representing the additional guids in the attributes. */
    private static final String ATTRIBUTES_ADDITIONALGUIDS = "GUIDAdditionalString";

    /** Constant representing the first name in the attributes. */
    private static final String ATTRIBUTES_FIRSTNAME = "firstName";

    /** Constant representing the last name in the attributes. */
    private static final String ATTRIBUTES_LASTNAME = "lastName";

    /** whitelist of servers authorized for additional attributes. */
    private final ArrayList<String> whitelist = new ArrayList<String>();
    private String whitelistLocation;

    public AttributeComposer() {
	this(DEFAULT_WHITELIST);
    }

    public AttributeComposer(String location) {
	this.setWhitelist(location);
    }

    /**
     * returns a Map representing the <cas:attribute> entities for the specified
     * user.
     * 
     * @param user
     *            the user to generate the attributes for
     * @param service
     *            the service the attributes are being returned to
     * @return
     */
    public Map<String, String> getUserAttributes(final GcxUser user,
	    final String service) throws Exception {
	HashMap<String, String> attrs = new HashMap<String, String>();

	// any extended attributes that everyone gets can go here.
	attrs.put(ATTRIBUTES_GUID, user.getGUID());
	attrs.put(ATTRIBUTES_ADDITIONALGUIDS, user.getGUIDAdditionalString());

	// only authorized services get extended data
	if (this.inWhitelist(service)) {
	    attrs.put(ATTRIBUTES_FIRSTNAME, user.getFirstName());
	    attrs.put(ATTRIBUTES_LASTNAME, user.getLastName());
	}

	// return the generated attributes
	return attrs;
    }

    /**
     * method that loads the whitelist into memory
     */
    private synchronized void loadWhitelist() {
	if (log.isDebugEnabled()) {
	    log.debug("loading whitelist from: " + this.whitelistLocation);
	}

	try {
	    // clear any existing parameters in the whitelist
	    this.whitelist.clear();

	    // load the config
	    XmlConfigurator config = new XmlConfigurator(this.whitelistLocation);
	    config.refresh();

	    // expand the list to include some possible variations.
	    // TODO: this code needs to become much more robust
	    for (String authority : config
		    .getListAsString(CONFIG_SERVERELEMENT)) {
		if (authority.startsWith("www.")) {
		    this.whitelist.add(authority);
		    this.whitelist.add(authority.substring(4));
		} else {
		    this.whitelist.add(authority);
		    this.whitelist.add("www." + authority);
		}
	    }
	} catch (Exception e) {
	    this.whitelist.clear();
	    log.error("There was a problem loading the whitelist.", e);
	}
    }

    /**
     * determines if a particular service is in the extended service list
     * 
     * @param service
     * @return
     */
    private boolean inWhitelist(String service) {
	try {
	    // parse the service to extract the authority
	    final URL url = new URL(service);

	    // return true if the authority is in the whitelist
	    if (this.whitelist.contains(url.getAuthority())) {
		return true;
	    }
	} catch (MalformedURLException e) {
	    if (log.isDebugEnabled())
		log.debug("Could not parse service to URL: " + service);
	}

	return false;
    }

    /**
     * @param whitelistLocation
     *            the whitelistLocation to set
     */
    public void setWhitelist(String location) {
	this.whitelistLocation = location;
	this.loadWhitelist();
    }
}
