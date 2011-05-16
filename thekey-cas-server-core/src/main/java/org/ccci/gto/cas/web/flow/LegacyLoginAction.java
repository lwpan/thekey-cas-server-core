package org.ccci.gto.cas.web.flow;

import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.config.ServerConfigList;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;

@Deprecated
public class LegacyLoginAction {
    @NotNull
    private ServerConfigList redlist;

    // Request parameters to look for in the request
    private static final String PARAMETER_USERNAME = "username";
    private static final String PARAMETER_PASSWORD = "password";

    public boolean isAutomatedLogin(final RequestContext context,
	    final Service service, final UsernamePasswordCredentials credentials) {
	// only allow automated login for services in the redlist
	if (service != null && this.redlist.inList(service.getId())) {
	    // check for a username and password in the request
	    final ParameterMap params = context.getRequestParameters();
	    final String userName = params.get(PARAMETER_USERNAME);
	    final String password = params.get(PARAMETER_PASSWORD);

	    // only attempt automated login when a username and password is
	    // present in the request
	    if (userName != null && password != null) {
		// populate the credentials object
		credentials.setUsername(userName);
		credentials.setPassword(password);

		// return that an automated login should be attempted
		return true;
	    }
	}

	// default to not supporting automated login
	return false;
    }

    /**
     * @param redlist
     *            the redlist to set
     */
    public void setRedlist(final ServerConfigList redlist) {
	this.redlist = redlist;
    }
}
