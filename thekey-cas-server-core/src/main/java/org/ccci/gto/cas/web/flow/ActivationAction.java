package org.ccci.gto.cas.web.flow;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.ccci.gto.cas.Constants;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;

public class ActivationAction {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // Request parameters to look for in the request
    private static final String PARAMETER_FLAG = Constants.PARAMETER_ACTIVATION_FLAG;
    private static final String PARAMETER_FLAGVALUE = Constants.PARAMETER_ACTIVATION_FLAGVALUE;
    private static final String PARAMETER_USERNAME = Constants.PARAMETER_ACTIVATION_USERNAME;
    private static final String PARAMETER_KEY = Constants.PARAMETER_ACTIVATION_KEY;

    @NotNull
    private GcxUserService gcxUserService;

    public boolean isActivation(final RequestContext context,
	    final UsernamePasswordCredentials credentials) {
	final ParameterMap params = context.getRequestParameters();

	// is activate=true set?
	final String activate = params.get(PARAMETER_FLAG);
	if (activate != null && activate.equals(PARAMETER_FLAGVALUE)) {
	    // is the specified user a transitional user
	    final String userName = params.get(PARAMETER_USERNAME);
	    final GcxUser user = gcxUserService
		    .findTransitionalUserByEmail(userName);
	    if (user != null) {
		// populate the credentials with the credentials being activated
		credentials.setUsername(userName);
		credentials.setPassword(params.get(PARAMETER_KEY));

		// return that this is an activation request
		return true;
	    }
	}

	// default to this not being an activation request
	return false;
    }

    public void setGcxUserService(GcxUserService userService) {
	this.gcxUserService = userService;
    }
}
