package org.ccci.gcx.idm.core.authentication.client.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientRequest;

public class CasAuthenticationRequest implements AuthenticationClientRequest {
    protected static final Log log = LogFactory
	    .getLog(CasAuthenticationRequest.class);

	private String credential;
	private String principal;
    private final HashMap<String, Cookie> cookies;
	private String requestURI;
	private String service;
	private String ticket;
	private String pgtUrl;
	private String logoutCallback;

    public CasAuthenticationRequest() {
	this.cookies = new HashMap<String, Cookie>();
    }

	/**
	 * @return the requestURI
	 */
	public String getRequestURI() {
		return requestURI;
	}
	/**
	 * @param requestURI the requestURI to set
	 */
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	/**
	 * @return the credential
	 */
	public String getCredential() {
		return credential;
	}
	/**
	 * @param credential the credential to set
	 */
	public void setCredential(String credential) {
		this.credential = credential;
	}
	/**
	 * @return the principal
	 */
	public String getPrincipal() {
		return principal;
	}
	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(String principal) {
		this.principal = principal;
	}

    public void addCookie(Cookie cookie) {
	this.cookies.put(cookie.getName(), cookie);
	if (log.isDebugEnabled()) {
	    log.debug("COOKIE ADDED TO REQUEST: " + cookie);
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
    public Map<String, Cookie> getCookies() {
	return new HashMap<String, Cookie>(this.cookies);
    }

    /**
     * @param cookies
     *            the cookies to set
     */
    public void setCookies(Map<String, Cookie> cookies) {
	this.cookies.clear();
	this.cookies.putAll(cookies);
    }

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	public String getLogoutCallback() {
		return logoutCallback;
	}
	public void setLogoutCallback(String logoutCallback) {
		this.logoutCallback = logoutCallback;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	public String getCASTGCValue(){
		return cookies.containsKey(Constants.CAS_TGC) ? cookies.get(Constants.CAS_TGC).getValue() : null;
	}
	/**
	 * @return the ticket
	 */
	public String getTicket() {
		return ticket;
	}
	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(String ticket) {
		this.ticket = ticket;
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
