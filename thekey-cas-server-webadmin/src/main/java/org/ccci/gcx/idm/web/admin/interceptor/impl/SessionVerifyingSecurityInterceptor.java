package org.ccci.gcx.idm.web.admin.interceptor.impl;

import org.ccci.gcx.idm.common.struts2.support.ActionUtils;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ccci.gcx.idm.core.model.impl.GcxUser;
import org.ccci.gcx.idm.web.admin.Constants;
import org.ccci.gcx.idm.web.admin.interceptor.SecurityInterceptorException;
import org.springframework.beans.factory.InitializingBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * <b>SessionVerifyingSecurityInterceptor</b> is a Struts2 interceptor used to verify access to a 
 * particular URL by inspecting the associated {@link HttpSession} object. Verification is carried
 * out as follows:
 * <p>
 * <ol>
 * <li> Compare action name against included URL's until first match is made,
 * <li> Compare action name against excluded URL's, if they exist, until first match is made,
 * <li> If an include match was made, and an exclude match was not, then access is authorized
 *      if the <tt>usersession</tt> object is present in the HTTP session. Everything else
 *      is automatically authorized.
 * </ol>
 *
 * @author Greg Crider  Nov 6, 2008  4:56:58 PM
 */
public class SessionVerifyingSecurityInterceptor extends AbstractInterceptor implements InitializingBean
{
    private static final long serialVersionUID = -8217849379894839393L ;

    protected static final Log log = LogFactory.getLog( SessionVerifyingSecurityInterceptor.class ) ;

    /** List of URL patterns that should be included in the security check. */
    protected List<String> m_IncludedUrls = null ;
    /** List of URL patterns that should be excluded in the security check. */
    protected List<String> m_ExcludedUrls = null ;
    
    
    /**
     * @return the includedUrls
     */
    public List<String> getIncludedUrls()
    {
        return this.m_IncludedUrls ;
    }
    /**
     * @param a_includedUrls the includedUrls to set
     */
    public void setIncludedUrls( List<String> a_includedUrls )
    {
        this.m_IncludedUrls = a_includedUrls ;
    }


    /**
     * @return the excludedUrls
     */
    public List<String> getExcludedUrls()
    {
        return this.m_ExcludedUrls ;
    }
    /**
     * @param a_excludedUrls the excludedUrls to set
     */
    public void setExcludedUrls( List<String> a_excludedUrls )
    {
        this.m_ExcludedUrls = a_excludedUrls ;
    }
    
    
    /**
     * Perform initialization and verification after all properties have been set.
     */
    public void afterPropertiesSet() 
    {
        if ( ( this.m_IncludedUrls == null ) || ( this.m_IncludedUrls.size() == 0 ) ) {
            throw new IllegalArgumentException( "At least one include URL pattern must be sepcified." ) ;
        }
    }

    
    /**
     * Test to see if the specified URL matches any of the patterns.
     * 
     * @param a_Patterns URL patterns used for test.
     * @param a_URL URL to be tested for a match against patterns.
     * 
     * @return <tt>True</tt> if the URL matches a pattern.
     */
    private boolean UrlMatch( List<String> a_Patterns, String a_URL )
    {
        boolean result = false ;
        
        Iterator<String> it = a_Patterns.iterator() ;
        while( it.hasNext() && !result ) {
            String pattern = it.next() ;
            result = a_URL.matches( pattern ) ;
            if ( result ) {
                /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** \tMatch: " + pattern ) ;
            } 
        }
        
        return result ;
    }
    

    /**
     * Verify access to the intercepted action.
     * 
     * @param a_Invocation {@link ActionInvocation} object from Aspect.
     * 
     * @return Outcome from action.
     * 
     * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    @Override
    public String intercept( ActionInvocation a_Invocation ) throws Exception
    {
        GcxUser user = null ;
        boolean include = false ;
        boolean exclude = false ;
        boolean authorized = true ;
        String action = a_Invocation.getInvocationContext().getName() ;
        String actualUri = a_Invocation.getProxy().getNamespace() + "/" + action + ".action" ;

        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Inside the SessionVerifyingSecurityInterceptor" ) ;
        
        // Make sure there is an action
        if ( action == null ) { action = "" ; }
        
        // Test for includes; everything is included, unless include patterns are present
        /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Test Includes" ) ;
        include = this.UrlMatch( this.m_IncludedUrls, action ) ;
        // Test for excludes
        if ( ( this.m_ExcludedUrls != null ) && ( this.m_ExcludedUrls.size() > 0  ) ) {
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Test Excludes" ) ;
            exclude = this.UrlMatch( this.m_ExcludedUrls, action ) ;
        }
        
        // Locate the user session object, if it exists
        if ( a_Invocation.getInvocationContext().getSession() != null ) {
            user = (GcxUser)a_Invocation.getInvocationContext().getSession().get( Constants.SESSION_AUTHENTICATED_USER ) ;
        }
        
        // Verify access
        if ( include && !exclude ) {
            /*= TRACE =*/ if ( log.isTraceEnabled() ) log.trace( "***** Action \"" + action + "\" is matched for security verification"  ) ;
            authorized = ( user != null ) ;
            // Test to see if the user is even logged in
            if ( !authorized ) {
                String userid = ( user != null ) ? user.getEmail() : "?" ;
                String error = "Unauthorized access attempt to action \"" + action + "\" by " + userid ;
                /*= WARN =*/ log.warn( error ) ;
                // Invalidate the session
                ActionUtils.invalidate( ActionContext.getContext().getSession() ) ;
                // Blow out of the interceptor stack now!
                throw new SecurityInterceptorException( error ) ;
            }
        }
        
        /*= TRACE =*/ if ( log.isTraceEnabled() ) {
            log.trace( "***** Action: (" + action + ")" ) ;
            log.trace( "***** Actual URI: (" + actualUri + ")" ) ;
            log.trace( "***** UserSession: " + user ) ;
            log.trace( "***** Authorized: " + authorized ) ;
        }
        
        return a_Invocation.invoke() ;
    }

}
