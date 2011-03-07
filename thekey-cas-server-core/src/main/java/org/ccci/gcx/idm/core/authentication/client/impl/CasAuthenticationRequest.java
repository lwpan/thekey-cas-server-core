package org.ccci.gcx.idm.core.authentication.client.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientRequest;

public class CasAuthenticationRequest implements AuthenticationClientRequest {

	private String credential;
	private String principal;
	private Map <String,Cookie>cookies;
	private String requestURI;
	private String service;
	private String ticket;
	private String pgtUrl;
	private String logoutCallback;
	
	protected static final Log log = LogFactory.getLog(CasClientImpl.class);

	
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
	/**
	 * @return the cookies
	 */
	public Map<String,Cookie> getCookies() {
		return cookies;
	}
	/**
	 * @param cookies the cookies to set
	 */
	public void setCookies(Map<String,Cookie> cookies) {
		this.cookies = cookies;
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
	

	public void addCookies(Cookie[] the_cookies) {
		if(cookies == null) cookies = new HashMap<String,Cookie>();
		if(the_cookies == null)
		{
			if(log.isDebugEnabled()) log.debug("the_cookies was null. skipping addCookies.");
			return;
		}
		for(int a = 0; a< the_cookies.length; a++)
		{
			Cookie c = the_cookies[a];
			this.cookies.put(c.getName(),c);
			if(log.isDebugEnabled()) 
				log.debug("COOKIE ADDED TO RESPONSE: "+c);
		}
		
		
	}
	
	public void addCookies(HashMap<String,Cookie> the_cookies)
	{
		if(the_cookies == null)
		{
			if(log.isDebugEnabled()) log.debug("the_cookies was null. skipping addCookies.");
			return;
		}
		cookies = the_cookies;
		if(log.isDebugEnabled()) log.debug("added cookies " + cookies.size());
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
