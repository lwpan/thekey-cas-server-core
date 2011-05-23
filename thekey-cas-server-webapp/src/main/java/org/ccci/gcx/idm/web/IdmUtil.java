package org.ccci.gcx.idm.web;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;

/**
 * provides various utility methods used by idmweb.
 * @author ken
 *
 */
public final class IdmUtil {
	protected static final Log log = LogFactory.getLog(IdmUtil.class);

    /**
     * adds a domain visited to a particular user's additional domain list.
     * 
     * @param user
     * @param url
     *            the url being visited
     * @param userService
     * @param source
     *            the identifier for the source of this addition
     */
    public static void addDomainVisited(GcxUser user, final String url,
	    GcxUserService userService, String source) {
	// extract the host from the url if possible
	String host = null;
	try {
	    URL u = new URL(url);
	    host = u.getHost();
	} catch (MalformedURLException e) {
	    log.error("Couldn't parse this url: " + url);
	}

	if (StringUtils.isNotBlank(host)
		&& !user.getDomainsVisited().contains(host)) {
	    // Store the host that was visited
	    if (log.isDebugEnabled()) {
		log.debug("Adding domain to list: " + host);
	    }

	    user.addDomainsVisited(host);
	    userService.updateUser(user, false, source, user.getEmail());
	} else if (StringUtils.isBlank(host)) {
	    log.warn("url was malformed: " + url);
	}
    }
}
