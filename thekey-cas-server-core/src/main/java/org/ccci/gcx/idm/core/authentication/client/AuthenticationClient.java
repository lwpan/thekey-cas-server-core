package org.ccci.gcx.idm.core.authentication.client;

import org.ccci.gcx.idm.core.AuthenticationException;

/**
 * 
 * 
 * An implementor of AuthenticationClient will provide validation of username and password
 * credentials for a specific 3rd party service.  It'll do so by handling the request
 * as defined in an AuthenticationClientRequest bean and returning an AuthenticationClientResponse bean.
 *    
 * Also provides a logout service.
 * 
 * @author ken burcham
  */
public interface AuthenticationClient {
	

	/**
	 * handles a straight up login request
	 * @param a_req
	 * @return AuthenticationClientReponse populated with results of the processed request 
	 * You can use response.isAuthenticated indication success or failure
	 * @throws AuthenticationException in the event something goes wrong.
	 */
	public AuthenticationClientResponse processLoginRequest(AuthenticationClientRequest a_req)
		throws AuthenticationException;
	
	/**
	 * handles an SSO request - if the user is attempting to send some credential (for CAS a TGC)
	 * in order to verify their identity without logging in again.
	 * @param a_req
	 * @return
	 * @throws AuthenticationException
	 */
	public AuthenticationClientResponse processSSORequest(AuthenticationClientRequest a_req)
		throws AuthenticationException;
}
