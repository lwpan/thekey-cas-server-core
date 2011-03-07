package org.ccci.gcx.idm.core.authentication.client.impl;

import java.util.List;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClient;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientRequest;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientResponse;

public class DummyClientImpl implements AuthenticationClient {

	protected static final Log log = LogFactory.getLog(DummyClientImpl.class);
	
	/**
	 * CasServerPool - Spring DI provided list of CAS server hosts that we can authenticate against.
	 * Strings should be in simple host format "cas1.mygcx.org/internal" (without protocol but with the target)
	 * 
	 * @param a_list
	 */
	public void setCasServerPool(List<String> a_list){
	}
	
	/**
	 * the number of seconds for which this cookie is valid. 
	 * Expected to be a non-negative number. -1 signifies that the cookie should never expire. 
	 * @param a_seconds
	 */
	public void setCookieMaxAge(int a_seconds)
	{
	}
	
	
	/**
	 * a_url should be like: proxy.ccci.org (not http://proxy.ccci.org).
	 * If a proxy isn't set then the HttpClient will use a direct connection.
	 * @param a_url
	 */
	public void setProxyUrl(String a_url)
	{
	}
	
	/**
	 * port should be 80 or 8080, etc.
	 * @param a_port
	 */
	public void setProxyPort(int a_port)
	{
	}
	
	/**
	 * use a dummy ssl certificate?
	 *
	 */
	public void setUseDummySSLCertificate(boolean a_val)
	{
	}
	
	
	
	
	public AuthenticationClientResponse processLoginRequest(
			AuthenticationClientRequest a_req) throws AuthenticationException {
		if(log.isDebugEnabled()) log.debug("processLoginRequest - Dummy!");
		return fakeResponse(a_req);
	}

	public AuthenticationClientResponse processSSORequest(
			AuthenticationClientRequest a_req) throws AuthenticationException {
		if(log.isDebugEnabled()) log.debug("processSSORequest - Dummy!");
		return fakeResponse(a_req);
	}

	public AuthenticationClientResponse processServiceValidationRequest(
			AuthenticationClientRequest a_req) throws AuthenticationException {
		if(log.isDebugEnabled()) log.debug("processServiceValidationRequest - Dummy!");
		return fakeResponse(a_req);
	}

	public AuthenticationClientResponse processProxyValidationRequest(
			AuthenticationClientRequest a_req) throws AuthenticationException {
		if(log.isDebugEnabled()) log.debug("processProxyValidationRequest - Dummy!");

		return fakeResponse(a_req);
	}

	public AuthenticationClientResponse processProxyRequest(
			AuthenticationClientRequest a_req) throws AuthenticationException {
		if(log.isDebugEnabled()) log.debug("processProxyRequest - Dummy!");

		return fakeResponse(a_req);
	}

	public void processLogoutRequest(AuthenticationClientRequest a_req)
			throws AuthenticationException {
		
		if(log.isDebugEnabled()) log.debug("processLogoutRequest - Dummy!");

	}
	
	/**
	 * Simply returns a response build from the request.
	 * @param a_req
	 * @return
	 */
	private AuthenticationClientResponse fakeResponse(AuthenticationClientRequest the_req)
	{
		CasAuthenticationRequest a_req = (CasAuthenticationRequest) the_req;
		CasAuthenticationResponse casresponse =  new CasAuthenticationResponse(a_req);
		casresponse.setAuthenticated(true); //we're not authenticating a user, just checking the ticket.
		casresponse.setService(a_req.getService());
		casresponse.setPgtUrl(a_req.getPgtUrl());
		log.debug("Service == " + casresponse.getService());
		return casresponse;
	}

}
