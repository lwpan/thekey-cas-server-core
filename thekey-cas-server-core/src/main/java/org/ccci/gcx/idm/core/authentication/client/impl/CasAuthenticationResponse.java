package org.ccci.gcx.idm.core.authentication.client.impl;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientResponse;

/**
 * Defines a specific response that a CAS server will return.
 * 
 * @author Ken Burcham, Daniel Frett
 */
public final class CasAuthenticationResponse implements AuthenticationClientResponse {
    protected static final Log log = LogFactory
	    .getLog(CasAuthenticationResponse.class);
	
	private boolean authenticated=false;
    private final HashMap<String, String> cookies;
	private Map <String,String>parameters;
	private String location;
	private boolean isError;
	private String errorCode;
	private String content;
	private String service;
	private String protocol;
	private String principal;
	private String pgtUrl;

	
	
    /**
     * This method will set an error code for this response
     * 
     * @param errorCode
     *            the error code to set for this response
     */
    public void setError(String errorCode) {
	this.isError = true;
	this.errorCode = errorCode;
    }

    public boolean isError() {
	return this.isError;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
	return this.errorCode;
    }

    /**
     * @param errorCode
     *            the errorCode to set
     */
    @Deprecated
    public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
    }

    /**
     * @param isError
     *            the isError to set
     */
    @Deprecated
    public void setError(boolean isError) {
	this.isError = isError;
    }

    /**
     * @return the content
     */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

    /**
     * Creates a somewhat populated response based on the parms in the request
     * 
     * @param a_req
     */
    public CasAuthenticationResponse(CasAuthenticationRequest a_req) {
	this.cookies = new HashMap<String, String>();
	this.setCookies(a_req.getCookies());
	this.principal = a_req.getPrincipal();
    }

	public boolean isAuthenticated() {
		return authenticated;
	}

    public void addCookie(Cookie cookie) {
	this.cookies.put(cookie.getName(), cookie.getValue());
	if (log.isDebugEnabled()) {
	    log.debug("COOKIE ADDED TO RESPONSE: " + cookie);
	}
    }

    public void addCookies(Cookie[] cookies) {
	if (cookies == null) {
	    log.debug("a null array of cookies was provided, don't do anything");
	}
	for (Cookie c : cookies) {
	    this.addCookie(c);
	}
    }

    /**
     * @return the cookies
     */
    public Map<String, String> getCookies() {
	return new HashMap<String, String>(this.cookies);
    }

    /**
     * @param cookies
     *            the cookies to set
     */
    public void setCookies(Map<String, String> cookies) {
	this.cookies.clear();
	this.cookies.putAll(cookies);
    }

    public void setCookie(final String name, final String value) {
	this.cookies.put(name, value);
	if (log.isDebugEnabled()) {
	    log.debug("Cookie set in response: " + name + " - " + value);
	}
    }

    public String getCASTGCValue() {
	return this.cookies.get(Constants.CAS_TGC);
    }

	/**
	 * @return the parameters
	 */
	public Map<String,String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String,String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @param authenticated the authenticated to set
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getPrincipal() {
		return principal;
	}
	
	public void setPrincipal(String a_usr)
	{
		principal = a_usr;
	}

	/**
	 * @return the pgtUrl
	 */
	public String getPgtUrl() {
		return pgtUrl;
	}

	/**
	 * @param pgtUrl the pgtUrl to set
	 */
	public void setPgtUrl(String pgtUrl) {
		this.pgtUrl = pgtUrl;
	}


}
