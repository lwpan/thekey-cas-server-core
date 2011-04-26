package org.ccci.gcx.idm.core.authentication.client;

/**
 * Collects all of the information necessary to return from the Authentication processing.
 * You'll probably want to add more if we abstract out a second kind of authentication client.
 * @author ken
 *
 */
public interface AuthenticationClientResponse {

	/**
	 * Were the credentials authentic?
	 * @return
	 */
	public boolean isAuthenticated();
	
	/**
	 * Returns the user authenticated (or not).
	 * @return
	 */
	public String getPrincipal();
	
	/**
	 * If there was an error;
	 * @return
	 */
	public boolean isError();


}
