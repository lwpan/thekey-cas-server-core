package org.ccci.gcx.idm.web.css.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ccci.gcx.idm.web.css.AbstractCssScrubber;
import org.ccci.gcx.idm.web.css.CssScrubber;
import org.ccci.gto.cas.service.css.CssValidator;

public class SimpleCssScrubberImpl extends AbstractCssScrubber implements
	CssScrubber {
    private final ArrayList<CssValidator> validators = new ArrayList<CssValidator>();

    /**
     * Retrieves css from cssUrl and checks for rule violations. If any of the
     * regex's are found it fails the css. It does not actually "scrub" or
     * remove the offending sections but rather simply returns an empty string
     * if it fails.
     * 
     * No caching. It fetches the css *every* time. Hey, its a place to start.
     */
    public String scrub(String cssUrl) {
	final String css = fetchCssContent(cssUrl);

	for (final CssValidator validator : validators) {
	    if (!validator.isValid(css)) {
		return "";
	    }
	}

	return css;
    }

    /**
     * fetch the css from the url provided.
     * 
     * @param a_cssUrl
     * @return returns the content of the response from the server or empty
     *         string if unable.
     */
    private String fetchCssContent(String url) {
	if (log.isDebugEnabled()) {
	    log.debug("Fetching CSS: " + url);
	}

	// short-circuit if a url isn't provided
	if (StringUtils.isBlank(url)) {
	    log.debug("returning no CSS.");
	    return "";
	}

	try {
	    // build HttpRequest object
	    HttpGet request = new HttpGet(url);

	    // Set some HttpParams for this request
	    final SyncBasicHttpParams params = new SyncBasicHttpParams();
	    params.setParameter(ConnRoutePNames.DEFAULT_PROXY, this.getProxy());
	    params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
		    HTTP.UTF_8);
	    request.setParams(params);

	    // execute request
	    if (log.isDebugEnabled()) {
		log.debug("HttpClient trying: " + request.getURI());
	    }
	    HttpResponse response = this.getHttpClient().execute(request);

	    // check for a valid response
	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		log.debug("Received OK response from css server");
		return EntityUtils.toString(response.getEntity());
	    } else {
		log.debug("Received NOT_OK response from css server");
	    }
	} catch (Exception e) {
	    log.error(
		    "An exception occurred so fine we'll return no css at all.",
		    e);
	}

	return "";
    }

    /**
     * @param validators
     *            the css validators to use when validating css
     */
    public void setValidators(
	    final Collection<? extends CssValidator> validators) {
	this.validators.clear();
	if (validators != null) {
	    this.validators.addAll(validators);
	}
    }
}
