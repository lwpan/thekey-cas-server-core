package org.ccci.gto.cas.css.servlet;

import static org.ccci.gto.cas.css.Constants.PARAMETER_CSS_URI;
import static org.ccci.gto.cas.css.Constants.PARAMETER_RELOAD_CSS;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ccci.gto.cas.css.scrubber.CachingCssScrubber;
import org.ccci.gto.cas.css.scrubber.CssScrubber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Receives a request for css and uses DI configured scrubber. Returns whatever
 * the scrubber gives us back. 
 * NOTE: we don't actually scrub anything in our current implementation...  We just
 * return NOTHING if we find any of the prohibited strings.
 *
 * feature: reload parameter.  reload=true or reload=yes should trigger a cache reload for this css url.
 *
 * Always returns a null ModelAndView and manually prints to the response output.
 * @author ken
 *
 */
public class CssServiceController implements Controller{
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());
	
	//DI
	private CssScrubber scrubber;
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/css");
		
		if(log.isDebugEnabled())  log.debug("determining css location");
	final String uri = request.getParameter(PARAMETER_CSS_URI);
	final boolean reload = StringUtils.hasText(request
		.getParameter(PARAMETER_RELOAD_CSS));

	if (!StringUtils.hasText(uri)) {
			if(log.isDebugEnabled())  log.debug("didn't find a css parameter");
			response.getOutputStream().print("");
			return null;
		}
		
	final String css = scrubCssContent(new URI(uri), reload);
	response.getOutputStream().print(css);
		
		return null;
	}

    /**
     * scrub css content
     * 
     * @param cssUri
     * @param reload
     * @return
     */
    private String scrubCssContent(final URI cssUri, boolean reload) {
	if (reload && scrubber instanceof CachingCssScrubber) {
	    log.debug("reload requested");
	    ((CachingCssScrubber) scrubber).removeFromCache(cssUri);
	}
	return scrubber.scrub(cssUri);
    }

	/**
	 * @return the scrubber
	 */
	public CssScrubber getScrubber() {
		return scrubber;
	}

	/**
	 * @param scrubber the scrubber to set
	 */
	public void setScrubber(CssScrubber scrubber) {
		this.scrubber = scrubber;
	}
}
