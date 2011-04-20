package org.ccci.gcx.idm.core.service;
import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientRequest;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientResponse;


/**
 * <b>AuthenticationService</b> provides authentication of credentials against an SSO server.
 * some of the services are pretty CAS protocol specific so if we do end up
 * moving to another SSO protocol/service you'd probably want to abstract this out a bit more.
 * 
 * @author Ken Burcham
 *
 */
public interface AuthenticationService {
	
	/**
	 * handles a straight up login request
	 * @param a_req
	 * @return AuthenticationClientReponse populated with results of the processed request 
	 * You can use response.isAuthenticated indication success or failure
	 * @throws AuthenticationException in the event something goes wrong.
	 */
	public AuthenticationClientResponse handleLoginRequest(AuthenticationClientRequest a_req)
		throws AuthenticationException;
	
	/**
	 * handles an SSO request - if the user is attempting to send some credential (for CAS a TGC)
	 * in order to verify their identity without logging in again.
	 * @param a_req
	 * @return
	 * @throws AuthenticationException
	 */
	public AuthenticationClientResponse handleSSORequest(AuthenticationClientRequest a_req)
		throws AuthenticationException;

	/**
	 * handles a logout request
	 * @param a_req
	 * @throws AuthenticationException
	 */
	public void handleLogoutRequest(AuthenticationClientRequest a_req)
		throws AuthenticationException;

}
