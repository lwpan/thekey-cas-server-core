package org.ccci.gto.cas.css.servlet;

import static org.ccci.gto.cas.css.Constants.PARAMETER_CSS_URI;
import static org.ccci.gto.cas.css.Constants.PARAMETER_RELOAD_CSS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.ccci.gto.cas.css.scrubber.CachingCssScrubber;
import org.ccci.gto.cas.css.scrubber.CssScrubber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class CssServiceController implements Controller {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    private CssScrubber scrubber;

    @NotNull
    private String defaultCssUri;

    private final HashSet<String> supportedSchemes = new HashSet<String>();

    public CssServiceController() {
	supportedSchemes.add("http");
	supportedSchemes.add("https");
    }

    /**
     * @param defaultCssUri
     *            the defaultCssUri to set
     */
    public void setDefaultCssUri(final String defaultCssUri) {
	this.defaultCssUri = defaultCssUri;
    }

    /**
     * @param scrubber
     *            the css scrubber to use
     */
    public void setScrubber(final CssScrubber scrubber) {
	this.scrubber = scrubber;
    }

    public ModelAndView handleRequest(final HttpServletRequest request,
	    final HttpServletResponse response) {
	// set the content type for this response
	response.setContentType("text/css");

	// parse the uri to see if it's valid
	final URI uri;
	try {
	    uri = new URI(request.getParameter(PARAMETER_CSS_URI));
	} catch (final URISyntaxException e) {
	    log.debug("invalid CSS uri specified", e);
	    sendDefaultImport(response);
	    return null;
	}

	// only allow supported schemes
	if (!supportedSchemes.contains(uri.getScheme())) {
	    log.debug("unsupported css uri scheme for: " + uri.toString());
	    sendDefaultImport(response);
	    return null;
	}

	// fetch the requested CSS
	final boolean reload = StringUtils.hasText(request
		.getParameter(PARAMETER_RELOAD_CSS));
	final String css = scrubCssContent(uri, reload);

	// Output the scrubbed CSS
	if (StringUtils.hasText(css)) {
	    try {
		response.getWriter().print(css);
	    } catch (final IOException e) {
		log.debug("Error outputing CSS", e);
	    }
	} else {
	    sendDefaultImport(response);
	}

	// return null indicating no further processing is required
	return null;
    }

    /**
     * scrub css content
     * 
     * @param uri
     * @param reload
     * @return
     */
    private String scrubCssContent(final URI uri, final boolean reload) {
	if (reload && scrubber instanceof CachingCssScrubber) {
	    log.debug("reload requested");
	    ((CachingCssScrubber) scrubber).removeFromCache(uri);
	}
	return scrubber.scrub(uri);
    }

    /**
     * This method sends css that imports the default css.
     * 
     * @param response
     *            the response object to send the css rule to
     */
    private void sendDefaultImport(final HttpServletResponse response) {
	try {
	    response.getWriter().print(
		    "@import url(\"" + this.defaultCssUri + "\");");
	} catch (final IOException e) {
	    log.debug("error writing import for the default css", e);
	}
    }
}
