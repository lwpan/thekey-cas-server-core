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
 * @author ken
 *
 */
public final class CasAuthenticationResponse implements AuthenticationClientResponse {

	protected static final Log log = LogFactory.getLog(CasClientImpl.class);
	
	private boolean authenticated=false;
	private Map <String,Cookie> cookies;
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
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
	 * @param isError the isError to set
	 */
	public void setError(boolean isError) {
		this.isError = isError;
	}

		
	/**
	 * Creates a somewhat populated response based on the parms in the request
	 * @param a_req
	 */
	public CasAuthenticationResponse(CasAuthenticationRequest a_req) {
		this.principal = a_req.getPrincipal();
		this.cookies = a_req.getCookies();
	}

	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public boolean isError()
	{
		return this.isError;
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

	public String getCASTGCValue(){
		return cookies.containsKey(Constants.CAS_TGC) ? cookies.get(Constants.CAS_TGC).getValue() : null;
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
