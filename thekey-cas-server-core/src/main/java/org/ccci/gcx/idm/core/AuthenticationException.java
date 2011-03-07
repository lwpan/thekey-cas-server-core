package org.ccci.gcx.idm.core;

/**
 * provides an exception class for authentication errors.
 * @author ken
 *
 */

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(String a_string) {
		super(a_string);
	}

}
