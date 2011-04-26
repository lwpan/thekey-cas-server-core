package org.ccci.gcx.idm.core.authentication.client;

/**
 * Wraps all of the information necessary to make an authentication request.
 * @author ken
 *
 */
public interface AuthenticationClientRequest {

	/**
	 * set the username
	 * @param a_username
	 */
	public void setPrincipal(String a_username);
	public String getPrincipal();
	
	/**
	 * set the password
	 * @param a_password
	 */
	public void setCredential(String a_password);
	public String getCredential();
	
}
