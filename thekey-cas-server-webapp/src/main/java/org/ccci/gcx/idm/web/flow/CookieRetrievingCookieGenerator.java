package org.ccci.gcx.idm.web.flow;

import org.springframework.web.util.CookieGenerator;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

	/**
	 * Extends CookieGenerator to allow you to set/retrieve a cookie value from a request.
	 *  
	 * @author Ken Burcham refactored as a utility from Cas
	 *
	 */
	public final class CookieRetrievingCookieGenerator extends CookieGenerator {
	    
	    /** The maximum age the cookie should be remembered for.
	     * The default is three months (7889231 in seconds, according to Google) */
	    private int rememberMeMaxAge = 7889231;
	    
	    public void addCookie(final HttpServletRequest request, final HttpServletResponse response, final String cookieValue) {
	        
            final Cookie cookie = createCookie(cookieValue);
            cookie.setMaxAge(this.rememberMeMaxAge);
            if (isCookieSecure()) {
                cookie.setSecure(true);
            }
            response.addCookie(cookie);
        }

	    public String retrieveCookieValue(final HttpServletRequest request) {
	        final Cookie cookie = org.springframework.web.util.WebUtils.getCookie(
	            request, getCookieName());

	        return cookie == null ? null : cookie.getValue();
	    }
	    
	    public void setRememberMeMaxAge(final int maxAge) {
	        this.rememberMeMaxAge = maxAge;
	    }
	}
	

