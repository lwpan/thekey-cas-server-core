package org.ccci.gcx.idm.core.authentication.client.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
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
    private static enum Method {
	GET, POST
    };

    protected static final Log log = LogFactory.getLog(CasClientImpl.class);
    private static final Pattern ltPattern = Pattern
	    .compile(Constants.LT_REGEX);
    private List<String> casServerPool;
    private HttpClient httpClient;
	private HostConfiguration hc = new HostConfiguration();
	private HttpConnectionManager hcm = new MultiThreadedHttpConnectionManager();
    private String proxyUrl = null;
    private int proxyPort = Constants.DEFAULTPROXY;
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
     * url should be like: proxy.ccci.org (not http://proxy.ccci.org). If a
     * proxy isn't set then the HttpClient will use a direct connection.
     * 
     * @param url
     */
    public void setProxyUrl(final String url) {
	log.debug("SETTING PROXY: " + url);
	this.proxyUrl = url;
    }

    /**
     * port should be 80 or 8080, etc.
     * 
     * @param port
     */
    public void setProxyPort(final int port) {
	this.proxyPort = port;
    }

    private HttpHost getProxy() {
	if (StringUtils.isNotBlank(this.proxyUrl)) {
	    return new HttpHost(this.proxyUrl, this.proxyPort);
	}
	return null;
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
     * getLoginTicket - connects to CAS server and retrieves a login page and
     * finds and returns the loginTicket. Throws an exception on any other
     * failure or error condition.
     * 
     * @param casServer
     * @param req
     * @return login ticket
     * @throws AuthenticationException
     */
    private String getLoginTicket(final String casServer,
	    final CasAuthenticationRequest req) throws AuthenticationException {
	String lt = null;

	try {
	    // build HttpUriRequest object
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(Constants.CAS_SERVICE, req
		    .getService()));
	    HttpUriRequest request = this.buildRequest(Method.GET, new URI(
		    casServer + Constants.LOGIN_URL), params);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    HttpResponse response = this.getHttpClient().execute(request);

	    // Throw an error if a 200 OK response wasn't received
	    StatusLine status = response.getStatusLine();
	    if (status.getStatusCode() != HttpStatus.SC_OK) {
		log.error("Unexpected error: Something went wrong trying to fetch the CAS Login page: "
			+ status.toString());
		throw new AuthenticationException(
			"Failure to connect to CAS Authentication Service. This is most likely a configuration or server error.");
	    }

	    log.debug("Received an OK login page. Lets parse for the login ticket.");

	    // use regex to pull out the login ticket
	    Matcher ltMatcher = CasClientImpl.ltPattern.matcher(response
		    .getEntity().toString());

	    if (!ltMatcher.find()) {
		log.error("Unexpected error: Regex failed to find a loginTicket in the CAS Login page");
		throw new AuthenticationException(
			"Failure to parse CAS Authentication Service. This is most likely a configuration or server error.");
	    }

	    lt = ltMatcher.group(1);
	    log.debug("Successfully found a loginTicket: " + lt);
	} catch (AuthenticationException e) {
	    // re-throw any AuthenticationExceptions
	    throw (e);
	} catch (Exception e) {
	    log.error("An exception occurred: " + e.getMessage());
	    throw new AuthenticationException(
		    "failed to retrieve a login ticket", e);
	}

	return lt;
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
     * All regular login authentication will be processed here. A service
     * parameter is required.
     * 
     * @param a_req
     * @return
     * @throws AuthenticationException
     */
    public CasAuthenticationResponse processLoginRequest(
	    final AuthenticationClientRequest a_req_nc)
	    throws AuthenticationException {
	log.debug("Attempting to process a login request");

	// validate the provided request
	CasAuthenticationRequest a_req = CasClientImpl
		.validateClientRequest(a_req_nc);

	// ensure we have a service for this request. cas won't give us a
	// redirect unless we do.
	if (StringUtils.isEmpty(a_req.getService())) {
	    throw new AuthenticationException(
		    "Service cannot be null for processing a login!");
	}

	// Start generating the response for this method
	CasAuthenticationResponse casresponse = new CasAuthenticationResponse(
		a_req);
	casresponse.setAuthenticated(false);
	casresponse.setService(a_req.getService());

	// make sure we have credentials and this is a login attempt.
	if (StringUtils.isEmpty(a_req.getPrincipal())
		|| StringUtils.isEmpty(a_req.getCredential())) {
	    log.debug("Principal or Credential is empty.  User cannot be authenticated");
	    casresponse.setError(Constants.ERROR_NOCREDENTIALORPRINCIPAL);
	    return casresponse;
	}

	// Attempt issuing the SSO Request
	try {
	    // Create a local HttpContext for this request
	    HttpContext localContext = new BasicHttpContext();
	    CookieStore cookies = new BasicCookieStore();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookies);

	    // build HttpRequest object
	    String casServer = this.getCasServer();
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(Constants.CAS_SERVICE, a_req
		    .getService()));
	    params.add(new BasicNameValuePair(Constants.CAS_USERNAME, a_req
		    .getPrincipal()));
	    params.add(new BasicNameValuePair(Constants.CAS_PASSWORD, a_req
		    .getCredential()));
	    params.add(new BasicNameValuePair(Constants.CAS_LOGINTICKET, this
		    .getLoginTicket(casServer, a_req)));
	    params.add(new BasicNameValuePair(Constants.CAS_GATEWAY, "true"));
	    params.add(new BasicNameValuePair(Constants.CAS_EVENTID, "submit"));
	    if (StringUtils.isNotEmpty(a_req.getLogoutCallback())) {
		params.add(new BasicNameValuePair(Constants.CAS_LOGOUTCALLBACK,
			a_req.getLogoutCallback()));
	    }
	    HttpUriRequest request = this.buildRequest(Method.POST, new URI(
		    casServer + Constants.LOGIN_URL), params);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    HttpResponse response = this.getHttpClient().execute(request,
		    localContext);

	    // Set the Location header and that we were authenticated if a
	    // redirect was returned
	    int status = response.getStatusLine().getStatusCode();
	    if (status == HttpStatus.SC_MOVED_PERMANENTLY
		    || status == HttpStatus.SC_MOVED_TEMPORARILY) {
		// store any returned cookies, if we have a CAS cookie then
		// we're authenticated.
		for (Cookie c : cookies.getCookies()) {
		    casresponse.setCookie(c.getName(), c.getValue());
		}
		if (StringUtils.isNotEmpty(casresponse.getCASTGCValue())) {
		    casresponse.setAuthenticated(true);
		} else {
		    log.error("Strange: ****  No cookie after successful redirect from CAS... ***");
		    casresponse.setError(Constants.ERROR_NOCOOKIEAFTERAUTH);
		}

		// we authenticated successfully so get the location.
		Header[] headers = response
			.getHeaders(Constants.CAS_LOCATIONHEADER);
		if (headers.length > 0) {
		    casresponse.setAuthenticated(true);
		    casresponse.setLocation(headers[0].getValue());
		} else {
		    log.warn("Didn't get a header back from CAS. Strange.");
		    throw new AuthenticationException(
			    "Didn't get a header back from CAS.");
		}

		// log a success message
		if (log.isInfoEnabled()) {
		    log.info("Successfully logged in: " + a_req.getPrincipal()
			    + " for " + a_req.getService());
		}
	    } else {
		log.error("Authentication failure: After posting the CAS login we didn't receive a redirect to the specified service");
		casresponse.setAuthenticated(false);
	    }

	} catch (AuthenticationException e) {
	    throw e;
	} catch (Exception e) {
	    log.error("An exception occurred.", e);
	    throw new AuthenticationException(e);
	}

	return casresponse;
    }

    /**
     * Calls CAS with a logout request. Returns nothing if success, throws an
     * exception if there was a problem.
     * 
     * @param a_req
     * @throws AuthenticationException
     */
    public void processLogoutRequest(AuthenticationClientRequest a_req_nc)
	    throws AuthenticationException {
	log.debug("Attempting to LOGOUT");

	// validate the provided request
	CasAuthenticationRequest a_req = CasClientImpl
		.validateClientRequest(a_req_nc);

	// Short-circuit if we don't have a ticket granting cookie
	if (StringUtils.isEmpty(a_req.getCASTGCValue())) {
	    log.debug("Logout called with no CASTGC. Apparently they're already logged out.");
	    return;
	}

	// Attempt issuing the SSO Request
	try {
	    // Create a local HttpContext for this request
	    HttpContext localContext = new BasicHttpContext();
	    CookieStore cookies = new BasicCookieStore();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookies);

	    // attach the TGC
	    BasicClientCookie tgc = new BasicClientCookie(Constants.CAS_TGC,
		    a_req.getCASTGCValue());
	    tgc.setDomain(this.cookieDomain);
	    tgc.setPath(Constants.CAS_COOKIEPATH);
	    cookies.addCookie(tgc);
	    if (log.isDebugEnabled()) {
		log.debug("Additive CASTGC COOKIE: " + tgc.toString());
	    }

	    // build HttpRequest object
	    HttpUriRequest request = this.buildRequest(Method.GET,
		    new URI(this.getCasServer() + Constants.LOGOUT_URL), null);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    HttpResponse response = this.getHttpClient().execute(request,
		    localContext);

	    // log the status if debugging is enabled
	    if (log.isDebugEnabled()) {
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		    log.debug("CAS said OK, assuming our logout is successful.");
		}
	    }
	} catch (Exception e) {
	    log.error("An exception occurred. ", e);
	    throw new AuthenticationException("CAS Logout exception", e);
	}

	return;
    }

    /**
     * Handle a proxy request
     */
    public AuthenticationClientResponse processProxyRequest(
	    AuthenticationClientRequest a_req_nc)
	    throws AuthenticationException {
	log.debug("proxy request.");

	// validate the provided request
	CasAuthenticationRequest a_req = CasClientImpl
		.validateClientRequest(a_req_nc);

	// Start generating the response for this method
	CasAuthenticationResponse casresponse = new CasAuthenticationResponse(
		a_req);
	casresponse.setAuthenticated(false);
	casresponse.setService(a_req.getService());
	casresponse.setPgtUrl(a_req.getPgtUrl());

	// Attempt issuing the SSO Request
	try {
	    // build HttpRequest object
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(
		    Constants.CAS_PROXY_TARGETSERVICE_PARAM, a_req.getService()));
	    params.add(new BasicNameValuePair(Constants.CAS_PROXY_PGT_PARAM,
		    a_req.getTicket()));
	    HttpUriRequest request = this.buildRequest(Method.GET,
		    new URI(this.getCasServer() + Constants.PROXY_TICKET_URL),
		    params);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    HttpResponse response = this.getHttpClient().execute(request);

	    // check for a valid response
	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		log.debug("Received OK response from CAS");
	    } else {
		log.debug("Received NOT_OK response from CAS");
		casresponse.setError(Constants.ERROR_VALIDATIONFAILED);
	    }

	    // return the cas response either way (success or failure)
	    String content = response.getEntity().toString();
	    casresponse.setContent(content);
	    log.debug(content);
	} catch (Exception e) {
	    log.error("An exception occurred.", e);
	    throw new AuthenticationException("Failed to retrieve proxy", e);
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
    public AuthenticationClientResponse processSSORequest(
	    AuthenticationClientRequest a_req_nc)
	    throws AuthenticationException {
	log.debug("SSO request");

	// validate the provided request
	CasAuthenticationRequest a_req = CasClientImpl
		.validateClientRequest(a_req_nc);

	// Start generating the response for this method
	CasAuthenticationResponse casresponse = new CasAuthenticationResponse(
		a_req);
	casresponse.setAuthenticated(false);
	casresponse.setService(a_req.getService());

	// Short-circuit if we don't have a ticket granting cookie
	if (StringUtils.isEmpty(a_req.getCASTGCValue())) {
	    log.debug("No CASTGC. Returning.");
	    casresponse.setError(Constants.ERROR_VALIDATIONFAILED);
	    return casresponse;
	}

	// Attempt issuing the SSO Request
	try {
	    // Create a local HttpContext for this request
	    HttpContext localContext = new BasicHttpContext();
	    CookieStore cookies = new BasicCookieStore();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookies);

	    // attach an existing TGC
	    BasicClientCookie tgc = new BasicClientCookie(Constants.CAS_TGC,
		    a_req.getCASTGCValue());
	    tgc.setDomain(this.cookieDomain);
	    tgc.setPath(Constants.CAS_COOKIEPATH);
	    cookies.addCookie(tgc);
	    if (log.isDebugEnabled()) {
		log.debug("Additive CASTGC COOKIE: " + tgc.toString());
	    }

	    // build HttpRequest object
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(Constants.CAS_SERVICE, a_req
		    .getService()));
	    if (StringUtils.isNotEmpty(a_req.getLogoutCallback())) {
		params.add(new BasicNameValuePair(Constants.CAS_LOGOUTCALLBACK,
			a_req.getLogoutCallback()));
	    }
	    HttpUriRequest request = this.buildRequest(Method.GET,
		    new URI(this.getCasServer() + Constants.LOGIN_URL), params);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    HttpResponse response = this.getHttpClient().execute(request,
		    localContext);

	    // Set the Location header and that we were authenticated if a
	    // redirect was returned
	    int status = response.getStatusLine().getStatusCode();
	    if (status == HttpStatus.SC_MOVED_PERMANENTLY
		    || status == HttpStatus.SC_MOVED_TEMPORARILY) {
		Header[] headers = response
			.getHeaders(Constants.CAS_LOCATIONHEADER);
		if (headers.length > 0) {
		    casresponse.setAuthenticated(true);
		    casresponse.setLocation(headers[0].getValue());
		} else {
		    log.warn("Didn't get a header back from CAS. Strange.");
		}
	    } else {
		log.warn("CAS didn't like the TGC so we'll return a NOT authenticated because they likely expired.");
	    }
	} catch (Exception e) {
	    log.error("An exception occurred while processing an SSO request.",
		    e);
	    throw new AuthenticationException("Failed to authenticate", e);
	}

	return casresponse;
    }

    /**
     * Processes a service/proxy validation request by passing it along to CAS.
     * 
     * @param a_req
     * @return
     */
    private AuthenticationClientResponse processValidationRequest(
	    final AuthenticationClientRequest a_req_nc, final String target)
	    throws AuthenticationException {
	log.debug("service or proxy Validation request.");

	// validate the provided request
	CasAuthenticationRequest a_req = CasClientImpl
		.validateClientRequest(a_req_nc);

	// Start generating the response for this method
	CasAuthenticationResponse casresponse = new CasAuthenticationResponse(
		a_req);
	casresponse.setAuthenticated(false);
	casresponse.setService(a_req.getService());
	casresponse.setPgtUrl(a_req.getPgtUrl());

	// Attempt issuing the SSO Request
	try {
	    // build HttpRequest object
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(Constants.CAS_SERVICE, a_req
		    .getService()));
	    params.add(new BasicNameValuePair(Constants.CAS_TICKET, a_req
		    .getTicket()));
	    if (StringUtils.isNotEmpty(a_req.getPgtUrl())) {
		params.add(new BasicNameValuePair(Constants.CAS_PGT, a_req
			.getPgtUrl()));
	    }
	    HttpUriRequest request = this.buildRequest(Method.GET,
		    new URI(this.getCasServer() + target), params);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    HttpResponse response = this.getHttpClient().execute(request);

	    // check for a valid response
	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		log.debug("Received OK response from CAS");
	    } else {
		log.debug("Received NOT_OK response from CAS");
		casresponse.setError(Constants.ERROR_VALIDATIONFAILED);
	    }

	    // return the cas response either way (success or failure)
	    String content = response.getEntity().toString();
	    casresponse.setContent(content);
	    log.debug(content);
	} catch (Exception e) {
	    log.error("An exception occurred.", e);
	    throw new AuthenticationException("Failed to validate service", e);
	}

	return casresponse;
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

    /**
     * Method that generates an HttpUriRequest from the specified parameters
     * 
     * @param method
     * @param baseUri
     * @param queryParams
     * @return
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    private HttpUriRequest buildRequest(final Method method,
	    final URI baseUri, final List<? extends NameValuePair> queryParams)
	    throws URISyntaxException, UnsupportedEncodingException {
	URI uri;
	// append any specified query parameters if this is a get request
	if (method == Method.GET && queryParams != null
		&& queryParams.size() > 0) {
	    // generate new query
	    String rawQuery = baseUri.getRawQuery();
	    String query = (rawQuery != null ? rawQuery + "&" : "")
		    + URLEncodedUtils.format(queryParams, "utf-8");

	    // generate a new URI object with the new query
	    uri = URIUtils.createURI(baseUri.getScheme(), baseUri.getHost(),
		    baseUri.getPort(), baseUri.getRawPath(), query,
		    baseUri.getRawFragment());
	} else {
	    uri = baseUri;
	}

	// create request object based on the method type
	HttpUriRequest request;
	switch (method) {
	case GET:
	    request = new HttpGet(uri);
	    break;
	case POST:
	    request = new HttpPost(uri);
	    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(
		    queryParams, "UTF-8"));
	    break;
	default:
	    return null;
	}

	// Set some HttpParams for this request
	BasicHttpParams params = new BasicHttpParams();
	params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
	HttpHost proxy = this.getProxy();
	if (proxy != null) {
	    params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}
	request.setParams(params);

	// return the generated request
	return request;
    }

    /**
     * validateClientRequest - ensure we have a CasAuthenticationRequest and
     * return it casted
     * 
     * @param a_req
     * @return
     * @throws AuthenticationException
     */
    private static CasAuthenticationRequest validateClientRequest(
	    AuthenticationClientRequest a_req) throws AuthenticationException {
	// ensure we have a proper subclass for a cas request.
	if (!(a_req instanceof CasAuthenticationRequest)) {
	    throw new AuthenticationException(
		    "CasClientImpl.handleAuthenticationRequest must be called with an instance of CasAuthenticationRequest.");
	}

	return (CasAuthenticationRequest) a_req;
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
}
