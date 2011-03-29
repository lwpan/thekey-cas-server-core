package org.ccci.gcx.idm.web.css;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
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
import org.ccci.gcx.idm.web.Constants;

/**
 * provides a proxied httpclient pool usable for fetching css by subclassed cssScrubbers.
 * @author ken
 *
 */
public abstract class AbstractCssScrubber
{
    private HttpClient httpClient;

	private HostConfiguration hc = new HostConfiguration();
	private HttpConnectionManager hcm = new MultiThreadedHttpConnectionManager();
	private String proxyUrl  = null; 
	private int    proxyPort = Constants.DEFAULTPROXY; //default
	
	protected static final Log log = LogFactory.getLog(AbstractCssScrubber.class);

	
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
     * @return the httpClient
     */
    protected HttpClient getHttpClient() {
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
	    cm.setDefaultMaxPerRoute(10);
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
	 * prepares and returns an httpclient ready for use.
	 * @return
	 */
    @Deprecated
    protected org.apache.commons.httpclient.HttpClient getOldHttpClient() {
	org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
		client.setHostConfiguration(hc);
		client.setHttpConnectionManager(hcm);
		return client;
	}

	
}
