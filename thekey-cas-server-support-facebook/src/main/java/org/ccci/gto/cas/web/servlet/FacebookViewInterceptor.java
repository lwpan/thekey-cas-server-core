package org.ccci.gto.cas.web.servlet;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

public class FacebookViewInterceptor extends HandlerInterceptorAdapter {
    @NotNull
    private Map<String, String> attributes;

    @Override
    public boolean preHandle(final HttpServletRequest request,
	    final HttpServletResponse response, final Object handler) {

	// put the facebook attributes into the session
	WebUtils.setSessionAttribute(request, "facebook", this.attributes);

	// Always return true
	return true;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(final Map<String, String> attributes) {
	this.attributes = Collections.unmodifiableMap(attributes);
    }
}
