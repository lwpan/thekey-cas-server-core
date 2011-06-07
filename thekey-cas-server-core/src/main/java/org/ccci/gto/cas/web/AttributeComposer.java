package org.ccci.gto.cas.web;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gto.cas.config.ServerConfigList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * composes attributes that we'll insert into a validation result. A list of
 * servers must be provided (via an xml configuration file, as implemented) who
 * are authorized to receive "extended attributes".
 * 
 * @author Ken Burcham
 * @author Daniel Frett
 */
public class AttributeComposer {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** Constant representing the guid in the attributes. */
    private static final String ATTRIBUTES_GUID = "ssoGuid";

    /** Constant representing the additional guids in the attributes. */
    private static final String ATTRIBUTES_ADDITIONALGUIDS = "GUIDAdditionalString";

    /** Constant representing the first name in the attributes. */
    private static final String ATTRIBUTES_FIRSTNAME = "firstName";

    /** Constant representing the last name in the attributes. */
    private static final String ATTRIBUTES_LASTNAME = "lastName";


    /** whitelist of servers authorized for additional attributes. */
    @NotNull
    private ServerConfigList whitelist;

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
	if (this.whitelist.inList(service)) {
	    attrs.put(ATTRIBUTES_FIRSTNAME, user.getFirstName());
	    attrs.put(ATTRIBUTES_LASTNAME, user.getLastName());
	}

	// return the generated attributes
	return attrs;
    }

    /**
     * @param whitelist
     *            the whitelist to use
     */
    public void setWhitelist(ServerConfigList whitelist) {
	this.whitelist = whitelist;
    }
}
