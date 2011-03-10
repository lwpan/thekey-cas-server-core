package org.ccci.gcx.idm.core.authentication.client.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.ccci.gcx.idm.core.AuthenticationException;
import org.ccci.gcx.idm.core.Constants;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClient;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientRequest;
import org.ccci.gcx.idm.core.authentication.client.AuthenticationClientResponse;

/**
 * <b>CasClientImpl</b> Provides AuthenticationClient services via direct
 * interaction with a CAS server.
 * 
 * NOTE: Expects a list of valid CAS servers to be provided via Dependency
 * Injection from Spring.
 * 
 * @author Ken Burcham, Daniel Frett
 */
public class CasClientImpl implements AuthenticationClient {
    protected static final Log log = LogFactory.getLog(CasClientImpl.class);
    private List<String> casServerPool;
    private HttpClient httpClient;
	private HostConfiguration hc = new HostConfiguration();
	private HttpConnectionManager hcm = new MultiThreadedHttpConnectionManager();
	private String proxyUrl  = null; 
	private int    proxyPort = Constants.DEFAULTPROXY; //default
	private String cookieDomain = Constants.CAS_DEFAULTCOOKIEDOMAIN;

    /**
     * @return the httpClient
     */
    public HttpClient getHttpClient() {
	// Create HTTP client if client doesn't exist yet
	if (this.httpClient == null) {
	    final SyncBasicHttpParams params = new SyncBasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	    HttpProtocolParams.setUseExpectContinue(params, true);
	    final SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http", 80, PlainSocketFactory
		    .getSocketFactory()));
	    registry.register(new Scheme("https", 443, SSLSocketFactory
		    .getSocketFactory()));
	    final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
		    registry);
	    cm.setDefaultMaxPerRoute(100);
	    cm.setMaxTotal(100);
	    final DefaultHttpClient client = new DefaultHttpClient(cm, params);

	    // synchronize this code to prevent race condition of paving over an
	    // HttpClient set by another thread while this method was executing
	    synchronized (this) {
		// Make sure we still don't have an HttpClient before setting
		// one
		if (this.httpClient == null) {
		    this.setHttpClient(client);
		}
	    }
	}

	return this.httpClient;
    }

    /**
     * @param httpClient
     *            the HttpClient object to use
     */
    public synchronized void setHttpClient(final HttpClient httpClient) {
	this.httpClient = httpClient;
    }

	/**
	 * CasServerPool - Spring DI provided list of CAS server hosts that we can authenticate against.
	 * Strings should be in simple host format "cas1.mygcx.org/internal" (without protocol but with the target)
	 * 
	 * @param a_list
	 */
	public void setCasServerPool(List<String> a_list){
		casServerPool = a_list;
	}

    /**
     * This is method is deprecated because the max cookie age time was never
     * actually observed
     * 
     * @param a_seconds
     */
    @Deprecated
    public void setCookieMaxAge(int a_seconds) {
	log.error("Setting deprecated cookieMaxAge value");
    }

	private void configureProxy()
	{
		if(StringUtils.isNotBlank(proxyUrl))
		{
			if(log.isDebugEnabled()) log.debug("PROXY is configured. Using: "+proxyUrl);
			hc.setProxy(proxyUrl, proxyPort);
		}
		else
			if(log.isDebugEnabled()) log.debug("NO PROXY configured. ");
	}
	
	/**
	 * a_url should be like: proxy.ccci.org (not http://proxy.ccci.org).
	 * If a proxy isn't set then the HttpClient will use a direct connection.
	 * @param a_url
	 */
	public void setProxyUrl(String a_url)
	{
		log.debug("SETTING PROXY: "+a_url);
		proxyUrl = a_url;
		configureProxy();
	}
	
	/**
	 * port should be 80 or 8080, etc.
	 * @param a_port
	 */
	public void setProxyPort(int a_port)
	{
		proxyPort = a_port;
	}
	
	/**
	 * use a dummy ssl certificate?
	 *
	 */
	public void setUseDummySSLCertificate(boolean a_val)
	{
		if(log.isDebugEnabled()) log.debug("DummySSLCertificate = "+a_val);
	if (a_val) {
			log.warn("Using Dummy SSL Certificate.");
			Protocol.registerProtocol("https", 
			new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
		}
	}
	
	/**
	 * getCasServer - returns a valid, pinged CasServer.
	 * @return
	 */
	//TODO: this is a stub but may be ok if we only use one loadbalanced uri...
	private String getCasServer(){
		return Constants.DEFAULTCASSERVICEPROTOCOL+getCasServerFromPool();
	}
	
	private String getCasServerFromPool()
	{
		return casServerPool.get(0);
	}

	

	/**
	 * validateClientRequest - ensure we have a CasAuthenticationRequest and return it casted
	 * @param a_req
	 * @return
	 * @throws AuthenticationException
	 */
	private CasAuthenticationRequest validateClientRequest(AuthenticationClientRequest a_req)
		throws AuthenticationException
	{
		//ensure we have a proper subclass for a cas request.
		if(!CasAuthenticationRequest.class.isInstance(a_req))
			throw new AuthenticationException("CasClientImpl.handleAuthenticationRequest must be called with an instance of CasAuthenticationRequest.");
		
		return (CasAuthenticationRequest) a_req;
		
	}
	
	/**
	 * processes a service validation request by wrapping a call to CAS.
	 * @param a_req_nc
	 * @return
	 * @throws AuthenticationException
	 */
	public AuthenticationClientResponse processServiceValidationRequest(
			AuthenticationClientRequest a_req_nc) throws AuthenticationException 
	{
			return processValidationRequest(a_req_nc,Constants.SERVICE_VALIDATE_URL);
	}

	
	/**
	 * processes a proxy validation request by wrapping a call to CAS.
	 * @param a_req_nc
	 * @return
	 * @throws AuthenticationException
	 */
	public AuthenticationClientResponse processProxyValidationRequest(
			AuthenticationClientRequest a_req_nc) throws AuthenticationException 
	{
		return processValidationRequest(a_req_nc,Constants.PROXY_VALIDATE_URL);
	}
	
	/**
	 * Handle a proxy request
	 */
	public AuthenticationClientResponse processProxyRequest(AuthenticationClientRequest a_req_nc)
		throws AuthenticationException
	{
		if(log.isDebugEnabled()) log.debug("proxy request.");
		
		CasAuthenticationRequest a_req = validateClientRequest(a_req_nc);
		
		String casServer = getCasServer();
		
		CasAuthenticationResponse casresponse = new CasAuthenticationResponse(a_req);
		casresponse.setAuthenticated(false); //we're not authenticating a user, just checking the ticket.
		casresponse.setService(a_req.getService());
		casresponse.setPgtUrl(a_req.getPgtUrl());
		String ticket = a_req.getTicket();

		//we're not going to fail on empty ticket or service because cas2 protocol says
		//  we should give a specific error. we'll just pass to cas and get back its error.
		
		try
		{
	    org.apache.commons.httpclient.HttpClient client = getOldHttpClient();
			
			URI casuri  = new URI(casServer + Constants.PROXY_TICKET_URL);

			UTF8GetMethod authget = new UTF8GetMethod(casuri.toString());
			authget.setFollowRedirects(false);
			authget.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF8");  

			
			//set querystring
			NameValuePair n_service = new NameValuePair(Constants.CAS_PROXY_TARGETSERVICE_PARAM, a_req.getService());
			NameValuePair n_ticket  = new NameValuePair(Constants.CAS_PROXY_PGT_PARAM, ticket);
			
			NameValuePair[] getParams;
			
			/*
			StringBuffer querystring=new StringBuffer();
			querystring.append(Constants.CAS_PROXY_TARGETSERVICE_PARAM).append(a_req.getService());
			querystring.append(Constants.SYMBOL_AMP).append( Constants.CAS_PROXY_PGT).append(ticket);
			*/

			getParams = new NameValuePair[] {n_service, n_ticket};
			
			if(log.isDebugEnabled()) log.debug("HttpClient trying: "+ casuri.toString());
			if(log.isDebugEnabled()) log.debug("  with querystring: "+authget.getQueryString());
			
			authget.setQueryString(getParams);
			
			//execute
			client.executeMethod(authget);
			
	        int statusCode = authget.getStatusCode();
			if(statusCode == HttpStatus.SC_OK)
	        {
	        	if(log.isDebugEnabled())log.debug("Received OK response from CAS");
	        }else
	        {
	        	if(log.isDebugEnabled())log.debug("Received NOT_OK response from CAS");
		casresponse.setError(Constants.ERROR_VALIDATIONFAILED);
	        }
			
			//either way we return the response from cas.
			if(log.isDebugEnabled())log.debug(authget.getResponseBodyAsString());
            casresponse.setContent(authget.getResponseBodyAsString());
	           
	        authget.releaseConnection();
	        
		}
	    catch (Exception e)
		{
			log.error("An exception occurred. ", e);
			throw new AuthenticationException ("Failed to retrieve proxy because an exception occurred: "+e.getMessage());
		}
		
		return casresponse;	
	
	}
	
	/**
	 * Processes a service/proxy validation request by passing it along to CAS.
	 * @param a_req, target to direct request.
	 * @return
	 */
	private AuthenticationClientResponse processValidationRequest(
			AuthenticationClientRequest a_req_nc, String target) throws AuthenticationException 
	{
		if(log.isDebugEnabled()) log.debug("service or proxy Validation request.");
	
		CasAuthenticationRequest a_req = validateClientRequest(a_req_nc);
		
		String casServer = getCasServer();
		
		CasAuthenticationResponse casresponse = new CasAuthenticationResponse(a_req);
		casresponse.setAuthenticated(false); //we're not authenticating a user, just checking the ticket.
		casresponse.setService(a_req.getService());
		casresponse.setPgtUrl(a_req.getPgtUrl());
		String ticket = a_req.getTicket();

		//we're not going to fail on empty ticket or service because cas2 protocol says
		//  we should give a specific error. we'll just pass to cas and get back its error.
		
		try
		{
		    org.apache.commons.httpclient.HttpClient client = getOldHttpClient();
			
			URI casuri  = new URI(casServer + target);

			UTF8GetMethod authget = new UTF8GetMethod(casuri.toString());
			authget.setFollowRedirects(false);
			
			//set querystring
			NameValuePair n_service = new NameValuePair(Constants.CAS_SERVICE, a_req.getService());
			NameValuePair n_ticket  = new NameValuePair(Constants.CAS_TICKET, ticket);
			
			NameValuePair[] getParams;
			
			if(StringUtils.isNotEmpty(a_req.getPgtUrl()))
			{
				NameValuePair n_pgt     = new NameValuePair(Constants.CAS_PGT, a_req.getPgtUrl());
				getParams = new NameValuePair[] {n_service, n_ticket, n_pgt};
			}
			else
			{
				getParams = new NameValuePair[] {n_service, n_ticket};
			}
			
			authget.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF8");  
			authget.setQueryString(getParams);
			
			if(log.isDebugEnabled()) log.debug("HttpClient trying: "+ casuri.toString());
			if(log.isDebugEnabled()) log.debug("  with querystring: "+authget.getQueryString());
			
			//execute
			client.executeMethod(authget);
			
	        int statusCode = authget.getStatusCode();
			if(statusCode == HttpStatus.SC_OK)
	        {
	        	if(log.isDebugEnabled())log.debug("Received OK response from CAS");
	        }else
	        {
	        	if(log.isDebugEnabled())log.debug("Received NOT_OK response from CAS");
		casresponse.setError(Constants.ERROR_VALIDATIONFAILED);
	        }
			
			//return the cas response either way (success or failure)
        	if(log.isDebugEnabled())log.debug(authget.getResponseBodyAsString());
            casresponse.setContent(authget.getResponseBodyAsString());	           
	        
            authget.releaseConnection();
		}
	    catch (Exception e)
		{
			log.error("An exception occurred. ", e);
			throw new AuthenticationException ("Failed to validate service because an exception occurred: "+e.getMessage());
		}
		
		return casresponse;
	}

	/**
	 * Hits CAS with the incoming request to see if they are already logged in.
	 * 
	 * @param a_req
	 * @return
	 * @throws AuthenticationException
	 */
	public AuthenticationClientResponse processSSORequest(AuthenticationClientRequest a_req_nc) throws AuthenticationException
	{

		if(log.isDebugEnabled()) log.debug("SSO request");
		
		CasAuthenticationRequest a_req = validateClientRequest(a_req_nc);
		
		String casServer = getCasServer();

		CasAuthenticationResponse casresponse = new CasAuthenticationResponse(a_req);
		casresponse.setAuthenticated(false);
		casresponse.setService(a_req.getService());

		if(StringUtils.isEmpty(a_req.getCASTGCValue()))
		{
			if(log.isDebugEnabled()) log.debug("No CASTGC. Returning.");
	    casresponse.setError(Constants.ERROR_VALIDATIONFAILED);
			return casresponse;
		}
		
		try
		{
			URI casuri  = new URI(casServer + Constants.LOGIN_URL);// + '?' + Constants.URL_BEGIN + a_req.getService());
						
			Cookie castgc = new Cookie(this.cookieDomain,Constants.CAS_TGC,a_req.getCASTGCValue(), Constants.CAS_COOKIEPATH,null,false);
			
			if(log.isDebugEnabled()) log.debug("Additive CASTGC COOKIE: "+castgc.toString());
			
			org.apache.commons.httpclient.HttpClient client = getOldHttpClient();
			
			//add cookie to our httpclient.
			client.getState().addCookie(castgc);
					
			UTF8GetMethod authget = new UTF8GetMethod(casuri.toString());
			authget.setFollowRedirects(false);
			authget.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF8"); 
			
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			
			pairs.add( new NameValuePair(Constants.CAS_SERVICE, a_req.getService()) );
			
			if(StringUtils.isNotEmpty(a_req.getLogoutCallback()))
				pairs.add( new NameValuePair(Constants.CAS_LOGOUTCALLBACK, a_req.getLogoutCallback()));
			
			NameValuePair[] params = pairs.toArray(new NameValuePair[]{});

			
			authget.setQueryString(params);
			
			//authget.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		
			if(log.isDebugEnabled()) log.debug("HttpClient trying: "+ casuri.toString());
			client.executeMethod(authget);
			
	        int statusCode = authget.getStatusCode();

			//NOTE: if statusCode=SC_OK then CAS returned a page instead of a redirect.
	        // that means they are NOT authenticated.
			if(statusCode == HttpStatus.SC_OK)
	        {
	        	log.warn("CAS didn't like the TGC so we'll return a NOT authenticated because they likely expired.");
	        	casresponse.setAuthenticated(false);
	        }
	
	        //String response = authpost.getResponseBodyAsString();
	        
	        Header header = authget.getResponseHeader(Constants.CAS_LOCATIONHEADER);
	        if (header != null) 
	        {
	        	casresponse.setLocation(header.getValue());

	            casresponse.setAuthenticated(true);
	            casresponse.addCookies(client.getState().getCookies());

	            if(log.isInfoEnabled()) log.info("Successfully retrieved location from CAS using TGC: "+casresponse.getLocation() );
	            if(log.isDebugEnabled()) log.debug("Successfully retrieved cookies: "+casresponse.getCookies().size() );
	            
	        }
	        else
	        {
	        	log.warn("Didn't get a header back from CAS. Strange.");
	        }
	           
	        authget.releaseConnection();
	        
		}
	    catch (Exception e)
		{
			log.error("An exception occurred. ", e);
			throw new AuthenticationException ("Failed to authenticate because an exception occurred: "+e.getMessage());
		}
	
		
		return casresponse;
	}

	/**
	 * All regular login authentication will be processed here. A service parm is required.
	 * @param a_req
	 * @return
	 * @throws AuthenticationException
	 */
	public CasAuthenticationResponse processLoginRequest(AuthenticationClientRequest a_req_nc) throws AuthenticationException
	{
		if(log.isDebugEnabled()) log.debug("Attempting to process a login request");
		
		CasAuthenticationRequest a_req = validateClientRequest(a_req_nc);
		
		//ensure we have a service for this request. cas won't give us a redirect unless we do.
		if(StringUtils.isEmpty(a_req.getService()))
			throw new AuthenticationException("Service cannot be null for processing a login!");

		//begin our response preparations
		CasAuthenticationResponse casresponse = new CasAuthenticationResponse(a_req);
		casresponse.setService(a_req.getService());
    	casresponse.setAuthenticated(false);

    	//now check to make sure we have credentials and this is a login attempt.
		if(StringUtils.isEmpty(a_req.getPrincipal()) || StringUtils.isEmpty(a_req.getCredential()))
		{
	    casresponse.setError(Constants.ERROR_NOCREDENTIALORPRINCIPAL);
			if(log.isDebugEnabled()) log.debug("Principal or Credential is empty.  User cannot be authenticated");
			return casresponse;
		}
			
		String casServer = getCasServer();
		
		try
		{
			URI casuri  = new URI(casServer + Constants.LOGIN_URL);
			
			if(log.isDebugEnabled()) log.debug("HttpClient trying: "+casuri.toString());    
			UTF8PostMethod authpost = new UTF8PostMethod(casuri.toString());
			authpost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF8");  

			org.apache.commons.httpclient.HttpClient client = getOldHttpClient();
			
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			
			pairs.add( new NameValuePair(Constants.CAS_SERVICE, a_req.getService()) );
			pairs.add( new NameValuePair(Constants.CAS_EVENTID,"submit" ));
			pairs.add( new NameValuePair(Constants.CAS_USERNAME, a_req.getPrincipal()));
			pairs.add( new NameValuePair(Constants.CAS_PASSWORD,a_req.getCredential() ));
			pairs.add( new NameValuePair(Constants.CAS_LOGINTICKET,getLoginTicket(client,a_req,casServer) ));
			pairs.add( new NameValuePair(Constants.CAS_GATEWAY,"true" ));
			
			if(StringUtils.isNotEmpty(a_req.getLogoutCallback()))
					pairs.add( new NameValuePair(Constants.CAS_LOGOUTCALLBACK, a_req.getLogoutCallback()));
			
			NameValuePair[] postParams = pairs.toArray(new NameValuePair[]{});
			
			authpost.setRequestBody(postParams);
			
			client.executeMethod(authpost);
	        
	        int statusCode = authpost.getStatusCode();
	        
	        //NOTE: if SC_OK then that is BAD because we didn't get a redirect (which is GOOD).
	        if(statusCode == HttpStatus.SC_OK)
	        {
	        	log.error("Authentication failure: After posting the CAS login we received another login. Login failed.");
	        	casresponse.setAuthenticated(false);
	        }
	        else
	        {
	        	//we authenticated successfully so get the location.
		        Header header = authpost.getResponseHeader(Constants.CAS_LOCATIONHEADER);
		        if (header != null) 
		        {
		            casresponse.addCookies(client.getState().getCookies());
		            
		            //if we have a CAS cookie then we're authenticated.
		            if(StringUtils.isNotEmpty(casresponse.getCASTGCValue()))
		            {
		            		casresponse.setAuthenticated(true);
		            }
		            else
		            {
		            	log.error("Strange: ****  No cookie after successful redirect from CAS... ***");
			casresponse.setError(Constants.ERROR_NOCOOKIEAFTERAUTH);
		            }
		            
		            
		        	casresponse.setLocation(header.getValue());
		            
		            if(log.isInfoEnabled()) log.info("Successfully logged in: "+a_req.getPrincipal()+" for "+casresponse.getLocation() );
	
		        }
		        else
		        {
		        	throw new Exception("Didn't get a header back from CAS.");
		        }

	        }
	        authpost.releaseConnection();
	        
		}
	    catch (Exception e)
		{
			log.error("An exception occurred. ", e);
			throw new AuthenticationException (e.getMessage());
		}
		return casresponse;
	}


	
	/**
	 * getLoginTicket - connects to CAS server and retrieves a login page and
	 * finds and returns the loginTicket.
	 * Throws an exception on any other failure or error condition.
	 * 
	 * @param a_server
	 * @param a_username
	 * @param a_password
	 * @param a_service
	 * @return login ticket
	 * 
	 * @throws AuthenticationException
	 */
	private String getLoginTicket(org.apache.commons.httpclient.HttpClient client, CasAuthenticationRequest a_req, String a_server)
		throws AuthenticationException
	{
		String loginTicket = null;
		
		String a_service = a_req.getService();
		
		try
		{
			log.debug("Trying to get a CAS loginTicket using Host = "+a_server+Constants.LOGIN_URL);
			
			UTF8PostMethod authget = new UTF8PostMethod(a_server+Constants.LOGIN_URL);
			authget.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF8");  
	        authget.setParameter(Constants.CAS_SERVICE,a_service);
	        
	        int statusCode = client.executeMethod(authget);
	        
	        if(statusCode != HttpStatus.SC_OK)
	        {
	        	log.error("Unexpected error: Something went wrong trying to fetch the CAS Login page: "+authget.getStatusLine());
	        	throw new AuthenticationException("Failure to connect to CAS Authentication Service. This is most likely a configuration or server error.");
	        }
	        
	        log.debug("Received an OK login page. Lets parse for the login ticket.");
	        
	        String response = authget.getResponseBodyAsString();
	        authget.releaseConnection();
	        
	        //use regex to pull out the login ticket 
	        Pattern ltPattern     = Pattern.compile(Constants.LT_REGEX);
	        Matcher ltMatcher     = ltPattern.matcher(response);
	        
	        if(!ltMatcher.find())
        	{
	        	log.error("Unexpected error: Regex failed to find a loginTicket in the CAS Login page");
	        	throw new AuthenticationException("Failure to parse CAS Authentication Service. This is most likely a configuration or server error.");
	        }
	        	
	        loginTicket = ltMatcher.group(1);
	        log.debug("Successfully found a loginTicket: "+loginTicket);
		}
	    catch (Exception e)
		{
			log.error("An exception occurred: "+e.getMessage());
			throw new AuthenticationException ("Failed to authenticate because an exception occurred: "+e.getMessage());
		}

	    return loginTicket;
	}

	/**
	 * Calls CAS with a logout request. Returns nothing if success, throws an excpetion if there was a problem.
	 * @param a_req
	 * @throws AuthenticationException
	 */
	public void processLogoutRequest(AuthenticationClientRequest a_req_nc)
			throws AuthenticationException {

		if(log.isDebugEnabled()) log.debug("Attempting to LOGOUT");
		
		CasAuthenticationRequest a_req = validateClientRequest(a_req_nc);

		String casServer 		= getCasServer();
		
		try
		{			
			URI casuri  = new URI(casServer + Constants.LOGOUT_URL);
						
			if(StringUtils.isEmpty(a_req.getCASTGCValue()))
			{
				log.warn("Logout called with no CASTGC. Apparently they're already logged out.");
				return;
			}
			
			Cookie castgc = new Cookie(this.cookieDomain,Constants.CAS_TGC,a_req.getCASTGCValue(), Constants.CAS_COOKIEPATH,null,false);
		
			if(log.isDebugEnabled()) log.debug("Additive CASTGC COOKIE: "+castgc.toString());
			
			org.apache.commons.httpclient.HttpClient client = getOldHttpClient();
			
			//add cookie to our httpclient.
			client.getState().addCookie(castgc);
			
					
			UTF8GetMethod authget = new UTF8GetMethod(casuri.toString());
			authget.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF8"); 
			authget.setFollowRedirects(false);

			if(log.isDebugEnabled()) log.debug("HttpClient trying: "+ casuri.toString());
			client.executeMethod(authget);
			
	        int statusCode = authget.getStatusCode();
			if(statusCode == HttpStatus.SC_OK)
	        {
	        	if(log.isDebugEnabled())log.debug("CAS said OK, assuming our logout is successful.");
	        }
	           
	        authget.releaseConnection();
	        
		}
	    catch (Exception e)
		{
			log.error("An exception occurred. ", e);
			throw new AuthenticationException ("Logout exception from CAS: "+e.getMessage());
		}
	
		
		
	}


    /**
     * prepares and returns an httpclient ready for use.
     * @return
     */
    private org.apache.commons.httpclient.HttpClient getOldHttpClient() {
	org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
	client.setHostConfiguration(hc);
	client.setHttpConnectionManager(hcm);
	return client;
    }

	public void setCookieDomain(String a_cookieDomain) {
		if(log.isDebugEnabled()) log.debug("CookieDomain changed by config to: "+a_cookieDomain);
		this.cookieDomain = a_cookieDomain;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public static class UTF8PostMethod extends PostMethod {
		public UTF8PostMethod(String url) {
			super(url);
		}

		@Override
		public String getRequestCharSet() {
			// return super.getRequestCharSet();
			return "UTF-8";
		}
	}
	
	
	public static class UTF8GetMethod extends GetMethod {
		public UTF8GetMethod(String url) {
			super(url);
		}

		@Override
		public String getRequestCharSet() {
			// return super.getRequestCharSet();
			return "UTF-8";
		}
	}	

}


