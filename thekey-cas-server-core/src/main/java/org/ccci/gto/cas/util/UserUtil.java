package org.ccci.gto.cas.util;

import java.net.MalformedURLException;
import java.net.URL;

import me.thekey.cas.service.UserManager;

import me.thekey.cas.service.UserNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.jasig.cas.authentication.principal.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public final class UserUtil {
    private static final Logger LOG = LoggerFactory.getLogger(UserUtil.class);

    /**
     * add the specified service to the list of services visited by the
     * specified user
     * 
     * @param userService
     * @param user
     * @param service
     *            the service being visited
     * @param source
     *            the identifier for the source of this addition
     */
    public static void addVisitedService(final UserManager userService, final GcxUser user, final Service service,
            final String source) {
	Assert.notNull(userService);
	Assert.notNull(user);
	Assert.notNull(service);

	try {
	    // extract the host from the Service if possible
	    final URL u = new URL(service.getId());
	    final String host = u.getHost();

	    if (StringUtils.isNotBlank(host)) {
		if (!user.getDomainsVisited().contains(host)) {
		    // make sure this domain wasn't already added
		    final GcxUser freshUser = userService.getFreshUser(user);
		    if (!freshUser.getDomainsVisited().contains(host)) {
			// Store the host that was visited
                        LOG.debug("Adding domain to list: {}", host);
			freshUser.addDomainsVisited(host);
                        userService.updateUser(freshUser);
		    }

		    // update the original user object with the latest domains
		    // visited list
		    user.setDomainsVisited(freshUser.getDomainsVisited());
		}
	    } else {
                LOG.warn("Service url was malformed: {}", service.getId());
	    }
	} catch (final MalformedURLException e) {
	    // log the error and then suppress it
            LOG.error("Couldn't parse this service: {}", service.getId(), e);
	} catch (final UserNotFoundException e) {
	    // log the error and then suppress it
            LOG.error("error updating visited services list", e);
	}
    }
}
