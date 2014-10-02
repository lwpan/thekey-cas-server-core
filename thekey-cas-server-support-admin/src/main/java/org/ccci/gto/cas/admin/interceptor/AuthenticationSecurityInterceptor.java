package org.ccci.gto.cas.admin.interceptor;

import static org.ccci.gto.cas.admin.Constants.SESSION_AUTHENTICATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Size;

import org.apache.struts2.dispatcher.SessionMap;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import me.thekey.cas.util.AuthenticationUtil;
import org.jasig.cas.authentication.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * <b>SessionVerifyingSecurityInterceptor</b> is a Struts2 interceptor used to
 * verify access to a particular URL by inspecting the associated
 * {@link HttpSession} object. Verification is carried out as follows:
 * <p>
 * <ol>
 * <li>Compare action name against included URL's until first match is made,
 * <li>Compare action name against excluded URL's, if they exist, until first
 * match is made,
 * <li>If an include match was made, and an exclude match was not, then access
 * is authorized if the <tt>usersession</tt> object is present in the HTTP
 * session. Everything else is automatically authorized.
 * </ol>
 * 
 * @author Daniel Frett
 */
public class AuthenticationSecurityInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = -8217849379894839393L;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** List of URL patterns that should be included in the security check. */
    @Size(min = 1)
    final private ArrayList<Pattern> includes = new ArrayList<Pattern>();

    /** List of URL patterns that should be excluded in the security check. */
    final private ArrayList<Pattern> excludes = new ArrayList<Pattern>();

    /**
     * @param patterns
     *            the included actions for this {@link Interceptor}
     */
    public void setIncludedActions(final List<String> patterns) {
	this.includes.clear();
	for (final String p : patterns) {
	    this.includes.add(Pattern.compile(p));
	}
    }

    /**
     * @param patterns
     *            the excluded actions for this {@link Interceptor}
     */
    public void setExcludedActions(final List<String> patterns) {
	this.excludes.clear();
	for (final String p : patterns) {
	    this.excludes.add(Pattern.compile(p));
	}
    }

    /**
     * Verify access to the intercepted action.
     * 
     * @param invocation
     *            {@link ActionInvocation} object from Aspect.
     * 
     * @return Outcome from action.
     * 
     * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    @Override
    public String intercept(final ActionInvocation invocation) throws Exception {
	log.trace("***** Inside the SessionVerifyingSecurityInterceptor");
	// check to see if this Interceptor needs to run for the current action
	final String action = invocation.getInvocationContext().getName();
	if (matchesAny(includes, action)
		&& (excludes.size() == 0 || !matchesAny(excludes, action))) {
	    if (log.isTraceEnabled()) {
		log.trace("***** Action \"" + action
			+ "\" is matched for security verification");
	    }

	    // make sure we have a valid SessionMap
	    final Map<String, Object> session = invocation
		    .getInvocationContext().getSession();
	    if (!(session instanceof SessionMap)) {
		log.error("invalid SessionMap");
		return Action.ERROR;
	    }

	    // make sure the current user is authenticated
	    final Authentication auth = (Authentication) session
		    .get(SESSION_AUTHENTICATION);
	    final GcxUser user = auth != null ? AuthenticationUtil
		    .getUser(auth) : null;
	    if (user == null) {
		((SessionMap<String, Object>) session).invalidate();
		log.error("Unauthorized attempt to access action \"" + action
			+ "\"");
		return Action.ERROR;
	    }

	    if (log.isTraceEnabled()) {
		log.trace("***** Action: (" + action + ")");
		log.trace("***** Actual URI: ("
			+ invocation.getProxy().getNamespace() + "/" + action
			+ ".action)");
		log.trace("***** User: " + user.getUserid());
	    }
	}

	return invocation.invoke();
    }

    /**
     * Test to see if the specified value matches any of the patterns.
     * 
     * @param patterns
     *            patterns used for test.
     * @param value
     *            value to be tested for a match against patterns.
     * 
     * @return <tt>True</tt> if the value matches a pattern.
     */
    private static boolean matchesAny(final List<Pattern> patterns,
	    final String value) {
	for (final Pattern p : patterns) {
	    if (p.matcher(value).matches()) {
		return true;
	    }
	}
	return false;
    }
}
