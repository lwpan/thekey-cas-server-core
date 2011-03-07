package org.ccci.gcx.idm.web.css;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.Constants;

/**
 * provides a proxied httpclient pool usable for fetching css by subclassed cssScrubbers.
 * @author ken
 *
 */
public abstract class AbstractCssScrubber
{

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
	 * prepares and returns an httpclient ready for use.
	 * @return
	 */
	protected HttpClient getHttpClient()
	{
		HttpClient client = new HttpClient();
		client.setHostConfiguration(hc);
		client.setHttpConnectionManager(hcm);
		return client;
	}

	
}
