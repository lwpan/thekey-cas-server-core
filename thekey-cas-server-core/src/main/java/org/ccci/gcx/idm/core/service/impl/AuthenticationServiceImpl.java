package org.ccci.gcx.idm.core.service.impl;

import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClient;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientRequest;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientResponse;
import org.ccci.gcx.idm.core.service.AuthenticationService;

/**
 * <b>AuthenticationServiceImpl</b> Provides AuthenticationService implementation to 
 * interact with Authentication Clients.
 * 
 * @author Ken Burcham
 *
 */
public class AuthenticationServiceImpl implements AuthenticationService {

	private AuthenticationClient client;
	
	public void setAuthenticationClient(AuthenticationClient a_client)
	{
		client = a_client;
	}


	public void handleLogoutRequest(AuthenticationClientRequest a_req)
		throws AuthenticationException {
		client.processLogoutRequest(a_req);
	}

	public AuthenticationClientResponse handleLoginRequest(
			AuthenticationClientRequest a_req) throws AuthenticationException {
		return client.processLoginRequest(a_req);

	}

	public AuthenticationClientResponse handleSSORequest(
			AuthenticationClientRequest a_req) throws AuthenticationException {
		return client.processSSORequest(a_req);

	}
}
