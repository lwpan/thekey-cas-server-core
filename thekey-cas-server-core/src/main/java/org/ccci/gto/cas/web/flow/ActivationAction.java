package org.ccci.gto.cas.web.flow;

import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_FLAG;
import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_FLAGVALUE;
import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_KEY;
import static org.ccci.gto.cas.Constants.PARAMETER_ACTIVATION_USERNAME;

import javax.validation.constraints.NotNull;

import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.core.service.GcxUserService;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;

public class ActivationAction {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @NotNull
    private GcxUserService gcxUserService;

    public boolean isActivation(final RequestContext context,
	    final UsernamePasswordCredentials credentials) {
	final ParameterMap params = context.getRequestParameters();

	// is activate=true set?
	final String activate = params.get(PARAMETER_ACTIVATION_FLAG);
	if (activate != null && activate.equals(PARAMETER_ACTIVATION_FLAGVALUE)) {
	    // find the specified user
	    final String userName = params.get(PARAMETER_ACTIVATION_USERNAME);
	    final GcxUser user = gcxUserService.findUserByEmail(userName);
	    // TODO: remove this code once all legacy Transitional users are
	    // migrated
	    @SuppressWarnings("deprecation")
	    final GcxUser legacyUser = gcxUserService
		    .findTransitionalUserByEmail(userName);

	    // is the specified user an unverified user
	    if ((user != null && !user.isVerified()) || legacyUser != null) {
		// populate the credentials with the credentials being activated
		credentials.setUsername(userName);
		credentials.setPassword(params.get(PARAMETER_ACTIVATION_KEY));

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
