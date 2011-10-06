package org.ccci.gcx.idm.web.css;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.params.CoreProtocolPNames.HTTP_CONTENT_CHARSET;
import static org.apache.http.protocol.HTTP.UTF_8;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.ccci.gcx.idm.web.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * provides a proxied httpclient pool usable for fetching css by subclassed
 * cssScrubbers.
 * 
 * @author Ken Burcham, Daniel Frett
 */
public abstract class AbstractCssScrubber implements CssScrubber {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private HttpClient httpClient;
    private String proxyUrl = null;
    private int proxyPort = Constants.DEFAULTPROXY;

    public abstract String scrub(final URI uri);

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

    protected HttpHost getProxy() {
	if (StringUtils.isNotBlank(this.proxyUrl)) {
	    return new HttpHost(this.proxyUrl, this.proxyPort);
	}
	return null;
    }

    /**
     * @return the httpClient
     */
    protected HttpClient getHttpClient() {
	// Create HTTP client if client doesn't exist yet
	if (this.httpClient == null) {
	    final SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http", 80, PlainSocketFactory
		    .getSocketFactory()));
	    registry.register(new Scheme("https", 443, SSLSocketFactory
		    .getSocketFactory()));
	    final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
		    registry);
	    cm.setDefaultMaxPerRoute(10);
	    cm.setMaxTotal(100);
	    final DefaultHttpClient client = new DefaultHttpClient(cm);

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
     * fetch the css from the uri provided.
     * 
     * @param uri
     * @return returns the content of the response from the server or empty
     *         string if unable.
     */
    protected String fetchCssContent(final URI uri) {
	if (log.isDebugEnabled()) {
	    log.debug("Fetching CSS: " + uri);
	}

	try {
	    // build HttpRequest object
	    final HttpGet request = new HttpGet(uri);

	    // Set some HttpParams for this request
	    final SyncBasicHttpParams params = new SyncBasicHttpParams();
	    params.setParameter(HTTP_CONTENT_CHARSET, UTF_8);
	    request.setParams(params);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    final HttpResponse response = this.getHttpClient().execute(request);

	    // check for a valid response
	    if (response.getStatusLine().getStatusCode() == SC_OK) {
		log.debug("Received OK response from css server");
		return EntityUtils.toString(response.getEntity());
	    } else {
		log.debug("Received NOT_OK response from css server");
	    }
	} catch (final Exception e) {
	    log.error(
		    "An exception occurred so fine we'll return no css at all.",
		    e);
	}

	return "";
    }
}
