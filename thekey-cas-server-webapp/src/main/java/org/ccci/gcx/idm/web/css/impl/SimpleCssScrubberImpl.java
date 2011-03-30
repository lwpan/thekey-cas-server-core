package org.ccci.gcx.idm.web.css.impl;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.ccci.gcx.idm.web.css.AbstractCssScrubber;
import org.ccci.gcx.idm.web.css.CssScrubber;

public class SimpleCssScrubberImpl extends AbstractCssScrubber implements CssScrubber 
{
	//DI
	private ArrayList<String> rules;
	
	protected static final Log log = LogFactory.getLog(SimpleCssScrubberImpl.class);

	
	/**
	 * Retrieves css from cssUrl and checks for rule violations. If any of the regex's are found
	 * it fails the css. It does not actually "scrub" or remove the offending sections but rather
	 * simply returns an empty string if it fails.
	 * 
	 * No caching. It fetches the css *every* time.  Hey, its a place to start.
	 */
	public String scrub(String cssUrl) {

		String css = fetchCssContent(cssUrl);
		
		if(isProboten(css,rules))
		{
			log.error("CSS rule failure... XSS attempt?: "+cssUrl);
			return "";
		}
		
		return css;
	}

	/**
	 * Is this css forbidden?  ie: does this css match any of our regex's?  
	 * @param css - string of the css content to search.
	 * @param a_rules
	 * @return true if the css contains forbidden content, otherwise false
	 */
	public boolean isProboten(String css, ArrayList<String> a_rules) {
		
		if(StringUtils.isBlank(css)) 
		{
			if(log.isDebugEnabled()) log.debug("isProboten received a blank css. we'll pretend thats ok then.");
			return false;
		}

		css = css.replaceAll("\\s|\\n|\\r", "");
		
		log.debug(css);
		
		try
		{
			for(String rule : a_rules)
			{
				if(log.isDebugEnabled()) log.debug("trying rule: "+rule);
				Pattern p = Pattern.compile(rule);
				if(p.matcher(css).find())
				{
					log.warn("isProboten found a rule ("+rule+") that fails the css.");
					if(log.isDebugEnabled()) log.debug("and the css is: "+css);
					return true;
				}
			}
			
		}
		catch(Exception e)
		{
			log.warn("isProboten had an exception. Perhaps a rule is incorrectly formatted?",e);
			return true;
		}
		
		return false;
		
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
	    BasicHttpParams params = new BasicHttpParams();
	    params.setParameter(ConnRoutePNames.DEFAULT_PROXY, this.getProxy());
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
	 * @return the rules
	 */
	public ArrayList<String> getRules() {
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(ArrayList<String> rules) {
		this.rules = rules;
	}

	
	
}
