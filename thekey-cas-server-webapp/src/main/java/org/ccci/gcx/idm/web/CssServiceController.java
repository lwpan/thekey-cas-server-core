package org.ccci.gcx.idm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.web.css.CssScrubber;
import org.ccci.gcx.idm.web.css.impl.CachingCssScrubberImpl;
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

	protected static final Log log = LogFactory.getLog(CssServiceController.class);
	
	//DI
	private CssScrubber scrubber;
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/css");
		
		if(log.isDebugEnabled())  log.debug("determining css location");
		String css = request.getParameter(Constants.REQUESTPARAMETER_CSS);
		
		String st_reload = request.getParameter(Constants.REQUESTPARAMETER_CSSRELOAD);
		if(log.isDebugEnabled()) log.debug("reload requested? "+st_reload);
		
		//detect reload parameter.  reload=true or reload=yes should trigger a cache reload for this css url.
		boolean reload = false;
		if(StringUtils.isNotEmpty(st_reload))
		{
			if(log.isDebugEnabled()) log.debug("Reload request detected on cssurl");
			reload = true;
		}

		if(StringUtils.isBlank(css))
		{
			if(log.isDebugEnabled())  log.debug("didn't find a css parameter");
			response.getOutputStream().print("");
			return null;
		}
		
		//css = HtmlUtils.htmlEscape(scrubCssContent(css,reload));
		css = scrubCssContent(css,reload);
		response.getOutputStream().print(css);
		
		return null;
	}

	/**
	 * scrub css content
	 * @param css
	 * @param reload
	 * @return
	 * note: sort of an ugly conditional class casting but i suppose its ok.
	 */
	private String scrubCssContent(String css, boolean reload) {

		if(scrubber instanceof CachingCssScrubberImpl)
		{
			if(log.isDebugEnabled()) log.debug("Using a caching css scrubber. reload = "+reload);
			return ((CachingCssScrubberImpl)scrubber).scrub(css,reload);
		}
		return scrubber.scrub(css);
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
