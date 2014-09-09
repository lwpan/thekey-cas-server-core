package org.ccci.gto.cas.css.servlet;

import static org.ccci.gto.cas.css.Constants.PARAMETER_CSS_URI;
import static org.ccci.gto.cas.css.Constants.PARAMETER_DEFAULTCSS;
import static org.ccci.gto.cas.css.Constants.PARAMETER_RELOAD_CSS;

import me.thekey.cas.css.scrubber.CachingCssScrubber;
import me.thekey.cas.css.scrubber.CssScrubber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

public class CssServiceController implements Controller {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @NotNull
    private CssScrubber scrubber;

    @NotNull
    private final HashSet<URI> trustedUris = new HashSet<URI>();

    private final HashSet<String> supportedSchemes = new HashSet<String>();

    public CssServiceController() {
	supportedSchemes.add("http");
	supportedSchemes.add("https");
    }

    public void addTrustedUri(final String uri) {
        if (uri != null) {
            try {
                this.addTrustedUri(new URI(uri));
            } catch (final URISyntaxException e) {
                log.debug("error parsing uri", e);
            }
        }
    }

    public void addTrustedUri(final URI uri) {
        if (uri != null && uri.isAbsolute()) {
            trustedUris.add(uri);
        }
    }

    public void setTrustedUris(final Collection<?> uris) {
        this.trustedUris.clear();
        if (uris != null) {
            for (final Object uri : uris) {
                if (uri instanceof URI) {
                    this.addTrustedUri((URI) uri);
                } else {
                    this.addTrustedUri(uri.toString());
                }
            }
        }
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
	URI uri;
	try {
	    uri = new URI(request.getParameter(PARAMETER_CSS_URI));
	} catch (final NullPointerException e) {
	    log.debug("no CSS uri specified", e);
	    uri = null;
	} catch (final URISyntaxException e) {
	    log.debug("invalid CSS uri specified", e);
	    uri = null;
	}

	// process the uri if it is found
	if (uri != null) {
	    // only allow supported schemes
	    if (supportedSchemes.contains(uri.getScheme())) {
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

		    // css was output or there was an error, so return early
		    return null;
		}
	    } else {
		log.debug("unsupported css uri scheme for: {}", uri);
	    }
	}

	// try sending the default css
        final String defaultCss = request.getParameter(PARAMETER_DEFAULTCSS);
        if (defaultCss != null && defaultCss.length() > 0) {
            try {
                final URI baseUri = URI.create(request.getRequestURL().toString());
                final URI defaultCssUri = baseUri.resolve(defaultCss);

                // see if the default css uri is trusted
                for (final URI trustedUri : this.trustedUris) {
                    if (defaultCssUri.equals(baseUri.resolve(trustedUri))) {
                        sendDefaultImport(response, defaultCssUri);
                        break;
                    }
                }
            } catch (final Exception e) {
                // log the error
                log.debug("invalid default css value: {}", defaultCss, e);
            }
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
    private void sendDefaultImport(final HttpServletResponse response, final URI defaultCss) {
	try {
            response.getWriter().print("@import url(\"" + defaultCss.toString() + "\");");
	} catch (final IOException e) {
	    log.debug("error writing import for the default css", e);
	}
    }
}
